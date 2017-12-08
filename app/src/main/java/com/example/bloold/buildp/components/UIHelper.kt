package com.example.bloold.buildp.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.TextInputLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ErrorUtils
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.profile.LoginActivity
import retrofit2.Response
import java.net.MalformedURLException
import java.net.URL
import java.util.*


/**
 * Created by sagus on 21.11.2017.
 */
object UIHelper
{
    /*** Есть ли ошибка в ответе сервера  */
    fun showServerError(response: Response<*>, activity: Context?) {
        activity?.let {
            val error = ErrorUtils.parseError(response)
            if (error != null && !TextUtils.isEmpty(error.message))
                Toast.makeText(it.applicationContext, error.message, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(it.applicationContext, R.string.server_error, Toast.LENGTH_LONG).show()
        }
    }
    /*** Показываем ошибку для TextView если пустой
     * @return true если ошибка
     */
    fun setErrorIfEmpty(editText: TextView, errorMsg: String): Boolean {
        val value = editText.text.toString()

        return if (TextUtils.isEmpty(value)) {
            setError(editText, errorMsg)
            true
        } else {
            setError(editText, null)
            false
        }
    }
    /*** Показываем ошибку для TextView или TextInputLayout если в него завёрнут EditText  */
    fun setError(editText: TextView, errorMsg: String?) {
        val textInputLayout = getParentTextInputLayout(editText)
        if (textInputLayout != null)
            textInputLayout.error = errorMsg
        else
            editText.error = errorMsg
    }
    private fun getParentTextInputLayout(textView: TextView): TextInputLayout? {
        var parent: ViewParent? = textView.parent
        while (parent != null && parent !is TextInputLayout)
            parent = parent.parent
        return parent as TextInputLayout?
    }

    /*** Возвращает true если авторизован, иначе открывает экран логина */
    fun userAuthorizedOtherwiseOpenLogin(cntx:Activity)
            = if(Settings.userToken.isNullOrEmpty())
                {
                    cntx.startActivity(Intent(cntx, LoginActivity::class.java))
                    false
                } else true

    fun makeEditTextScrollable(editText:EditText)
    {
        editText.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    fun showDatePickerDialog(act: Activity, defaultValue: Calendar): DatePickerFragment {
        val newFragment = DatePickerFragment()
        newFragment.setDefaultValue(defaultValue)
        newFragment.show(act.fragmentManager, "datePicker")
        return newFragment
    }

    fun hideKeyboard(view: View?) {
        view?.let {
            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
    fun getYoutubePreviewImage(youtubeUrl: String?): String?
    {
        extractYoutubeId(youtubeUrl)?.let {
            return "http://img.youtube.com/vi/$it/0.jpg" // this is link which will give u thumnail image of that video
        }
        return null
    }

    fun extractYoutubeId(url: String?): String?
    {
        try {
            url?.let {
                val query:String? = URL(url).query
                val param:List<String>? = query?.split("&".toRegex())?.dropLastWhile({ it.isEmpty() })
                var id: String? = null
                param?.let { it.map { row -> row.split("=".toRegex()).dropLastWhile({ it.isEmpty() }) }
                        .filter { it[0] == "v" }
                        .forEach { id = it[1] } }
                if(id==null)
                {
                    val splashPos=url.lastIndexOf("/")
                    if(splashPos!=-1&&splashPos!=url.length-1)
                        id=url.substring(splashPos+1)
                }
                return id
            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
        }
        return null
    }
}