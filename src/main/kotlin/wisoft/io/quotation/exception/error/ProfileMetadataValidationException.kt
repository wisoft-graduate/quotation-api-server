package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.BadRequestException

class ProfileMetadataValidationException(override val value: String) : BadRequestException(value = value)
