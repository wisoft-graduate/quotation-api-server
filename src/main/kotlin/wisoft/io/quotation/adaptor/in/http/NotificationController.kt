package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.notification.DeleteNotificationUseCase
import wisoft.io.quotation.application.port.`in`.notification.GetNotificationListUseCase
import wisoft.io.quotation.application.port.`in`.notification.UpdateNotificationUseCase
import java.util.UUID

@RestController
class NotificationController(
    val getNotificationListUseCase: GetNotificationListUseCase,
    val updateNotificationUseCase: UpdateNotificationUseCase,
    val deleteNotificationUseCase: DeleteNotificationUseCase,
) {
    @GetMapping("/notifications")
    fun getNotificationList(
        @RequestParam userId: String,
    ): ResponseEntity<GetNotificationListUseCase.GetNotificationListResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetNotificationListUseCase.GetNotificationListResponse(
                    data = getNotificationListUseCase.getNotificationList(userId),
                ),
            )
    }

    @PutMapping("/notifications/{id}")
    fun updateNotification(
        @PathVariable id: UUID,
        @RequestBody request: UpdateNotificationUseCase.UpdateNotificationRequest,
    ): ResponseEntity<UpdateNotificationUseCase.UpdateNotificationResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                UpdateNotificationUseCase.UpdateNotificationResponse(
                    data = UpdateNotificationUseCase.Data(id = updateNotificationUseCase.updateNotification(id, request)),
                ),
            )
    }

    @DeleteMapping("/notifications/{id}")
    fun deleteNotification(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        deleteNotificationUseCase.deleteNotification(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}
