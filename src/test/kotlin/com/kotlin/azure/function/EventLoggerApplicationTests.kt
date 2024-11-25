package com.kotlin.azure.function

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["APPINSIGHTS_INSTRUMENTATIONKEY=my-key"])
class EventLoggerApplicationTests {

	@Test
	fun contextLoads() {
	}
}
