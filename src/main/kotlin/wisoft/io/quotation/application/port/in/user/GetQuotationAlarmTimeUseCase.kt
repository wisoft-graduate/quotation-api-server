package wisoft.io.quotation.application.port.`in`.user

import java.sql.Timestamp

interface GetQuotationAlarmTimeUseCase {
    fun getQuotationAlarmTime(userId: String): List<Timestamp>

    data class GetQuotationAlarmTimeResponse(
        val data: Data,
    )

    data class Data(
        val quotationAlarmTimes: List<Timestamp>,
    )
}
