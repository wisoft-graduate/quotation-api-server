package wisoft.io.quotation.util

object SaltUtil {
    fun generateSalt(length: Int = 4): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
