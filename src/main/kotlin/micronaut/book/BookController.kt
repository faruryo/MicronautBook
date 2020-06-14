package micronaut.book

import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import javax.validation.constraints.Size

import micronaut.book.domain.Book
import micronaut.book.domain.PageResponse
import micronaut.book.repository.BookRepository
import javax.validation.Valid

@Controller("/book")
open class BookController(private val bookRepository: BookRepository) {

    @Get("/{?pageable*}")
    open fun readAll(@Valid pageable: Pageable): PageResponse<Book> {
        val slicedBook = bookRepository.list(pageable)
        val count = bookRepository.count()
        return PageResponse(count=count, results=slicedBook.content)
    }

    @Get("/{id}")
    open fun readById(@PathVariable id: Long): Book? {
        return bookRepository.findById(id).orElse(null)
    }

    @Post("/")
    open fun create(@Size(max = 1024) @Body book: Book): HttpStatus {
        bookRepository.save(book)
        return HttpStatus.CREATED
    }

    @Put("/{id}")
    open fun updateById(@PathVariable id: Long, @Size(max = 1024) @Body newBook: Book): HttpStatus {
        val updatingBook = bookRepository.findById(id).orElse(null) ?: return HttpStatus.NOT_FOUND

        updatingBook.title = newBook.title
        updatingBook.author = newBook.author
        bookRepository.update(updatingBook)
        return HttpStatus.OK
    }

    @Delete("/{id}")
    open fun delete(@PathVariable id: Long): HttpStatus {
        bookRepository.deleteById(id)
        return HttpStatus.OK
    }
}