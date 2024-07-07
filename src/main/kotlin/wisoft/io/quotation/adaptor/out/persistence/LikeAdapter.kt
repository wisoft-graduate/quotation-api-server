package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.LikeRepository
import wisoft.io.quotation.application.port.out.GetLikeListPort
import wisoft.io.quotation.application.port.out.like.CreateLikePort
import wisoft.io.quotation.application.port.out.like.DeleteLikePort
import wisoft.io.quotation.application.port.out.like.GetLikePort
import wisoft.io.quotation.domain.Like
import java.util.*

@Component
class LikeAdapter(
    val likeRepository: LikeRepository,
) : GetLikeListPort,
    DeleteLikePort,
    GetLikePort,
    CreateLikePort {
    override fun getLikeListCountByUserId(userId: String): Long {
        return likeRepository.countAllByUserId(userId)
    }

    override fun createLike(like: Like): UUID {
        return likeRepository.save(like.toEntity()).id
    }

    override fun deleteLike(id: UUID) {
        return likeRepository.deleteById(id)
    }

    override fun getLikeById(id: UUID): Like? {
        return likeRepository.findByIdOrNull(id)?.toDomain()
    }
}
