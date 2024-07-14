package wisoft.io.quotation.application.port.`in`.user

import java.sql.Timestamp

interface CreateQuotationAlarmTimeUseCase {
    fun createQuotationAlarmTime(
        userId: String,
        request: CreateQuotationAlarmTimeRequest,
    ): String

    data class CreateQuotationAlarmTimeRequest(
        val quotationAlarmTime: Timestamp,
    )

    data class CreateQuotationAlarmTimeResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
