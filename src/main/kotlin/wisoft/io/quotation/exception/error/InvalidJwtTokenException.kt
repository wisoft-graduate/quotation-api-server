package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.ForbiddenException

class InvalidJwtTokenException (override val value: String): ForbiddenException(value = value)