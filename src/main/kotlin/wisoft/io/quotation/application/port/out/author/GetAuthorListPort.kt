package wisoft.io.quotation.application.port.out.author

import wisoft.io.quotation.domain.Author

interface GetAuthorListPort {
    fun getAuthorList(): List<Author>
}
