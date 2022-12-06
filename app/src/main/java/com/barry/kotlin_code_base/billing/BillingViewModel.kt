package com.barry.kotlin_code_base.billing

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.barry.kotlin_code_base.SingleLiveEvent

class BillingViewModelFactory (
    private val owner: SavedStateRegistryOwner,
    private val application: Application
): AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, state: SavedStateHandle) =
        BillingViewModel(state, application) as T
}

class BillingViewModel(private val savedStateHandle: SavedStateHandle, private val application: Application) : ViewModel() {

    private  val billingClientManager: BillingClientManager = BillingClientManager((application))

    val isBillingClientConnectLiveData: LiveData<Boolean> = billingClientManager.isConnectingLiveData

    val googleIapSingleEvent: SingleLiveEvent<BillingClientManager.GoogleIapEvent> = billingClientManager.googleIapEventSingleLiveEvent

    val idWithProductDetailMayLiveData: LiveData<Map<String, ProductDetails>> = billingClientManager.productIdWithProductDetailsLiveData

    val activePurchaseActive: LiveData<List<Purchase>?> = billingClientManager.activePurchases

    val purchaseUpdateSingleLiveEvent: SingleLiveEvent<List<Purchase>?> = billingClientManager.purchaseUpdateEvent


    fun bindBillingClient(lifecycle: Lifecycle) {
        lifecycle.addObserver(billingClientManager)
    }

    fun connectBillingServer() {
        billingClientManager.connect()
    }

    fun queryProductDetail(listProductId: List<String>) {
        billingClientManager.queryProductDetailsWithProductIdAsync(listProductId)
    }

    fun queryActivePurchaseAsync() {
        billingClientManager.queryActivePurchasesAsync()
    }

    suspend fun queryActivePurchaseSync(): BillingResultEntity<List<Purchase>?> {
        return billingClientManager.queryActivePurchaseSync()
    }

    suspend fun queryPurchaseHistorySync(): BillingResultEntity<List<PurchaseHistoryRecord>?> {
        return billingClientManager.queryPurchaseHistorySync()
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, originalPurchase: Purchase? = null) {
        val offerToken = productDetails.subscriptionOfferDetails!![0].offerToken
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).setOfferToken(offerToken).build()
        val accountId = "" // todo: put identify data to order
        val billingBuilder = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productDetailsParams)).setObfuscatedAccountId(accountId)
        originalPurchase?.let {
            if (productDetails.productId == originalPurchase.products[0]) {
                // already own same subscription
                googleIapSingleEvent.postValue(BillingClientManager.GoogleIapEvent.PRODUCT_ALREADY_OWN)
                return
            }
            val subscriptionUpdateParams = BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldPurchaseToken(it.purchaseToken).build()
            billingBuilder.setSubscriptionUpdateParams(subscriptionUpdateParams)
        }
        billingClientManager.launchBillingFlow(activity, billingBuilder.build())
    }
}