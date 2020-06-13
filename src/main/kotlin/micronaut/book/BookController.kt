package micronaut.book

import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import javax.validation.constraints.Size

import micronaut.book.domain.Book
import micronaut.book.repository.BookRepository
import java.util.Optional

@Controller("/book")
open class BookController(private val bookRepository: BookRepository) {

    @Get("/")
    fun readAll(pageable: Pageable): List<Book> {
        val slicedBook = bookRepository.list(Pageable.from(pageable.number, pageable.size))
        return slicedBook.content
    }

    @Get("/{id}")
    fun readById(id: Long): Book? {
        return bookRepository.findById(id).orElse(null)
    }

    @Post("/")
    open fun create(@Size(max = 1024) @Body book: Book): HttpStatus {
        bookRepository.save(book)
        return HttpStatus.CREATED
    }

    @Put("/{id}")
    open fun updateById(id: Long, @Size(max = 1024) @Body newBook: Book): HttpStatus {
        val updatingBook = bookRepository.findById(id).orElse(null) ?: return HttpStatus.NOT_FOUND

        updatingBook.title = newBook.title
        updatingBook.author = newBook.author
        bookRepository.update(updatingBook)
        return HttpStatus.OK
    }

    @Delete("/{id}")
    open fun delete(id: Long): HttpStatus {
        bookRepository.deleteById(id)
        return HttpStatus.OK
    }
}