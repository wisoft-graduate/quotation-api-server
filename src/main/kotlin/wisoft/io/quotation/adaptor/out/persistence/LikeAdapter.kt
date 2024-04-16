package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.LikeRepository
import wisoft.io.quotation.application.port.out.GetLikeListPort

@Component
class LikeAdapter(
    val likeRepository: LikeRepository
) : GetLikeListPort {
    override fun getLikeListCountByUserId(userId: String): Long {
        return likeRepository.countAllByUserId(userId)
    }
}