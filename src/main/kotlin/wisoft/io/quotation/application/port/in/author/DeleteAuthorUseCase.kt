package wisoft.io.quotation.application.port.`in`.author

import java.util.UUID

interface DeleteAuthorUseCase {
    fun deleteAuthor(id: UUID)
}
