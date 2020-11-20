package com.diskin.alon.common.presentation

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.Test

/**
 * [ModelDispatcher] unit test class.
 */
class ModelDispatcherTest {

    // Test subject
    private lateinit var dispatcher: ModelDispatcher

    @Test
    fun executeKnownRequestWithExistingUseCaseAndMapper() {
        // Given an initialized dispatcher, containing a use case and mapper,for a
        // known model request
        val useCase = object : UseCase<Int,String> {
            override fun execute(param: Int): String {
                return param.toString()
            }

        }
        val mapper = object : Mapper<String, String> {
            override fun map(source: String): String {
                return source.plus(" yes!!")
            }

        }
        val myRequest = object : ModelRequest<Int,String>(30) {}
        val map = HashMap<Class<out ModelRequest<*,*>>,Pair<UseCase<*,*>, Mapper<*, *>?>>()
        map[myRequest::class.java] = Pair(useCase,mapper)
        dispatcher = ModelDispatcher(map)

        // When dispatcher executes to serve a request
        val modelResult = dispatcher.execute(myRequest)
        println(modelResult)

        // Then dispatcher should return mapped use case result
        val expectedResult = "30 yes!!"
        assertThat(modelResult).isEqualTo(expectedResult)
    }

    @Test
    fun executeKnownRequestWithExistingUseCaseAndWithoutMapper() {
        // Given an initialized dispatcher, containing a use case without mapper,for a
        // known model request
        val useCase = object : UseCase<Int,String> {
            override fun execute(param: Int): String {
                return param.toString()
            }

        }
        val myRequest = object : ModelRequest<Int,String>(30) {}
        val map = HashMap<Class<out ModelRequest<*,*>>,Pair<UseCase<*,*>, Mapper<*, *>?>>()
        map[myRequest::class.java] = Pair(useCase,null)
        dispatcher = ModelDispatcher(map)

        // When dispatcher executes to serve a request
        val modelResult = dispatcher.execute(myRequest)
        println(modelResult)

        // Then dispatcher should return use case result
        val expectedResult = "30"
        assertThat(modelResult).isEqualTo(expectedResult)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenExecutingUnknownRequest() {
        // Given an initialized dispatcher
        val map = HashMap<Class<out ModelRequest<*,*>>,Pair<UseCase<*,*>, Mapper<*, *>?>>()
        map[mockkClass(ModelRequest::class)::class.java] = mockk()

        dispatcher = ModelDispatcher(map)

        // When dispatcher execute an unknown request
        val myRequest = object : ModelRequest<Int,String>(30) {}
        dispatcher.execute(myRequest)

        // Then dispatcher should throw an IllegalArgumentException
    }
}