package wisoft.io.quotation.application.port.out.author

import wisoft.io.quotation.domain.Author
import java.util.*

interface GetAuthorPort {
    fun getAuthor(id: UUID): Author?
}
