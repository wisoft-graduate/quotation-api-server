package wisoft.io.quotation.exception.error

import wisoft.io.quotation.exception.error.http.DuplicateException

class QuotationAlarmTimeDuplicateException(override val value: String) : DuplicateException(value = value)
