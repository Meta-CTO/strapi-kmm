package com.swensonhe.strapikmm.analytics

/**
 * The [TrackingEvent] class represents an event to be tracked by analytics services. It includes
 * event details such as platforms to track on, event properties, and the event name.
 *
 * @param builder The [Builder] instance used for creating a tracking event.
 */
class TrackingEvent private constructor(builder: Builder) {
    /**
     * The set of analytics platforms where the event should be tracked.
     */
    val platforms = builder.platforms

    /**
     * The map of properties associated with the event.
     */
    val properties = builder.properties

    /**
     * The name of the event.
     */
    val name = builder.eventName

    /**
     * The [Builder] class is used to construct a [TrackingEvent] with event details.
     *
     * @param eventName The name of the event.
     */
    class Builder(val eventName: String) {
        val platforms = mutableSetOf<AnalyticsPlatform>()
        val properties = mutableMapOf<String, Any>()

        /**
         * Add a property to the event.
         *
         * @param key The property key.
         * @param value The property value.
         * @return The [Builder] instance for method chaining.
         */
        fun addProperty(key: String, value: Any): Builder {
            properties[key] = value
            return this
        }

        /**
         * Add multiple properties to the event.
         *
         * @param properties The map of properties to add.
         * @return The [Builder] instance for method chaining.
         */
        fun addProperties(properties: Map<String, Any>): Builder {
            this.properties.putAll(properties)
            return this
        }

        /**
         * Specify that the event should be tracked on CleverTap platform.
         *
         * @return The [Builder] instance for method chaining.
         */
        fun trackOnCleverTap(): Builder {
            platforms.add(AnalyticsPlatform.CLEVERTAP)
            return this
        }

        /**
         * Specify that the event should be tracked on Amplitude platform.
         *
         * @return The [Builder] instance for method chaining.
         */
        fun trackOnAmplitude(): Builder {
            platforms.add(AnalyticsPlatform.AMPLITUDE)
            return this
        }

        /**
         * Specify that the event should be tracked on both CleverTap and Amplitude platforms.
         *
         * @return The [Builder] instance for method chaining.
         */
        fun trackOnAllAnalyticsPlatform(): Builder {
            platforms.addAll(listOf(AnalyticsPlatform.CLEVERTAP, AnalyticsPlatform.AMPLITUDE))
            return this
        }

        /**
         * Build and create the [TrackingEvent] based on the configured details.
         *
         * @return The [TrackingEvent] instance.
         */
        fun build(): TrackingEvent {
            return TrackingEvent(this)
        }
    }
}
