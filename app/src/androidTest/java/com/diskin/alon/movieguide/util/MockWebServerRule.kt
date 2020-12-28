package com.diskin.alon.movieguide.util

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerRule : TestRule {
    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    NetworkUtil.initServer()
                    base.evaluate()
                } finally {
                    NetworkUtil.server.shutdown()
                }
            }
        }
    }
}