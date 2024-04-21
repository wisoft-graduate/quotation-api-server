package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.UnauthorizedException

class UnauthorizedUserException(override val value: String) : UnauthorizedException(value = value)
