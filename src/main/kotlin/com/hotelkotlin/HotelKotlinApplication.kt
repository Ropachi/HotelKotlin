//起動アプリケーション
package com.hotelkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HotelKotlinApplication

fun main(args: Array<String>) {
    runApplication<HotelKotlinApplication>(*args)
}
