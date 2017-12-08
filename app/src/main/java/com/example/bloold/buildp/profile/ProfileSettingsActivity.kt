package com.example.bloold.buildp.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.model.BaseModel
import com.example.bloold.buildp.utils.MediaFilePicker
import com.example.bloold.buildp.utils.PermissionUtil
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_profile_settings.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class ProfileSettingsActivity: AppCompatActivity(), MediaFilePicker.OnFilePickerListener {

    companion object {
        private const val TAG = "ProfileSettingsActivity"

        private const val REQUEST_STORAGE_PERMISSION = 1
        private const val REQUEST_CAMERA_PERMISSION = 2

        private const val BASE_URL = "http://ruinnet.idefa.ru/api_app"
        private const val FILE_UPLOAD = "/file/upload/"
    }

    private lateinit var ivAvatar: ImageView
    private lateinit var tvEditPhoto: TextView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var spinnerEdit: Spinner
    private lateinit var tvSaveChanges: TextView

    private var ivBack: ImageView? = null
    private var tvTitle: TextView? = null
    private var ivCancel: ImageView? = null

    private var adapterS: ArrayAdapter<String>? = null
    private var regions: ArrayList<BaseModel> = ArrayList()

    private var FirstName: String = ""
    private var LastName: String = ""
    private var Region: String = ""
    private var IndexPhoto: String = ""

    private lateinit var filePicker: MediaFilePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_settings)
        setSupportActionBar(findViewById<View>(R.id.toolbar_actionbar) as Toolbar?)

        var toolbar = findViewById<Toolbar?>(R.id.toolbar_actionbar)

        ivBack = toolbar?.findViewById<ImageView>(R.id.ivBack)
        tvTitle = toolbar?.findViewById<TextView>(R.id.tvTitle)
        ivCancel = toolbar?.findViewById<ImageView>(R.id.ivCancel)

        ivBack?.visibility = View.VISIBLE
        tvTitle?.visibility = View.VISIBLE
        ivCancel?.visibility = View.VISIBLE

        ivCancel?.setOnClickListener{
            logOut()
            onBackPressed()
        }

        ivBack?.setOnClickListener{
            onBackPressed()
        }

        if (supportActionBar != null) {
        }

        ivAvatar = findViewById(R.id.ivAvatar)
        tvEditPhoto = findViewById(R.id.tvEditPhoto)
        etFirstName = findViewById(R.id.etEditFirstName)
        etLastName = findViewById(R.id.etEditLastName)
        spinnerEdit = findViewById(R.id.spEdit)
        tvSaveChanges = findViewById(R.id.tvSaveChange)

        tvSaveChanges.setOnClickListener{
            FirstName = etFirstName.text.toString()
            LastName = etLastName.text.toString()
            Region  = regions[spinnerEdit.selectedItemPosition].id?:""

            JSONEdit(this).execute("http://ruinnet.idefa.ru/api_app" + "/user/profile/edit/")

            onBackPressed()
        }

        tvEditPhoto.setOnClickListener{
            dialogFilePicker()
        }

        adapterS = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item)
                /*.createFromResource(this,
                R.array.spinner_choice_region, android.R.layout.simple_spinner_item)
*/
        //adapterS?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerEdit.adapter = adapterS
        spinnerEdit.id
        getUser()
        //getRegion()

        filePicker = MediaFilePicker(this, this, savedInstanceState)
    }

    private fun dialogFilePicker() {
        val choose = resources.getStringArray(R.array.chooseImage)
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.profile_pick_photo))
                .setItems(choose, { _, i ->
                    if (i == 0) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestStoragePermission(REQUEST_STORAGE_PERMISSION)
                        } else {
                            filePicker.requestGalleryIntent()
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermission()
                        } else {
                            filePicker.requestCameraIntent()
                        }
                    }
                })
                .show()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
    }

    private fun requestStoragePermission(code: Int) {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        filePicker.onActivityResult(requestCode, resultCode, data)
        return super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                filePicker.requestGalleryIntent()
            } else {

            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                filePicker.requestCameraIntent()
            } else {

            }
        }
    }

    private fun logOut(){

            /*var urlParam = ""
            var JsonOutput = ""

            try {
                var sPref = getSharedPreferences("main", MODE_PRIVATE);
                var AuthTokenSuccess = sPref.getString(LoginActivity.AuthToken,"");
                JsonOutput = JSONPost().execute("http://ruinnet.idefa.ru/api_app/user/logout/",urlParam).get();

                var response: JSONObject? = null;
                try {
                    response = JSONObject(JsonOutput);

                } catch (e: JSONException) {
                    e.printStackTrace();
                }

                var code: String? = null;

                try {
                    code = response?.getString("CODE");
                    if(code.equals("200")) {
                        hideItem();
                        DeleteToken();
                        LogBtnShow();
                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }

            } catch (e: InterruptedException) {
                e.printStackTrace();
            } catch (e: ExecutionException) {
                e.printStackTrace();
            }*/
    }

    private fun getUser(){
        var sPref = getSharedPreferences("main", MODE_PRIVATE)
        var AuthTokenSuccess = sPref.getString(LoginActivity.AuthToken,"")

        var url = "http://ruinnet.idefa.ru/api_app/user/profile/"
        var client = AsyncHttpClient()

        var params = RequestParams()

        client.setBasicAuth("defa","defa")
        client.addHeader("Device-Id", Settings.getUdid())
        client.addHeader("Auth-Token",AuthTokenSuccess)

        var context = this

        client.get(url, params, object : TextHttpResponseHandler() {
                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                    Log.d("onfail", statusCode.toString())
                }

                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                    var response: JSONObject? = null
                    try {
                        response = JSONObject(responseString)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    Log.d("onSuccess", response.toString())

                    var image: String? = ""
                    var code: String? = ""
                    var name: String? = ""
                    var last_name: String? = ""

                    var data: JSONObject? = null

                    try {
                        code = response?.getString("CODE")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if(code.equals("200")) {

                        try {
                            data = JSONObject(response?.getString("DATA"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        Log.d(code, data.toString())

                        try {
                            name = data?.getString("NAME")
                            Log.d("name", name.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        try {
                            last_name = data?.getString("LAST_NAME")
                            Log.d("last_name", last_name.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        etFirstName.setText(name)
                        etLastName.setText(last_name)


                        try {
                            image = data?.getString("PHOTO")

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        Region=data?.getString("REGION_ID")?:""

                        if(image != null && !TextUtils.isEmpty(image) && image != "null")
                            Glide.with(context).load("http://ruinnet.idefa.ru" + image).into(ivAvatar)
                    }

                }

                override fun onStart() {
                    Log.d("onStart", "")
                }


                override fun onFinish() {
                    // Completed the request (either success or failure)
                    getRegion()
                }
            })
    }

    private fun getRegion(){
        var sPref = getSharedPreferences("main", MODE_PRIVATE)
        var AuthTokenSuccess = sPref.getString(LoginActivity.AuthToken,"")

        var url = "http://ruinnet.idefa.ru/api_app/directory/region/"
        var client = AsyncHttpClient()

        var params = RequestParams()

        client.setBasicAuth("defa","defa")
        client.addHeader("Device-Id",Settings.getUdid())
        client.addHeader("Auth-Token",AuthTokenSuccess)

        var context = this

        client.get(url, params, object : TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("onfail", statusCode.toString())
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                var response: JSONObject? = null
                try {
                    response = JSONObject(responseString)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Log.d("onSuccess", response.toString())

                var id: String? = ""
                var code: String? = ""
                var name: String? = ""
                var data: JSONArray? = null

                try {
                    code = response?.getString("CODE")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if(code.equals("200")) {

                    try {
                        data = JSONArray(response?.getString("DATA"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    Log.d(code, data.toString())

                    for(i: Int in 0..(data?.length()?:1) - 1){
                        var model = BaseModel()

                        try{
                            id = data?.getJSONObject(i)?.getString("ID")
                        } catch (e: Exception){

                        }

                        try {
                            name = data?.getJSONObject(i)?.getString("NAME")
                        } catch (e: Exception){

                        }

                        model.id = id
                        model.name = name

                        regions.add(model)
                        adapterS?.add(name)
                    }
                }

            }

            override fun onStart() {
                Log.d("onStart", "")
            }


            override fun onFinish() {
                //Выделяем регион, который указан в профиле пользователя
                for (i in 0 until regions.size)
                {
                    if(regions[i].id==Region)
                    {
                        spEdit.setSelection(i)
                        break
                    }
                }
            }
        })
    }

    override fun onFilePicked(file: File?) {
        Log.d("filePisked", file?.name.toString())
        UploadPhoto(this, toBase64(ivAvatar)).execute(BASE_URL + FILE_UPLOAD)
        Glide.with(this).load(file).into(ivAvatar)
    }

    inner class JSONEdit(val context: Context): AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            var connection: HttpURLConnection? = null
            val reader: BufferedReader? = null
            var output = ""
            var sPref = getSharedPreferences("main", MODE_PRIVATE)
            var AuthTokenSuccess = sPref.getString(LoginActivity.AuthToken, "")
            try {
                FirstName = URLEncoder.encode(FirstName, "utf-8");
                LastName = URLEncoder.encode(LastName, "utf-8");
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                var urlParameters = "FIRST_NAME=${FirstName}&LAST_NAME=${LastName}&REGION_ID="+Region

                if(!IndexPhoto.isNullOrEmpty()) {
                    urlParameters = urlParameters + "&PHOTO_ID=" + IndexPhoto
                    Log.d("tag", IndexPhoto)
                }

                connection.requestMethod = "POST"
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0")
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5")
                connection.setRequestProperty("Device-Id", Settings.getUdid())
                connection.setRequestProperty("Auth-Token", AuthTokenSuccess)
                val header = "Basic " + String(android.util.Base64.encode("defa:defa".toByteArray(), android.util.Base64.NO_WRAP))
                connection.addRequestProperty("Authorization", header)
                connection.doOutput = true
                val dStream = DataOutputStream(connection.outputStream)
                dStream.writeBytes(urlParameters)
                dStream.flush()
                dStream.close()

                //connection.connect();
                val responseCode = connection.responseCode

                val br = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String? = ""
                val responseOutput = StringBuilder()
                line = br.readLine()
                while (!line.isNullOrEmpty()) {
                    responseOutput.append(line)
                    line = br.readLine()
                }
                br.close()

                output = responseOutput.toString()
                return output

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (connection != null) {
                    connection.disconnect()
                }
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return output
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d("edit", result.toString())

            val finalJson = result.toString()
            var response: JSONObject? = null
            try {
                response = JSONObject(result)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (response != null) {
                var code: String? = null
                try {
                    code = response.getString("CODE")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (code == "200") {

                }
            }

        }
    }

    inner class UploadPhoto(val context: Context, val photo: String): AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            var connection: HttpURLConnection? = null
            val reader: BufferedReader? = null
            var sPref = getSharedPreferences("main", MODE_PRIVATE)
            var AuthTokenSuccess = sPref.getString(LoginActivity.AuthToken, "")
            var output = ""

            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                val urlParameters = "FILES[1][BASE64]=${photo}&FILES[1][NAME]=121321.jpg"
                connection.requestMethod = "POST"
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0")
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5")
                connection.setRequestProperty("Device-Id", Settings.getUdid())
                connection.setRequestProperty("Auth-Token", AuthTokenSuccess)
                val header = "Basic " + String(android.util.Base64.encode("defa:defa".toByteArray(), android.util.Base64.NO_WRAP))
                connection.addRequestProperty("Authorization", header)
                connection.doOutput = true

                val dStream = DataOutputStream(connection.outputStream)
                dStream.writeBytes(urlParameters)
                dStream.flush()
                dStream.close()

//connection.connect();
                val responseCode = connection.responseCode

                Log.d("uploadPhoto", responseCode.toString())

                val br = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String? = ""
                val responseOutput = StringBuilder()
                line = br.readLine()
                while (!line.isNullOrEmpty()) {
                    responseOutput.append(line)
                    line = br.readLine()
                }
                br.close()

                output = responseOutput.toString()
                return output

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (connection != null) {
                    connection.disconnect()
                }
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            Log.d("uploadPhotoOutput", output)
            return output
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            Log.d("upload", result.toString())

            val finalJson = result.toString()
            var response: JSONObject? = null
            try {
                response = JSONObject(result)
                Log.d("uploadR", response.toString())

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (response != null) {
                var code: String? = null
                try {

                    code = response.getString("CODE")
                    IndexPhoto = response.getJSONObject("DATA").getString("ITEMS").substring(1, 7)
                    Log.d("uploadR", IndexPhoto.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (code == "200") {
                    Toast.makeText(this@ProfileSettingsActivity, IndexPhoto, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun toBase64(mSomeImageView: ImageView): String {
        // Получаем изображение из ImageView
        var drawable = mSomeImageView.getDrawable() as BitmapDrawable;
        var bitmap = drawable.getBitmap();

        // Записываем изображение в поток байтов.
        // При этом изображение можно сжать и / или перекодировать в другой формат.
        var byteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // Получаем изображение из потока в виде байтов
        var bytes = byteArrayOutputStream.toByteArray();

        // Кодируем байты в строку Base64 и возвращаем
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private fun sendPhoto() {
    }
}
