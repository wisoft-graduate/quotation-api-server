package wisoft.io.quotation.application.port.`in`.user

interface PatchQuotationAlarmTimeUseCase {
    fun patchQuotationAlarmTime(
        userId: String,
        request: PatchQuotationAlarmTimeRequest,
    ): String

    data class PatchQuotationAlarmTimeRequest(
        val quotationAlarmHour: Int,
        val quotationAlarmMinute: Int,
    )

    data class PatchQuotationAlarmTimeResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
