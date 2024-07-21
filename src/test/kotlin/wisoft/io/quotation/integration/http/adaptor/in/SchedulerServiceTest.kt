package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.out.push.PushAlarmNotificationPort
import wisoft.io.quotation.application.port.out.push.PushTagNotificationPort
import wisoft.io.quotation.application.service.SchedulerService
import wisoft.io.quotation.fixture.entity.getUserEntityFixtureIncludeQuotationAlarmTimes

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class SchedulerServiceTest(
    val userRepository: UserRepository,
    val schedulerService: SchedulerService,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            userRepository.deleteAll()
        }

        context("pushQuotationAlarm Test") {
            test("pushQuotationAlarm 성공") {
                // given
                val existUser = userRepository.save(getUserEntityFixtureIncludeQuotationAlarmTimes())

                // when
                val resultCount = schedulerService.pushQuotationAlarm()

                // then
                resultCount shouldBe existUser.quotationAlarmTimes.size
            }
            test("pushQuotationAlarm 성공 - 알람 허용을 하지 않은 사용자는 보내지 않음") {
                // given
                val existUser = userRepository.save(getUserEntityFixtureIncludeQuotationAlarmTimes(quotationAlarm = false))

                // when
                val resultCount = schedulerService.pushQuotationAlarm()

                // then
                resultCount shouldBe 0
            }
        }
    }) {
    @MockBean
    lateinit var pushTagNotificationPort: PushTagNotificationPort

    @MockBean
    lateinit var pushAlarmNotificationPort: PushAlarmNotificationPort
}
