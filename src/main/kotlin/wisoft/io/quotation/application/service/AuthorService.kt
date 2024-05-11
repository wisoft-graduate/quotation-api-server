package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.author.*
import wisoft.io.quotation.application.port.out.author.*
import wisoft.io.quotation.domain.Author
import wisoft.io.quotation.exception.error.AuthorNotFoundException
import java.util.*

@Service
@Transactional(readOnly = true)
class AuthorService(
    val createAuthorPort: CreateAuthorPort,
    val updateAuthorPort: UpdateAuthorPort,
    val deleteAuthorPort: DeleteAuthorPort,
    val getAuthorPort: GetAuthorPort,
    val getAuthorListPort: GetAuthorListPort,
) : CreateAuthorUseCase, UpdateAuthorUseCase, DeleteAuthorUseCase, GetAuthorUseCase, GetAuthorListUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createAuthor(request: CreateAuthorUseCase.CreateAuthorRequest): UUID {
        return runCatching {
            val author =
                request.run {
                    Author(
                        name = this.name,
                        countryCode = this.countryCode,
                    )
                }
            createAuthorPort.createAuthor(author)
        }.onFailure {
            logger.error { "createAuthor fail: param[$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun updateAuthor(
        id: UUID,
        request: UpdateAuthorUseCase.UpdateAuthorRequest,
    ): UUID {
        return runCatching {
            val author = getAuthorPort.getAuthor(id) ?: throw AuthorNotFoundException(id.toString())
            updateAuthorPort.updateAuthor(author.update(request))
        }.onFailure {
            logger.error { "updateAuthor fail: param[$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun deleteAuthor(id: UUID) {
        return runCatching {
            getAuthorPort.getAuthor(id) ?: throw AuthorNotFoundException(id.toString())
            deleteAuthorPort.deleteAuthor(id)
        }.onFailure {
            logger.error { "deleteAuthor fail: param[id: $id]" }
        }.getOrThrow()
    }

    override fun getAuthor(id: UUID): Author {
        return runCatching {
            getAuthorPort.getAuthor(id) ?: throw AuthorNotFoundException(id.toString())
        }.onFailure {
            logger.error { "getAuthor fail: param[id: $id]" }
        }.getOrThrow()
    }

    override fun getAuthorList(): List<Author> {
        return runCatching {
            getAuthorListPort.getAuthorList()
        }.onFailure {
            logger.error { "getAuthorList fail" }
        }.getOrThrow()
    }
}
