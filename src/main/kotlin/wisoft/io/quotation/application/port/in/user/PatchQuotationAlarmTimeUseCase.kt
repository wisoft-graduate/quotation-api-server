package wisoft.io.quotation.application.port.`in`.user

import java.sql.Timestamp

interface PatchQuotationAlarmTimeUseCase {
    fun patchQuotationAlarmTime(
        userId: String,
        request: PatchQuotationAlarmTimeRequest,
    ): String

    data class PatchQuotationAlarmTimeRequest(
        val quotationAlarmTime: Timestamp,
    )

    data class PatchQuotationAlarmTimeResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
