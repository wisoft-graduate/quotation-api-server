package wisoft.io.quotation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class QuotationApplication

fun main(args: Array<String>) {
    runApplication<QuotationApplication>(*args)
}
