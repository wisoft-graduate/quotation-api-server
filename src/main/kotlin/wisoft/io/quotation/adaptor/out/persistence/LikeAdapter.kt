package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.LikeRepository
import wisoft.io.quotation.application.port.out.GetLikeCountByUserIdPort

@Component
class LikeAdapter(
    val likeRepository: LikeRepository
) : GetLikeCountByUserIdPort {
    override fun getLikeCountByUserId(userId: String): Long {
        return likeRepository.countAllByUserId(userId)
    }
}