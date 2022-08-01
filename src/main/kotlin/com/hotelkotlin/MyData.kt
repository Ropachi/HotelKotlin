package com.hotelkotlin

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

//顧客データ作成
@Entity
@Table(name = "mydata")
class MyData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    var id: Long = 0

    @Column(nullable = false)
    @NotEmpty(message = "名前を入力してください。")
    var name: String? = null

    @Column(nullable = false)
    @NotEmpty(message = "パスワードを入力してください。")
    var psw: String? = null

    @Column(nullable = false)
    @Email(message = "メール形式で入力してください")
    var mail: String? = null

    //login時、名前とパスワード入力時用変数セット
    var login_id: Long? = null
    var login_name: String? = null
    var login_psw: String? = null
}
