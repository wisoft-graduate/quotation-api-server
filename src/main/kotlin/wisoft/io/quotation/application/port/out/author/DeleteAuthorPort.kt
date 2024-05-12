package wisoft.io.quotation.application.port.out.author

import java.util.UUID

interface DeleteAuthorPort {
    fun deleteAuthor(id: UUID)
}
