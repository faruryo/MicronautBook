package micronaut.book

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpStatus

@Controller("/book")
class BookController {

    @Get("/")
    fun index(): HttpStatus {
        return HttpStatus.OK
    }
}