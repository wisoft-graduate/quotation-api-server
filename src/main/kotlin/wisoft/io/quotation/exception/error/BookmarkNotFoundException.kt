package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.NotFoundException

class BookmarkNotFoundException(override val value: String) : NotFoundException(value = value)
