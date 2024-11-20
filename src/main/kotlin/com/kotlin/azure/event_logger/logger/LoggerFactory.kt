package com.kotlin.azure.event_logger.logger

import java.util.function.Function
import kotlin.reflect.KClass

interface LoggerFactory : Function<KClass<*>, Logger> {

    companion object {

        private lateinit var loggerFactory: LoggerFactory

        @JvmStatic
        fun setLoggerFactory(loggerFactory: LoggerFactory) {
            Companion.loggerFactory = loggerFactory
        }

        @JvmStatic
        fun getLogger(kClass: KClass<*>) = loggerFactory.apply(kClass)

    }

}
