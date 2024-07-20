package wisoft.io.quotation.application.port.`in`.user

interface CreateQuotationAlarmTimeUseCase {
    fun createQuotationAlarmTime(
        userId: String,
        request: CreateQuotationAlarmTimeRequest,
    ): String

    data class CreateQuotationAlarmTimeRequest(
        val quotationAlarmHour: Int,
        val quotationAlarmMinute: Int,
    )

    data class CreateQuotationAlarmTimeResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
