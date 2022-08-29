//個人データモデルリポジトリ
package com.hotelkotlin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

import com.hotelkotlin.MyData

//顧客データリポジトリ
@Repository
interface MyDataRepository : JpaRepository<MyData, Long> {
    //個人データを全て検索
    override fun findAll(): List<MyData>

    //個人データをIDで検索
                                     //OptionalでNullable型として扱う。
    override fun findById(id: Long): Optional<MyData>
}