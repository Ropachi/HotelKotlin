package com.hotelkotlin

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

//logdata: loginからindexへの移動時の変数を渡すためのデータセット
@Entity
@Table(name = "logdata")
class LogData {
    @Id
    @Column
    var logid: Long? = null
    @Column
    var logname: String? = null
    @Column
    var logpsw: String? = null
}

