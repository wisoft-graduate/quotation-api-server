package wisoft.io.quotation.adaptor.out.persistence.mapeer

interface Mapper<Entity, Domain> {
    fun toDomain(entity: Entity): Domain

    fun toEntity(domain: Domain): Entity
}
