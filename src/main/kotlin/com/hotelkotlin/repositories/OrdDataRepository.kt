//オーダー用モデル リポジトリ
package com.hotelkotlin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

import com.hotelkotlin.OrdData

//予約データリポジトリ
@Repository
interface OrdDataRepository : JpaRepository<OrdData, Long> {
    //オーダーデータをIDで検索
                           //型宣言に?を付けnullable宣言
                                    //OptionalでNullable型として扱う。
    fun findByOrdid(ordid: Long?): Optional<OrdData>

    //個人IDで検索
    fun findByMyid(myid: Long?): List<OrdData>

    //オーダーデータをIDで削除
    fun deleteByOrdid(ordid: Long?)
}