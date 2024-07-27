package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.InternalServerErrorException

class S3ObjectException(override val value: String) : InternalServerErrorException(value = value)
