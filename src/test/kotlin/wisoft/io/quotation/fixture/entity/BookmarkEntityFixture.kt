package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.BookmarkEntity

fun getBookmarkEntityFixture(userId: String): BookmarkEntity {
    return BookmarkEntity(
        name = "bookmark",
        userId = userId,
        quotationIds = listOf(),
        visibility = true,
        icon = "icon",
    )
}
