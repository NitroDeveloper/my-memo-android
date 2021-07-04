package com.nitroex.my_memo.ui

import android.os.Bundle
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.adapter.CompanyAdapter
import com.nitroex.my_memo.utils.model.Company
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.model.CheckEmail
import kotlinx.android.synthetic.main.activity_company.*

class CompanyActivity : BaseActivity() {
    private lateinit var model: Company
    private var isForgotPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)

        setView()
        setClick()
    }

    private fun setView() {
        val companyList: ArrayList<Company>
        val companyDummy: ArrayList<Company> = arrayListOf()

        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            isForgotPass = bundle.getString("isFrom","")=="ForgotPass"
            val model = bundle.getSerializable("model") as ArrayList<CheckEmail>

            for (i in model.indices){
                companyDummy.add(Company("", model[i].company_id,"", model[i].company_code,"","","","", null,""))
            }
        }

        companyList = if (isForgotPass) { companyDummy
        } else{ getDataCompany() }

        val adapter = CompanyAdapter(this, R.layout.list_view_row, companyList)
        autoCompany.setAdapter(adapter)
        autoCompany.setOnItemClickListener { _, _, position, _ ->
            autoCompany.setText("")
            model = companyList[position]
            tvCompany.text = model.company_name
        }

    }

    private fun setClick() {
        tvCompany.setOnClickListener { autoCompany.showDropDown() }
        ivSelect.setOnClickListener { autoCompany.showDropDown() }
        btnConfirm.setOnClickListener {
            if (getTextToTrim(tvCompany).isNotEmpty()) {
                if (isForgotPass) { returnActivityWithBundle()
                }else{ checkSelectCompany() }
            }else{
                showDialogAlert(false, getString(R.string.select_company))
            }
        }
    }

    private fun checkSelectCompany() {
        if (checkTextIsNotEmptyAlert(getTextToTrim(tvCompany), getHintToTrim(tvCompany) ,false)) {
            setSharePreUserByCompany(model.emp_id_pk, model.emp_com_id, model.com_id_pk, model.company_name)
            setSharePreUser(this, Configs.IsSelectCompany, true)
            openActivityFinish(MainActivity::class.java)
        }
    }

    // isForgotPass only
    private fun returnActivityWithBundle() {
        val bundle = Bundle().apply {
            putString("company_id", model.com_id_pk)
        }
        returnBundleRefreshActivity(bundle); finish()
    }
}