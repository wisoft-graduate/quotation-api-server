package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.NotFoundException

class QuotationAlarmTimeNotFoundException(override val value: String) : NotFoundException(value = value)
