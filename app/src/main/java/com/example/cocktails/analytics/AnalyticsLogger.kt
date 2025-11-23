package com.example.cocktails.analytics

import android.util.Log

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any>? = null)
}

class LogcatAnalyticsLogger : AnalyticsLogger {
    companion object {
        private const val TAG = "CocktailAppAnalytics"
    }

    override fun logEvent(eventName: String, params: Map<String, Any>?) {
        val paramsString = params?.entries?.joinToString(", ") { "${it.key}=${it.value}" } ?: "no_params"
        Log.d(TAG, "Event: $eventName, Params: [$paramsString]")
    }
}
