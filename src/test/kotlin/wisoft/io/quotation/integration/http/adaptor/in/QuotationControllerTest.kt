package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationUseCase
import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.RankProperty
import wisoft.io.quotation.domain.SortDirection
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getAuthorEntityFixture
import wisoft.io.quotation.fixture.entity.getQuotationEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class QuotationControllerTest(
    val mockMvc: MockMvc,
    val quotationRepository: QuotationRepository,
    val authorRepository: AuthorRepository,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            authorRepository.deleteAll()
            quotationRepository.deleteAll()
        }

        context("getQuotationRank Test") {
            test("getQuotationRank 성공") {
                // given
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                // when
                val result =
                    mockMvc.get("/quotations/rank") {
                        apply { listOf(quotation.id).forEach { param("ids", it.toString()) } }
                        param("rankProperty", RankProperty.LIKE.name)
                        param("page", 1.toString())
                        param("count", 100.toString())
                    }.andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(
                        result,
                        GetQuotationRankUseCase.GetQuotationRankResponse::class.java,
                    ).data.quotationRanks.first()

                println("actual: $actual")
                actual.id shouldBe quotation.id
                actual.rank shouldBe 1
                actual.count shouldBe 0
                actual.backgroundImagePath shouldBe quotation.backgroundImagePath
            }
        }

        context("getQuotation Test") {
            test("getQuotation 성공") {
                // given
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/quotations/${quotation.id}")
                            .contentType(MediaType.APPLICATION_JSON),
                    )
                        .andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetQuotationUseCase.GetQuotationResponse::class.java).data
                actual.id shouldBe quotation.id
                actual.author.id shouldBe author.id
                actual.content shouldBe quotation.content
                actual.likeCount shouldBe quotation.likeCount
                actual.shareCount shouldBe quotation.shareCount
                actual.commentCount shouldBe quotation.commentCount
                actual.backgroundImagePath shouldBe quotation.backgroundImagePath
                actual.author.name shouldBe author.name
            }

            test("getQuotation 실패") {
                // given
                val status = HttpMessage.HTTP_404.status
                val id = UUID.randomUUID()
                val path = "/quotations/$id"

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get(path),
                    )
                        .andExpect(MockMvcResultMatchers.status().isNotFound)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data
                actual.status shouldBe status.value()
                actual.error shouldBe status.reasonPhrase
                actual.path shouldBe path
                actual.message shouldBe id.toString() + HttpMessage.HTTP_404.message
            }
        }

        context("getQuotationList Test") {
            test("getQuotationList 성공") {
                // given
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))
                val request =
                    GetQuotationListUseCase.GetQuotationListRequest(
                        searchWord = quotation.content,
                        sortTarget = QuotationSortTarget.LIKE,
                        sortDirection = SortDirection.ASC,
                        paging = Paging(page = 1, count = 10),
                        ids = listOf(quotation.id),
                        rankProperty = RankProperty.LIKE,
                    )

                // when
                val result =
                    mockMvc.get("/quotations") {
                        param("searchWord", request.searchWord!!)
                        param("sortTarget", request.sortTarget?.name!!)
                        param("sortDirection", request.sortDirection?.name!!)
                        param("page", request.paging?.page?.toString()!!)
                        param("count", request.paging?.count?.toString()!!)
                        param("rankProperty", request.rankProperty?.name!!)
                        request.ids?.forEach { id -> param("ids", id.toString()) }
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(
                        result,
                        GetQuotationListUseCase.GetQuotationListResponse::class.java,
                    ).data.first()

                actual.id shouldBe quotation.id
                actual.author.id shouldBe author.id
                actual.content shouldBe quotation.content
                actual.likeCount shouldBe quotation.likeCount
                actual.shareCount shouldBe quotation.shareCount
                actual.commentCount shouldBe quotation.commentCount
                actual.backgroundImagePath shouldBe quotation.backgroundImagePath
                actual.author.name shouldBe author.name
                actual.rank shouldBe 1
            }
        }

        context("shareQuotation Test") {
            test("shareQuotation 성공") {
                // given
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                // when, then
                mockMvc.post("/quotations/${quotation.id}/share")
                    .andExpect { MockMvcResultMatchers.status().isCreated }
                quotationRepository.findById(quotation.id).get().shareCount shouldBe 1
            }
        }
    })
