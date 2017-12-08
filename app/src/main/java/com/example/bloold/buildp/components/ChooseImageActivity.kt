package com.example.bloold.buildp.components

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.PhotoHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 21.10.17.
 */

abstract class ChooseImageActivity : NetworkActivity() {
    private var mCurrentPhotoPath: String? = null

    protected abstract fun onImageChosen(imagePath: String)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, R.string.need_write_external, Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showChooseImageFromGallery()
            } else {
                Toast.makeText(this, R.string.need_read_external, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                //decodeImageAndSaveToTmp(mCurrentPhotoPath);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                data?.let { mCurrentPhotoPath = PhotoHelper.getPath(it.data) }
            }
            mCurrentPhotoPath?.let { onImageChosen(it) }
        }
    }

    protected fun chooseImage() {
        AlertDialog.Builder(this)
                .setTitle(R.string.add_photo)
                .setItems(arrayOf(getString(R.string.open_gallery), getString(R.string.open_camera))
                ) { _, which ->
                    if (which == 0)
                        showChooseImageFromGallery()
                    else if (which == 1)
                        dispatchTakePictureIntent()
                }.show()
    }

    /*** Файл в котором будет храниться фото  */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    /*** Вызываем интент для выбора фото из галереи  */
    private fun showChooseImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL)
            return
        }

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_application)), REQUEST_GALLERY_PHOTO)
    }

    /*** Вызываем интент для фото  */
    private fun dispatchTakePictureIntent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    REQUEST_WRITE_EXTERNAL)
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                //Crashlytics.logException(ex);
                // Error occurred while creating the File
                ex.printStackTrace()
                Toast.makeText(this, R.string.cant_create_image_file, Toast.LENGTH_LONG).show()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this, "ru.net.ruin.provider", photoFile))
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        } else
            Toast.makeText(this, R.string.no_capture_image, Toast.LENGTH_LONG).show()
    }

    companion object {
        internal val REQUEST_TAKE_PHOTO = 1
        internal val REQUEST_GALLERY_PHOTO = 2
        internal val REQUEST_WRITE_EXTERNAL = 111
        internal val REQUEST_READ_EXTERNAL = 333
    }
}
