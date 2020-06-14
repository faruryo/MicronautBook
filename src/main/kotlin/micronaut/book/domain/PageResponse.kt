package micronaut.book.domain

data class PageResponse<T>(
        var count: Long,
        var results: List<T>)