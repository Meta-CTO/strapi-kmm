package com.metacto.strapikmm.analytics

class TrackingEvent private constructor(builder: Builder) {
    val platforms = builder.platforms
    val properties = builder.properties
    val name = builder.eventName

    class Builder(val eventName: String) {
        val platforms = mutableSetOf<AnalyticsPlatform>()
        val properties = mutableMapOf<String, Any>()
        fun addProperty(key: String, value: Any): Builder {
            properties[key] = value
            return this
        }

        fun addProperties(properties: Map<String, Any>): Builder {
            this.properties.putAll(properties)
            return this
        }

        fun trackOnCleverTap(): Builder {
            platforms.add(AnalyticsPlatform.CLEVERTAP)
            return this
        }

        fun trackOnAmplitude(): Builder {
            platforms.add(AnalyticsPlatform.AMPLITUDE)
            return this
        }

        fun trackOnAppsFlyer(): Builder {
            platforms.add(AnalyticsPlatform.APPSFLYER)
            return this
        }

        fun trackOnAllAnalyticsPlatform(): Builder {
            platforms.addAll(listOf(AnalyticsPlatform.APPSFLYER, AnalyticsPlatform.CLEVERTAP, AnalyticsPlatform.AMPLITUDE))
            return this
        }

        fun build(): TrackingEvent {
            return TrackingEvent(this)
        }
    }
}