//アプリケーションクラス
package com.hotelkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//Spring Bootの様々な設定を自動的に有効にする.
@SpringBootApplication
class HotelKotlinApplication

fun main(args: Array<String>) {
    runApplication<HotelKotlinApplication>(*args)
}
