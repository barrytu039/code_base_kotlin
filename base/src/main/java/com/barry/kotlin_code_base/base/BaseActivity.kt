package com.barry.kotlin_code_base.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.barry.kotlin_code_base.R

open class BaseActivity : AppCompatActivity() {
    fun <T> lazyExtra(key : String, default : T) : Lazy<T> {
        return lazy {
            when(default) {
                is String -> intent.getStringExtra(key) as T ?: default
                is Int -> intent.getIntExtra(key, default) as T
                else -> throw IllegalArgumentException("wrong type")
            }
        }
    }
}