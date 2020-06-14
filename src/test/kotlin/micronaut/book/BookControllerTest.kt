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
import io.kotlintest.data.forall
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import io.micronaut.core.type.Argument
import io.micronaut.http.client.exceptions.HttpClientResponseException
import micronaut.book.domain.Book
import micronaut.book.domain.PageResponse

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
            response.body.get().shouldMatchJson("{\"count\":0}")
        }

        "check POST /book データ登録" {
            val book = Book(id = 0, title = "title", author = "author")
            val response = client.toBlocking().exchange(HttpRequest.POST("/book", book), String::class.java)

            response.status shouldBe HttpStatus.CREATED
        }

        "check GET /book データ1件" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book"), String::class.java)

            response.status shouldBe HttpStatus.OK
            response.body.get().shouldMatchJson("{\"count\":1,\"results\":[{\"id\":1,\"title\":\"title\",\"author\":\"author\"}]}")
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
            readResponse.body.get().shouldMatchJson("{\"count\":1,\"results\":[{\"id\":1,\"title\":\"PUT /book/{id} 存在する場合 title\",\"author\":\"PUT /book/{id} 存在する場合 author\"}]}")
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
            readResponse.body.get().shouldMatchJson("{\"count\":0}")
        }

        "check DELETE /book/{id} 存在しない場合" {
            val response = client.toBlocking().exchange(HttpRequest.DELETE<String>("/book/0"), String::class.java)

            response.status shouldBe HttpStatus.OK
        }

        "check POST /book データ登録3件" {
            forall(
                    row(Book(id = 0, title = "A", author = "3")),
                    row(Book(id = 0, title = "B", author = "2")),
                    row(Book(id = 0, title = "C", author = "1"))
            ){ book: Book ->
                val response = client.toBlocking().exchange(HttpRequest.POST("/book", book), String::class.java)

                response.status shouldBe HttpStatus.CREATED
            }
        }

        "check GET /book page/sort試験 sort=id ASC" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book?size=1&page=0&sort=id"), Argument.of(PageResponse::class.java, Book::class.java))
            val book: Book = response.body.get().results[0] as Book
            response.status shouldBe HttpStatus.OK
            book.title shouldBe "A"
            response.body.get().results.size shouldBe 1
            response.body.get().count shouldBe 3
        }

        "check GET /book page/sort試験 sort=idはASC 2ページ目" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book?size=1&page=1&sort=id"), Argument.of(PageResponse::class.java, Book::class.java))
            val book: Book = response.body.get().results[0] as Book
            response.status shouldBe HttpStatus.OK
            book.title shouldBe "B"
            response.body.get().results.size shouldBe 1
            response.body.get().count shouldBe 3
        }

        "check GET /book page/sort試験 sort=id,desc" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book?size=1&page=0&sort=id,desc"), Argument.of(PageResponse::class.java, Book::class.java))
            val book: Book = response.body.get().results[0] as Book
            response.status shouldBe HttpStatus.OK
            book.title shouldBe "C"
            response.body.get().results.size shouldBe 1
            response.body.get().count shouldBe 3
        }

        "check GET /book page/sort試験 sort=author ASC" {
            val response = client.toBlocking().exchange(HttpRequest.GET<String>("/book?size=1&page=0&sort=author"), Argument.of(PageResponse::class.java, Book::class.java))
            val book: Book = response.body.get().results[0] as Book
            response.status shouldBe HttpStatus.OK
            book.title shouldBe "C"
            response.body.get().results.size shouldBe 1
            response.body.get().count shouldBe 3
        }
    }
}