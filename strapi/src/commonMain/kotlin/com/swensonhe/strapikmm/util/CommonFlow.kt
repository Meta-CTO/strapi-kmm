package com.swensonhe.strapikmm.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Converts a Flow of elements into a CommonFlow, allowing it to be collected in a platform-agnostic way.
 *
 * @param T The type of elements emitted by the Flow.
 * @return A CommonFlow wrapping the original Flow.
 */
fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

/**
 * A common Flow that can be collected in a platform-agnostic way.
 *
 * @param T The type of elements emitted by the Flow.
 * @property origin The original Flow that this CommonFlow wraps.
 */
class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    /**
     * Collects elements from the Flow with a given callback and a specified coroutine scope.
     *
     * @param coroutineScope The coroutine scope in which to collect the Flow. For example, 'viewModelScope' on Android and 'nil' on iOS.
     * @param callback The callback to execute for each emitted element.
     */
    fun collectCommon(
        coroutineScope: CoroutineScope? = null,
        callback: (T) -> Unit
    ) {
        onEach {
            callback(it)
        }.launchIn(coroutineScope ?: CoroutineScope(Dispatchers.Main + Job()))
    }
}
