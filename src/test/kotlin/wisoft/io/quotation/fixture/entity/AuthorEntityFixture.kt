package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.AuthorEntity

fun getAuthorEntityFixture(): AuthorEntity {
    return AuthorEntity(
        name = "author",
        countryCode = "kr",
    )

}
