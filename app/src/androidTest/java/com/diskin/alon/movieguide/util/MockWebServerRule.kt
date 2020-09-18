package com.diskin.alon.movieguide.util

import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerRule : TestRule {

    companion object {

        private var url: HttpUrl? = null
        val serverUrl: HttpUrl
        get() {
            return url ?:
            throw NullPointerException("MockWebServer not started yet,url not available")
        }
    }

    private val server = MockWebServer()

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    url = server.url("/")
                    server.setDispatcher(MockWebServerDispatcher())
                    base.evaluate()
                } finally {
                    server.shutdown()
                }
            }
        }
    }
}