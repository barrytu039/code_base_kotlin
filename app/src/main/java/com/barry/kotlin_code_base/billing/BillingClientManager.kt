package com.barry.kotlin_code_base.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.*
import com.barry.kotlin_code_base.SingleLiveEvent
import java.util.ArrayList
import java.util.HashMap

class BillingClientManager constructor(private val app: Application) :
    LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    ProductDetailsResponseListener, PurchasesResponseListener, DefaultLifecycleObserver {
    public enum class GoogleIapEvent {
        CONNECTION_ERROR,
        NON_CONNECTION,
        PRODUCT_ALREADY_OWN,
        DEVELOPER_ERROR,
        QUERY_PRODUCT_ERROR,
        DISCONNECTED,
        QUERY_PURCHASE_HISTORY_ERROR,
        QUERY_ACTIVE_PURCHASE_ERROR,
        USER_CANCEL,
        PURCHASE_SUBSCRIPTION_ERROR,
        SERVICE_DISCONNECTED
    }

    var googleIapEventSingleLiveEvent = SingleLiveEvent<GoogleIapEvent>()

    var purchaseUpdateEvent = SingleLiveEvent<List<Purchase>?>()

    var activePurchases = MutableLiveData<List<Purchase>?>().apply {
        value = null
    }
    //
    var productIdWithProductDetailsLiveData = MutableLiveData<Map<String, ProductDetails>>()

    var listProductId: MutableList<String> = ArrayList()

    private lateinit var billingClient: BillingClient
    // billing client connect state
    private var _isConnectedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isConnectingLiveData: LiveData<Boolean> get() = _isConnectedLiveData
    var isConnecting = false
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        connect()
    }

    fun connect() {
        if (isConnecting) return
        // Create a new BillingClient in onCreate().
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(app)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()
        if (!billingClient.isReady) {
            try {
                billingClient.startConnection(this)
                isConnecting = true
            } catch (e: IllegalStateException) {
                googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.CONNECTION_ERROR)
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        destroy()
    }

    private fun destroy() {
        listProductId.clear()
        if (::billingClient.isInitialized) {
            if (billingClient.isReady) {
                // BillingClient can only be used once.
                // After calling endConnection(), we must create a new BillingClient.
                billingClient.endConnection()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.DISCONNECTED)
        _isConnectedLiveData.postValue(false)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        isConnecting = false
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.
            _isConnectedLiveData.postValue(true)
        } else {
            googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.CONNECTION_ERROR)
            _isConnectedLiveData.postValue(false)
        }
    }

    suspend fun queryPurchaseHistorySync(): BillingResultEntity<List<PurchaseHistoryRecord>?> {
        if (!isBillingClientEnable()) return BillingResultEntity.ResultError(
            GoogleIapEvent.NON_CONNECTION,
            "billing client no connection"
        )
        val param = QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val result = billingClient.queryPurchaseHistory(param)
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            BillingResultEntity.ResultSuccess(result.purchaseHistoryRecordList)
        } else {
            BillingResultEntity.ResultError(
                GoogleIapEvent.QUERY_PURCHASE_HISTORY_ERROR,
                result.billingResult.debugMessage
            )
        }
    }

    @Synchronized
    fun queryPurchaseHistoryAsync() {
        if (!isBillingClientEnableWithConnectionEvent()) return
        val param = QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        billingClient.queryPurchaseHistoryAsync(param
        ) { result, recordList ->
        }
    }

    suspend fun queryProductDetailsWithProductIdSync(listProductId: List<String>): BillingResultEntity<List<ProductDetails>?> {
        val newIdList = mutableListOf<String>().apply {
            addAll(listProductId)
        }
        this.listProductId.clear()
        this.listProductId.addAll(newIdList)
        if (!isBillingClientEnable()) return BillingResultEntity.ResultError(
            GoogleIapEvent.NON_CONNECTION,
            "billing client no connection"
        )

        val productList : MutableList<QueryProductDetailsParams.Product> = mutableListOf()

        listProductId.forEach {
            val productIdParams = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            productList.add(productIdParams)
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)
        val result = billingClient.queryProductDetails(params.build())
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            BillingResultEntity.ResultSuccess(result.productDetailsList)
        } else {
            BillingResultEntity.ResultError(
                GoogleIapEvent.QUERY_PRODUCT_ERROR,
                result.billingResult.debugMessage
            )
        }
    }

    @Synchronized
    fun queryProductDetailsWithProductIdAsync(listProductId: List<String>) {
        val newIdList = mutableListOf<String>().apply {
            addAll(listProductId)
        }
        this.listProductId.clear()
        this.listProductId.addAll(newIdList)
        if (!isBillingClientEnableWithConnectionEvent()) return

        val productList : MutableList<QueryProductDetailsParams.Product> = mutableListOf()

        listProductId.forEach {
            val productIdParams = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            productList.add(productIdParams)
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)
        billingClient.queryProductDetailsAsync(params.build(), this)
    }

    suspend fun queryActivePurchaseSync(): BillingResultEntity<List<Purchase>?> {
        if (!isBillingClientEnable()) return BillingResultEntity.ResultError(
            GoogleIapEvent.NON_CONNECTION,
            "billing client no connection"
        )
        val param = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val result = billingClient.queryPurchasesAsync(param)
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            BillingResultEntity.ResultSuccess(result.purchasesList)
        } else {
            BillingResultEntity.ResultError(
                GoogleIapEvent.QUERY_ACTIVE_PURCHASE_ERROR,
                result.billingResult.debugMessage
            )
        }
    }

    fun queryActivePurchasesAsync() {
        if (!isBillingClientEnableWithConnectionEvent()) return
        val param = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        billingClient.queryPurchasesAsync(param, this)
    }

    private fun processActivePurchases(purchasesList: List<Purchase>?) {
        if (purchasesList != null) {
            activePurchases.postValue(purchasesList)
        } else {
            googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.QUERY_PRODUCT_ERROR)
        }
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        if (!isBillingClientEnableWithConnectionEvent()) return
        billingClient.launchBillingFlow(activity, params)
    }

    override fun onQueryPurchasesResponse(billingResult: BillingResult, list: List<Purchase>) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            processActivePurchases(list)
        } else {
            googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.QUERY_ACTIVE_PURCHASE_ERROR)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, String.format("onPurchasesUpdated: %s %s", responseCode, debugMessage))
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> if (purchases == null) {
                Log.d(TAG, "onPurchasesUpdated: null purchase list")
                processActivePurchases(null)
                purchaseUpdateEvent.postValue(null)
            } else {
                purchaseUpdateEvent.postValue(purchases)
                processActivePurchases(purchases)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.USER_CANCEL)
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // already own
                googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.PRODUCT_ALREADY_OWN)
            }
            else -> {
                googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.PURCHASE_SUBSCRIPTION_ERROR)
            }
        }
    }

    companion object {
        private const val TAG = "BillingLifecycle"
    }

    override fun onProductDetailsResponse(billingResult: BillingResult, skuDetailsList: MutableList<ProductDetails>) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                val expectedSkuDetailsCount = listProductId.size
                if (skuDetailsList == null) {
                    productIdWithProductDetailsLiveData.postValue(emptyMap())
                    Log.e(
                        TAG, "onSkuDetailsResponse: " +
                                "Expected " + expectedSkuDetailsCount + ", " +
                                "Found null SkuDetails. " +
                                "Check to see if the SKUs you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    val newSkusDetailList: MutableMap<String, ProductDetails> = HashMap()
                    for (skuDetails in skuDetailsList) {
                        newSkusDetailList[skuDetails.productId] = skuDetails
                    }
                    productIdWithProductDetailsLiveData.postValue(newSkusDetailList)
                    val skuDetailsCount = newSkusDetailList.size
                    if (skuDetailsCount == expectedSkuDetailsCount) {
                        Log.i(TAG, "onSkuDetailsResponse: Found $skuDetailsCount SkuDetails")
                    } else {
                        Log.e(
                            TAG, "onSkuDetailsResponse: " +
                                    "Expected " + expectedSkuDetailsCount + ", " +
                                    "Found " + skuDetailsCount + " SkuDetails. " +
                                    "Check to see if the SKUs you requested are correctly published " +
                                    "in the Google Play Console."
                        )
                    }
                }
                return
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE, BillingClient.BillingResponseCode.BILLING_UNAVAILABLE, BillingClient.BillingResponseCode.ITEM_UNAVAILABLE, BillingClient.BillingResponseCode.DEVELOPER_ERROR, BillingClient.BillingResponseCode.ERROR -> {
                Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(
                    TAG,
                    "onSkuDetailsResponse: $responseCode $debugMessage"
                )
            }
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED, BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED, BillingClient.BillingResponseCode.ITEM_NOT_OWNED ->{
                Log.wtf(
                    TAG, "onSkuDetailsResponse: $responseCode $debugMessage"
                )
            }
            else -> Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
        }
        googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.QUERY_PRODUCT_ERROR)
    }

    private fun isBillingClientEnableWithConnectionEvent(): Boolean {
        if (!::billingClient.isInitialized) {
            googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.NON_CONNECTION)
            return false
        }
        if (!billingClient.isReady) {
            googleIapEventSingleLiveEvent.postValue(GoogleIapEvent.NON_CONNECTION)
            return false
        }
        return true
    }

    private fun isBillingClientEnable(): Boolean {
        if (!::billingClient.isInitialized) {
            return false
        }
        if (!billingClient.isReady) {
            return false
        }
        return true
    }
}

sealed class BillingResultEntity<out T> {
    data class ResultSuccess<out T>(val data: T) : BillingResultEntity<T>()
    data class ResultError(val type: BillingClientManager.GoogleIapEvent, val msg: String) : BillingResultEntity<Nothing>()
}