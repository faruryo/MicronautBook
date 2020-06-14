package micronaut.book

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.test.annotation.MicronautTest
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.shouldThrow
import io.micronaut.http.client.exceptions.HttpClientResponseException
import micronaut.book.domain.Book

@MicronautTest
class BookControllerTest(ctx: ApplicationContext): StringSpec() {

    private val embeddedServer = autoClose(
            ApplicationContext.run(EmbeddedServer::class.java)
    )

    private val client = autoClose(
            embeddedServer.applicationContext.createBean(RxHttpClient::class.java, embeddedServer.url)
    )

    init {
        "check environment" {
            ctx.environment.activeNames shouldContain "test"
        }

        "test the server is running" {
            embeddedServer.isRunning shouldBe true
        }

        "BookController server" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            response.status shouldBe HttpStatus.OK
        }

        "check GET /book データ0件" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            response.status shouldBe HttpStatus.OK
            response.body.get().shouldMatchJson("[]")
        }

        "check POST /book データ登録" {
            val book = Book(id = 0, title = "title", author = "author")
            val response = client.toBlocking().exchange(HttpRequest.POST("/book", book), String::class.java)

            response.status shouldBe HttpStatus.CREATED
        }

        "check GET /book データ1件" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            response.status shouldBe HttpStatus.OK
            response.body.get().shouldMatchJson("[{\"id\":1,\"title\":\"title\",\"author\":\"author\"}]")
        }

        "check GET /book/{id} 存在する場合" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book/1"), String::class.java)

            response.status shouldBe HttpStatus.OK
            response.body.get().shouldMatchJson("{\"id\":1,\"title\":\"title\",\"author\":\"author\"}")
        }

        "check GET /book/{id} 存在しない場合" {
            val exception = shouldThrow<HttpClientResponseException> {
                client.toBlocking().exchange(HttpRequest.GET<String>("/book/0"), String::class.java)
            }
            exception.response.status shouldBe HttpStatus.NOT_FOUND
        }

        "check PUT /book/{id} 存在する場合" {
            val book = Book(id = 0, title = "PUT /book/{id} 存在する場合 title", author = "PUT /book/{id} 存在する場合 author")
            val updateResponse = client.toBlocking().exchange(HttpRequest.PUT("/book/1", book), String::class.java)

            updateResponse.status shouldBe HttpStatus.OK

            val readResponse = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            readResponse.status shouldBe HttpStatus.OK
            readResponse.body.get().shouldMatchJson("[{\"id\":1,\"title\":\"PUT /book/{id} 存在する場合 title\",\"author\":\"PUT /book/{id} 存在する場合 author\"}]")
        }

        "check PUT /book/{id} 存在しない場合" {
            val book = Book(id = 0, title = "title", author = "author")
            val exception = shouldThrow<HttpClientResponseException> {
                client.toBlocking().exchange(HttpRequest.PUT("/book/0", book), String::class.java)
            }
            exception.response.status shouldBe HttpStatus.NOT_FOUND
        }

        "check DELETE /book/{id} 存在する場合" {
            val response = client.toBlocking().exchange(HttpRequest.DELETE<String>("/book/1"), String::class.java)

            response.status shouldBe HttpStatus.OK

            val readResponse = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            readResponse.status shouldBe HttpStatus.OK
            readResponse.body.get().shouldMatchJson("[]")
        }

        "check DELETE /book/{id} 存在しない場合" {
            val response = client.toBlocking().exchange(HttpRequest.DELETE<String>("/book/0"), String::class.java)

            response.status shouldBe HttpStatus.OK
        }
    }
}