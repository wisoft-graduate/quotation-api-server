package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.NotFoundException

class LikeNotFoundException(override val value: String) : NotFoundException(value = value)
