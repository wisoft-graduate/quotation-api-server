package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.LikeMapper
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
    val likeMapper: LikeMapper,
) : GetLikeListPort,
    DeleteLikePort,
    GetLikePort,
    CreateLikePort {
    override fun getLikeListCountByUserId(userId: String): Long {
        return likeRepository.countAllByUserId(userId)
    }

    override fun createLike(like: Like): UUID {
        return likeRepository.save(likeMapper.toEntity(like)).id
    }

    override fun deleteLike(id: UUID) {
        return likeRepository.deleteById(id)
    }

    override fun getLikeById(id: UUID): Like? {
        return likeRepository.findByIdOrNull(id)?.let { likeMapper.toDomain(it) }
    }

    override fun getLikeByUserIdAndQuotationId(
        userId: String,
        quotationId: UUID,
    ): Like? {
        return likeRepository.findByUserIdAndQuotationId(userId, quotationId)?.let { likeMapper.toDomain(it) }
    }

    override fun getLikeByUserId(userId: String): List<Like> {
        return likeMapper.toDomains(likeRepository.findByUserId(userId))
    }
}
