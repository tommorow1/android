package com.example.bloold.buildp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.ui.fragments.MapObjectListFragment

/**
 * Created by sagus on 20.11.2017.
 */
class MapActivity: AppCompatActivity() {
    companion object {
        fun launch(cntx: Context, obj: CatalogObject?=null)
        {
            cntx.startActivity(Intent(cntx, MapActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_LATITUDE, obj?.getLocation()?.latitude)
                    .putExtra(IntentHelper.EXTRA_LONGITUDE, obj?.getLocation()?.longitude))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.container,
                    MapObjectListFragment.newInstance(intent.getSerializableExtra(IntentHelper.EXTRA_LATITUDE) as Double?,
                            intent.getSerializableExtra(IntentHelper.EXTRA_LONGITUDE) as Double?)).commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
