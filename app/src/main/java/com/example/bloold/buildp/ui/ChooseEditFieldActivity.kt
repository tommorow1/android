package com.example.bloold.buildp.ui

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.StringPairAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ActivityChooseEditFieldBinding


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChooseEditFieldActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityChooseEditFieldBinding
    private var objectId: Int = -1

    companion object {
        val REQUEST_CODE_EDIT_OBJECT=123
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_edit_field)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        objectId=intent.getIntExtra(IntentHelper.EXTRA_OBJECT_ID, -1)

        mBinding.rvFields.layoutManager = LinearLayoutManager(this)
        mBinding.rvFields.addItemDecoration(DividerItemDecoration(this, OrientationHelper.VERTICAL))
        mBinding.rvFields.adapter = StringPairAdapter(OnItemClickListener {
            when(it.second)
            {
                "add_object_item" -> AddObjectActivity.launch(this)
                "status_item" -> startActivityForResult(Intent(this, EditStateActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId), EditStateActivity.REQUEST_CODE_EDIT_STATE_OBJECT)
                "photo_video_audio_item" -> startActivity(Intent(this, EditPhotoVideoAudioActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
                "main_info_item" -> AddObjectActivity.launch(this, true, objectId)
                "archive_item" -> startActivity(Intent(this, EditArchiveMaterialsActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
                "science_pub_info" -> startActivity(Intent(this, EditPublicationsActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
            }
            //TODO открываем в зависимости от того что выбрали
        },
                arrayListOf(Pair(getString(R.string.add_object_item), "add_object_item"),
                        Pair(getString(R.string.state_item), "status_item"),
                        Pair(getString(R.string.photo_video_audio_item), "photo_video_audio_item"),
                        Pair(getString(R.string.main_info_item), "main_info_item"),
                        Pair(getString(R.string.archive_item), "archive_item"),
                        Pair(getString(R.string.science_pub_info), "science_pub_info")))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
            setResult(Activity.RESULT_OK)
    }
}
