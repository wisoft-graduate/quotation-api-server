package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.AccountEntity

@Repository
interface UserRepository: JpaRepository<AccountEntity, String>