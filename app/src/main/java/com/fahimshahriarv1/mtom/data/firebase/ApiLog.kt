package com.fahimshahriarv1.mtom.data.firebase

import android.util.Log

/**
 * Single tag for every remote call so the whole request/response trail can be
 * followed with: adb logcat -s MtoM-API
 */
const val API_TAG = "MtoM-API"

/** Logged right before a remote call leaves the app. */
fun logApiRequest(api: String, params: String = "") {
    Log.d(API_TAG, "→ REQUEST  $api${if (params.isEmpty()) "" else " { $params }"}")
}

/** Logged when a remote call comes back successfully. */
fun logApiResponse(api: String, response: String = "") {
    Log.d(API_TAG, "← RESPONSE $api${if (response.isEmpty()) "" else " { $response }"}")
}

/** Logged when a remote call fails. */
fun logApiError(api: String, error: Throwable?) {
    Log.e(
        API_TAG,
        "✕ FAILED   $api { ${error?.javaClass?.simpleName}: ${error?.message} }"
    )
}
