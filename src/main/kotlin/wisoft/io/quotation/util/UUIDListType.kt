import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.*
import java.util.*
import kotlin.jvm.Throws

class UUIDListType : UserType<List<UUID>> {
    override fun getSqlType(): Int {
        return Types.ARRAY
    }

    override fun returnedClass(): Class<List<UUID>> {
        return List::class.java as Class<List<UUID>>
    }

    override fun equals(
        x: List<UUID>?,
        y: List<UUID>?,
    ): Boolean {
        return x == y
    }

    override fun hashCode(x: List<UUID>?): Int {
        return x?.hashCode() ?: 0
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeGet(
        rs: ResultSet?,
        position: Int,
        session: SharedSessionContractImplementor?,
        owner: Any?,
    ): List<UUID> {
        val array = rs?.getArray(position) ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val javaArray = array.array as Array<UUID>
        return javaArray.toList()
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeSet(
        st: PreparedStatement?,
        value: List<UUID>?,
        index: Int,
        session: SharedSessionContractImplementor?,
    ) {
        if (value == null) {
            st?.setNull(index, Types.ARRAY)
        } else {
            session?.doWork { connection ->
                val array = connection.createArrayOf("uuid", value.toTypedArray())
                st?.setArray(index, array)
            }
        }
    }

    override fun deepCopy(value: List<UUID>?): List<UUID> {
        return value?.toList() ?: emptyList()
    }

    override fun isMutable(): Boolean {
        return true
    }

    override fun disassemble(value: List<UUID>?): Serializable {
        return ArrayList(value ?: emptyList())
    }

    override fun assemble(
        cached: Serializable?,
        owner: Any?,
    ): List<UUID> {
        @Suppress("UNCHECKED_CAST")
        return cached as List<UUID>? ?: emptyList()
    }
}
