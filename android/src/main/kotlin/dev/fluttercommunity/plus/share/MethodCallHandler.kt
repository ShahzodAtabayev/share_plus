package dev.fluttercommunity.plus.share

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** Handles the method calls for the plugin.  */
internal class MethodCallHandler(
    private val share: Share,
    private val manager: ShareSuccessManager,
) : MethodChannel.MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        expectMapArguments(call)

        // Force no-result mode to avoid blocking the Dart side while the user
        // is in a target app (Telegram often doesn't return a result promptly).
        val isWithResult = false

        when (call.method) {
            "share" -> {
                share.share(
                    arguments = call.arguments<Map<String, Any>>()!!,
                    withResult = isWithResult,
                ) { shareResult ->
                    // Invoked on the platform thread once the share sheet has been
                    // launched (success) or preparation failed (failure).
                    shareResult.fold(
                        onSuccess = { success(isWithResult, result) },
                        onFailure = { e ->
                            manager.clear()
                            result.error("Share failed", e.message, e)
                        },
                    )
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun success(
        isWithResult: Boolean,
        result: MethodChannel.Result
    ) {
        if (!isWithResult) {
            result.success("dev.fluttercommunity.plus/share/unavailable")
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun expectMapArguments(call: MethodCall) {
        require(call.arguments is Map<*, *>) { "Map arguments expected" }
    }
}
