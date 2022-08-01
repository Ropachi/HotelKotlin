package com.hotelkotlin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

import com.hotelkotlin.MyData

//顧客データリポジトリ
@Repository
interface MyDataRepository : JpaRepository<MyData, Long> {
    override fun findAll(): List<MyData>
    override fun findById(id: Long): Optional<MyData>
}