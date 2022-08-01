package com.hotelkotlin

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

import org.hibernate.validator.constraints.NotEmpty

//予約データ作成
@Entity
@Table(name = "orddata")
class OrdData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    var ordid: Long? = null

    @Column
    var myid: Long? = null

    @Column
    var ordcity: String? = null

    @Column
    var ordtype: String? = null

    @Column
    var orddate: String? = null
}