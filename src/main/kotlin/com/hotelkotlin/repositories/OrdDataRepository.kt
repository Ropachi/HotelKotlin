package com.hotelkotlin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

import com.hotelkotlin.OrdData

//予約データリポジトリ
@Repository
interface OrdDataRepository : JpaRepository<OrdData, Long> {
    fun findByOrdid(ordid: Long?): Optional<OrdData>
    fun findByMyid(myid: Long?): List<OrdData>
    fun deleteByOrdid(ordid: Long?)
}