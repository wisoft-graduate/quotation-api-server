package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.application.port.out.author.CreateAuthorPort
import wisoft.io.quotation.application.port.out.author.DeleteAuthorPort
import wisoft.io.quotation.application.port.out.author.GetAuthorPort
import wisoft.io.quotation.application.port.out.author.UpdateAuthorPort
import wisoft.io.quotation.domain.Author
import java.util.*

@Component
class AuthorAdaptor(
    val authorRepository: AuthorRepository,
) : CreateAuthorPort, GetAuthorPort, UpdateAuthorPort, DeleteAuthorPort {
    override fun createAuthor(author: Author): UUID {
        return authorRepository.save(author.toEntity()).id
    }

    override fun getAuthor(id: UUID): Author? {
        return authorRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun updateAuthor(author: Author): UUID {
        return authorRepository.save(author.toEntity()).id
    }

    override fun deleteAuthor(id: UUID) {
        return authorRepository.deleteById(id)
    }
}
