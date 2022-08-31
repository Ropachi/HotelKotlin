//メインコントロールクラス
package com.hotelkotlin

import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.Optional

import com.hotelkotlin.repositories.LogDataRepository
import com.hotelkotlin.repositories.MyDataRepository
import com.hotelkotlin.repositories.OrdDataRepository
import java.util.*

//画面遷移用のコントローラーとして定義
@Controller
class HelloController {
    //クラスのインスタンスを使用できるように設定。
    @Autowired
    internal var repository: MyDataRepository? = null
    @Autowired
    internal var ordrepository: OrdDataRepository? = null
    @Autowired
    internal var logrepository: LogDataRepository? = null

    //login時、名前とパスワード入力時用変数セット
    internal var login_id: Long? = null
    internal var login_name: String? = null
    internal var login_psw: String? = null

    //EntityManagerのBeanを取得してフィールドに割り当てる
    @PersistenceContext
    internal var entityManager: EntityManager? = null  //field定義

    internal var ret = 0

    //インスタンス生成時に処理することを指定。
    @PostConstruct
    fun init() {
        //logdata: loginからindexへの移動時の変数を渡すためのデータセット
        val d1 = LogData()
        d1.logid = 1.toLong()
        d1.logname = "Thb78mLu52"
        d1.logpsw = "Uy75TvE9NpO6J"
        //SQLを実行
                   //↓!!でNullable型を強制的に通常の型として扱うようにする。
        logrepository!!.saveAndFlush(d1)
    }

    //メインindexページ処理
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun index(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("formModel") mydata: MyData,
        @ModelAttribute("logModel") logdata: LogData,
                           //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {
        mav.viewName = "index"

        //Home等ボタン表示内容設定のために logname を設定
        var logname: String = "Thb78mLu52"
                              //↓!!でNullable型を強制的に通常の型として扱うようにする。
        val list = logrepository!!.findByLogid(1.toLong())
        if (list.get().logname !== "Thb78mLu52") {
            logname = list.get().logname.toString()
                //↓値を保管する。
            mav.addObject("logname", logname)
        }

        //コース申し込み等の際の判断のための login_id をここで設定。
        if (login_name != null && login_psw != null) {
                                                 //↓!!でNullable型を強制的に通常の型として扱うようにする。
            val data2 = findByNameAndPsw(login_name!!, login_psw!!)
            if (data2 != null) {
                login_id = data2.get().id
            }
        }
        return mav
    }

    //ログインページ:GET
    @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
    fun login(
                             //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {
        mav.viewName = "login"
        var mes = ""
        if (ret == 1)
            mes = ""
        if (ret == 2)
            mes = "お名前あるいはパスワードが一致しません"
            //↓値を保管する。
        mav.addObject("msg", mes)
        return mav
    }

    //ログインページ:POST
    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun login2(
        //↓パラメータを取得(フォームnameの値をlogin_nameに渡す)
        @RequestParam("name") login_name: String,
        //↓パラメータを取得(フォームpswの値をlogin_pswに渡す)
        @RequestParam("psw") login_psw: String,
        //↓テンプレートで利用する値を設定
        @ModelAttribute("formModel") mydata: MyData,
        @ModelAttribute("logModel") logdata: LogData,
                            //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {
        var login_name = login_name
        var login_psw = login_psw

        try {
            val data = findByNameAndPsw(login_name, login_psw)
            //入力した名前とパスワードのセットが既存データにあるかどうかチェック
            if (data != null) {  //合致すべき該当データがあった場合
                login_id = data.get().id
                login_name = data.get().name.toString()
                login_psw = data.get().psw.toString()

                //index.htmlへ値を渡すための変数設定 LogDataへ値をセット
                logdata.logid = 1.toLong()
                logdata.logname = login_name
                logdata.logpsw = login_psw
                updatelog(logdata, mav)

                ret = 1
            } else {
                ret = 2  //該当するデータがなかったのでアウト
            }
        //エラー処理
        } catch (e: Exception) {
            ret = 2  //何らかのエラー発生があるとアウト
        } finally {
        }
        return if (ret == 1) {
            //ログインできればindexページへ移動
               //↓上記で設定したデータでテンプレートを利用。
            ModelAndView("redirect:/")
        } else {
            //ログインできなければ再度入力のため留まる
              //↓上記で設定したデータでテンプレートを利用。
            ModelAndView("redirect:/login")
        }
    }

    //アカウント作成ページ: GET
    @RequestMapping(value = ["/create"], method = [RequestMethod.GET])
                //↓テンプレートで利用する値を設定
    fun create(@ModelAttribute("formModel") mydata: MyData,
                                    //↓上記で設定したデータでテンプレートを利用。
               mav: ModelAndView): ModelAndView {
        mav.viewName = "create"
        var mes = ""
        when (ret) {
            1 -> {
                mes = "その名前とパスワードの組み合わせは既に存在します。"
                mes = ""
                mes = "入力エラーがありました!"
            }
            2 -> {
                mes = ""
                mes = "入力エラーがありました!"
            }
            3 -> mes = "入力エラーがありました!"
        }
        println("create:GET mes=$mes")
            //↓値を保管する。
        mav.addObject("msg", mes)
        return mav
    }

    //アカウント作成ページ: POST
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
    fun create2(
        //↓パラメータを取得(フォームnameの値をlogin_nameに渡す)
        @RequestParam("name") login_name: String,
        //↓パラメータを取得(フォームpswの値をlogin_pswに渡す)
        @RequestParam("psw") login_psw: String,
        //↓テンプレートで利用する値を設定
        @ModelAttribute("logModel") logdata: LogData,
                                     //↓自動的にバリデーション
        @ModelAttribute("formModel") @Validated mydata: MyData,
        //バリデーションチェックの結果は、このBindingResultという引数で取得
        result: BindingResult,
                            //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {

        //既存の名前とパスワードのセットと重複していないかチェック
        var res: ModelAndView? = null
        try {
            val data = findByNameAndPsw(login_name, login_psw)
            if (data != null) {
                res = ModelAndView("redirect:/create")
                ret = 1     //Out 該当既存データがある、つまり重複している
            } else {
                res = ModelAndView("redirect:/")
                ret = 2     //OK 該当既存データとは重複してない。
            }
        //エラー処理
        } catch (e: Exception) {
            ret = 1
        }

        //validation
        // ↓この変数resultにエラー結果が入っている。
        if (result.hasErrors()) {
                                //↓!!でNullable型を強制的に通常の型として扱うようにする。
            val list = repository!!.findAll()
                //↓値を保管する。
            mav.addObject("datalist", list)
            res = mav
            ret = 3  //Error
        } else {
            //mydata更新
            update(mydata, mav)
            //index.htmlへ値を渡すための準備 LogDataへ値をセット
            logdata.logid = 1.toLong()
            logdata.logname = login_name
            logdata.logpsw = login_psw
            updatelog(logdata, mav)
                    //↓上記で設定したデータでテンプレートを利用。
            res = ModelAndView("redirect:/")
        }
        return res
    }

    //予約ページ: GET
    @RequestMapping(value = ["/createord"], method = [RequestMethod.GET])
    fun createord(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("ordModel") orddata: OrdData,
        //@ModelAttribute("formModel") MyData mydata,
        @ModelAttribute("logModel") logdata: LogData,
                            //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {
        mav.viewName = "createord"
            //↓値を保管する。
        mav.addObject("title", "CreateOrd")

        //Home等ボタン表示内容設定のために logname を設定
        var logname = "Thb78mLu52"
                              //↓!!でNullable型を強制的に通常の型として扱うようにする。
        val list = logrepository!!.findByLogid(1.toLong())
        if (list.get().logname !== "Thb78mLu52") {
            logname = list.get().logname.toString()
                //↓値を保管する。
            mav.addObject("logname", logname)
        } else {
            logname = "Thb78mLu52"
        }
        return mav
    }

    //予約ページ: POST
    @RequestMapping(value = ["/createord"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
    fun createord2(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("ordModel") orddata: OrdData,
                            //↓上記で設定したデータでテンプレートを利用。
        mav: ModelAndView): ModelAndView {
        //予約用データセットへ顧客Idをセット
        if (login_id != null) {
            orddata.myid = login_id
        }
        //データを保存
        updateord(orddata, mav)
                //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //アカウント削除ページ: GET
    @RequestMapping(value = ["/delete/{id}"], method = [RequestMethod.GET])
               //↓パラメータを受け取るという定義
    fun delete(@PathVariable id: Long,
                                    //↓上記で設定したデータでテンプレートを利用。
               mav: ModelAndView): ModelAndView {
        mav.viewName = "delete"
            //↓値を保管する。
        mav.addObject("title", "アカウント削除.")
                           //↓!!でNullable型を強制的に通常の型として扱うようにする。
        val data = repository!!.findById(id)
            //↓値を保管する。
        mav.addObject("formModel", data.get())
        return mav
    }

    //アカウント削除ページ:POST
    @RequestMapping(value = ["/delete"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
                //↓パラメータを取得
    fun remove(@RequestParam id: Long,
               mav: ModelAndView): ModelAndView {
                //↓!!でNullable型を強制的に通常の型として扱うようにする。
        repository!!.deleteById(id)
                  //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //予約削除ページ: GET
    @RequestMapping(value = ["/deleteord/{ordid}"], method = [RequestMethod.GET])
                  //↓パラメータを受け取るという定義
    fun deleteord(@PathVariable ordid: Long?,
                  mav: ModelAndView): ModelAndView {
        mav.viewName = "deleteord"
            //↓値を保管する。
        mav.addObject("title", "Order Delete")
                              //↓!!でNullable型を強制的に通常の型として扱うようにする。
        val data = ordrepository!!.findByOrdid(ordid)
            //↓値を保管する。
        mav.addObject("ordModel", data.get())
        return mav
    }

    //予約削除ページ: POST
    @RequestMapping(value = ["/deleteord"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
                  //↓パラメータを取得
    fun removeord(@RequestParam ordid: Long?,
                  mav: ModelAndView): ModelAndView {
        ordrepository!!.deleteByOrdid(ordid)
                //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //アカウント修正ページ: GET
    @RequestMapping(value = ["/edit"], method = [RequestMethod.GET])
              //↓テンプレートで利用する値を設定
    fun edit(@ModelAttribute mydata: MyData,
             mav: ModelAndView): ModelAndView {
        mav.viewName = "edit"
            //↓値を保管する。
        mav.addObject("title", "アカウント修正.")

        //if (login_id != null) {
        val data = login_id?.let { repository?.findById(it) }
            //↓値を保管する。
        mav.addObject("formModel", data?.get())
        //}
        return mav
    }

    //アカウント修正ページ:POST
    @RequestMapping(value = ["/edit"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
               //↓テンプレートで利用する値を設定
    fun edit2(@ModelAttribute mydata: MyData,
              result: BindingResult,
              mav: ModelAndView): ModelAndView {
        var res: ModelAndView? = null

        if (!result.hasErrors()) {  //修正入力にエラーがあったかどうかチェック
            //SQLを実行
            repository!!.saveAndFlush(mydata)
                    //↓上記で設定したデータでテンプレートを利用。
            res = ModelAndView("redirect:/edit")
        } else {
                //↓値を保管する。
            mav.addObject("msg", "Error is occured!")
                    //↓上記で設定したデータでテンプレートを利用。
            res = ModelAndView("redirect:/edit")
        }
        return res
    }

    //ログアウトページ:GET
    @RequestMapping(value = ["/logout"], method = [RequestMethod.GET])
    fun logout(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("formModel") mydata: MyData,
        mav: ModelAndView): ModelAndView {
        mav.viewName = "logout"
        return mav
    }

    //ログアウトページ:POST
    @RequestMapping(value = ["/logout"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
                //↓パラメータを取得(フォームnameの値をlogin_nameに渡す)
    fun logout2(@RequestParam("name") login_name: String?,
                //↓パラメータを取得(フォームpswの値をlogin_pswに渡す)
                @RequestParam("psw") login_psw: String?,
                 //↓テンプレートで利用する値を設定
                @ModelAttribute("formModel") mydata: MyData,
                mav: ModelAndView): ModelAndView {
        var login_name = login_name
        var login_psw = login_psw
        //ログアウトするので変数初期化
        login_id = null
        login_name = null
        login_psw = null
        val logname = "Thb78mLu52"
                //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //アカウント一覧ページ:GET
    @RequestMapping(value = ["/list"], method = [RequestMethod.GET])
    fun list(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("formModel") mydata: MyData,
        mav: ModelAndView): ModelAndView {
        mav.viewName = "list"
            //↓値を保管する。
        mav.addObject("msg", "This is Data List.")
        val list = repository!!.findAll()
            //↓値を保管する。
        mav.addObject("datalist", list)
        return mav
    }

    //アカウント一覧ページ:POST
    @RequestMapping(value = ["/list"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
    fun list2(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("formModel") mydata: MyData,
        mav: ModelAndView): ModelAndView {
        //SQLを実行
        repository!!.saveAndFlush(mydata)
                //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //予約リスト表示:GET
    @RequestMapping(value = ["/listord"], method = [RequestMethod.GET])
    fun listord(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("ordModel") orddata: OrdData,
        mav: ModelAndView): ModelAndView {
        mav.viewName = "listord"
            //↓値を保管する。
        mav.addObject("msg", "This is Order List.")
        val list = ordrepository!!.findAll()
        //リスト表示用にデータを渡す。
            //↓値を保管する。
        mav.addObject("datalist", list)
        return mav
    }

    //予約リスト表示:POST
    @RequestMapping(value = ["/listord"], method = [RequestMethod.POST])
    @Transactional(readOnly = false)
    fun listord2(
        //↓テンプレートで利用する値を設定
        @ModelAttribute("ordModel") orddata: OrdData,
        mav: ModelAndView): ModelAndView {
        //SQLを実行
        ordrepository!!.saveAndFlush(orddata)
                //↓上記で設定したデータでテンプレートを利用。
        return ModelAndView("redirect:/")
    }

    //顧客データ保存
    fun update(mydata: MyData,
               mav: ModelAndView): ModelAndView {
        //SQLを実行
        repository!!.saveAndFlush(mydata)
        return mav
    }

    //予約データ保存
    fun updateord(orddata: OrdData,
                  mav: ModelAndView): ModelAndView {
        //SQLを実行
        ordrepository!!.saveAndFlush(orddata)
        return mav
    }

    //変数データ保存
    fun updatelog(logdata: LogData,
                  mav: ModelAndView): ModelAndView {
        //SQLを実行
logrepository!!.saveAndFlush(logdata)
        return mav
    }

    //顧客データから名前でレコード検索
    fun findByName(sname: String): MyData {
        val qstr = "SELECT c FROM MyData c WHERE c.name = :fname"
        val query = entityManager!!.createQuery<MyData>(qstr, MyData::class.java)
        query.setParameter("fname", sname)
        return query.singleResult as MyData
    }

    //顧客データから 名前とパスワードのセットでレコード検索
                                                       //OptionalでNullable型として扱う。
    fun findByNameAndPsw(sname: String, spsw: String): Optional<MyData> {
        val qstr = "SELECT c FROM MyData c WHERE c.name = :fname and c.psw = :fpsw"
        val query = entityManager!!.createQuery<MyData>(qstr, MyData::class.java)
        query.setParameter("fname", sname)
        query.setParameter("fpsw", spsw)
        val list = query.singleResult as MyData
        return Optional.ofNullable(list)
    }
}