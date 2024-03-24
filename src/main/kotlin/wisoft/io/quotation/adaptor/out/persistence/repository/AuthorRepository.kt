package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import wisoft.io.quotation.adaptor.out.persistence.entity.AuthorEntity
import java.util.UUID

interface AuthorRepository: JpaRepository<AuthorEntity, UUID>