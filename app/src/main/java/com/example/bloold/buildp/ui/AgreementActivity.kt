package com.example.bloold.buildp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.ui.fragments.WebViewFragment
import kotlinx.android.synthetic.main.activity_agreement.*

/**
 * Created by mikha on 29-Oct-17.
 */
class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_agreement)

        setSupportActionBar(myToolbar)
        supportActionBar?.title = getString(R.string.title_activity_agreement)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if(savedInstanceState==null)
        {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, WebViewFragment.newInstance(getString(R.string.title_activity_agreement), Settings.URL_AGREEMENT))
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}