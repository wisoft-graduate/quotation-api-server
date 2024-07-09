package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.AuthorMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.application.port.out.author.*
import wisoft.io.quotation.domain.Author
import java.util.*

@Component
class AuthorAdaptor(
    val authorRepository: AuthorRepository,
    val authorMapper: AuthorMapper,
) : CreateAuthorPort, GetAuthorPort, UpdateAuthorPort, DeleteAuthorPort, GetAuthorListPort {
    override fun createAuthor(author: Author): UUID {
        return authorRepository.save(authorMapper.toEntity(author)).id
    }

    override fun getAuthor(id: UUID): Author? {
        return authorRepository.findByIdOrNull(id)?.let { authorMapper.toDomain(it) }
    }

    override fun updateAuthor(author: Author): UUID {
        return authorRepository.save(authorMapper.toEntity(author)).id
    }

    override fun deleteAuthor(id: UUID) {
        return authorRepository.deleteById(id)
    }

    override fun getAuthorList(): List<Author> {
        return authorRepository.findAll().map { authorMapper.toDomain(it) }
    }

    override fun getAuthorListById(ids: List<UUID>): List<Author> {
        return authorRepository.findAllById(ids).map { authorMapper.toDomain(it) }
    }
}
