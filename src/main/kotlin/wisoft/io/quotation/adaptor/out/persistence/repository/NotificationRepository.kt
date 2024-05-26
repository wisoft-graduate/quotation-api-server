package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import wisoft.io.quotation.adaptor.out.persistence.entity.NotificationEntity
import java.util.UUID

interface NotificationRepository : JpaRepository<NotificationEntity, UUID>
