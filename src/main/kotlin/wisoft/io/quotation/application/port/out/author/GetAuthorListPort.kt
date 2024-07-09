package wisoft.io.quotation.application.port.out.author

import wisoft.io.quotation.domain.Author
import java.util.UUID

interface GetAuthorListPort {
    fun getAuthorList(): List<Author>

    fun getAuthorListById(ids: List<UUID>): List<Author>
}
