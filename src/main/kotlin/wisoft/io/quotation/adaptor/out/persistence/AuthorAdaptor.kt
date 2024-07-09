package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.application.port.out.author.*
import wisoft.io.quotation.domain.Author
import java.util.*

@Component
class AuthorAdaptor(
    val authorRepository: AuthorRepository,
) : CreateAuthorPort, GetAuthorPort, UpdateAuthorPort, DeleteAuthorPort, GetAuthorListPort {
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

    override fun getAuthorList(): List<Author> {
        return authorRepository.findAll().map { it.toDomain() }
    }

    override fun getAuthorListById(ids: List<UUID>): List<Author> {
        return authorRepository.findAllById(ids).map { it.toDomain() }
    }
}
