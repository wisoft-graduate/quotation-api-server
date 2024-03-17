package wisoft.io.quotation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
class QuotationApplication

fun main(args: Array<String>) {
    runApplication<QuotationApplication>(*args)
}
