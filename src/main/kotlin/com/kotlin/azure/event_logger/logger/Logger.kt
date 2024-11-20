package com.kotlin.azure.event_logger.logger

typealias Properties = Map<String, Any?>
typealias Metrics = Map<String, Double?>

interface Logger {
    fun verbose(message: () -> Any)
    fun verbose(properties: Properties, message: () -> Any)
    fun info(message: () -> Any)
    fun info(properties: Properties, message: () -> Any)
    fun warn(message: () -> Any)
    fun warn(properties: Properties, message: () -> Any)
    fun error(message: () -> Any)
    fun error(properties: Properties, message: () -> Any)
    fun critical(message: () -> Any)
    fun critical(properties: Properties, message: () -> Any)
    fun event(name: String, properties: Properties? = null, metrics: Metrics? = null)
    fun metric(name: String, value: Double)
}
