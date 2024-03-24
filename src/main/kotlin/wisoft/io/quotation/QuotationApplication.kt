package wisoft.io.quotation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuotationApplication

fun main(args: Array<String>) {
    runApplication<QuotationApplication>(*args)
}
