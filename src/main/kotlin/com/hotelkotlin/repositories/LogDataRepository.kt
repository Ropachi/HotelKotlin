//ログイン用データモデルリポジトリ
package com.hotelkotlin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import com.hotelkotlin.LogData

import java.util.Optional

//logdata: loginからindexへの移動時の変数を渡すためのデータリポジトリ
@Repository
interface LogDataRepository : JpaRepository<LogData, Long> {
    //ログイン用個人をIDで検索
    fun findByLogid(logid: Long): Optional<LogData>
}

