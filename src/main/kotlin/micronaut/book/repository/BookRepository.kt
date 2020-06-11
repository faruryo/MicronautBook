package micronaut.book.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Slice
import io.micronaut.data.repository.CrudRepository

import micronaut.book.domain.Book

@Repository
interface BookRepository : CrudRepository<Book, Long> {
    fun list(pageable: Pageable): Slice<Book>
}