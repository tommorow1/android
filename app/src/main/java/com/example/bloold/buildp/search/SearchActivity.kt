package com.example.bloold.buildp.search

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.example.bloold.buildp.R
import android.widget.ArrayAdapter
import com.example.bloold.buildp.ListActivityObjects.ListObjectsActivity
import com.example.bloold.buildp.callback
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.SpinnerWithoutLPaddingAdapter
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.ActivitySearchBinding
import com.example.bloold.buildp.model.CatalogObjectsModel
import com.example.bloold.buildp.model.HightFilterModelLevel
import com.example.bloold.buildp.ui.CatalogObjectDetailsActivity
import com.example.bloold.buildp.ui.fragments.MapObjectListFragment


class SearchActivity : AppCompatActivity(), callback, AdapterView.OnItemSelectedListener {

    private lateinit var mBinding: ActivitySearchBinding
    private lateinit var spinnerChoice: Spinner
    private lateinit var etChoice: EditText
    private lateinit var lvObjects: ListView
    private lateinit var btnViewFind: Button

    private val INDEX_ADDRESS = 1
    private val INDEX_OBJECT = 2

    private var indexChoice = INDEX_ADDRESS

    private var objects: ArrayList<CatalogObjectsModel> = ArrayList()

    private val presenter: SearchPresenter = SearchPresenter(this, this)

    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        val REQUEST_CODE_SEARCH=378
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        mBinding.listener=this
        val intent = intent
        val fromActivity = intent.getStringExtra("fromActivity") as String

        spinnerChoice = findViewById(R.id.spinnerChoice)
        etChoice = findViewById(R.id.etChoice)
        lvObjects = findViewById(R.id.lvNames)
        btnViewFind = findViewById(R.id.btnViewFind)

        val adapterS = ArrayAdapter.createFromResource(this, R.array.spinner_choice_object_address, android.R.layout.simple_spinner_item)

        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, ArrayList<String>())

        spinnerChoice.adapter = adapterS
        spinnerChoice.onItemSelectedListener = this

        etChoice.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                mBinding.ivClear.visibility=if(mBinding.etChoice.text.toString().isBlank()) View.INVISIBLE else View.VISIBLE
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
                if(indexChoice == INDEX_ADDRESS){
                    presenter.findAddress(p0.toString())
                } else {
                    presenter.findObjects(p0.toString())
                }
            }

        })

        btnViewFind.setOnClickListener{
            if(!adapter.isEmpty){
                if (fromActivity != null && fromActivity == "map") {
                    val searchType = if(indexChoice == INDEX_ADDRESS)
                        "address"
                    else {
                        val egrnNumber = mBinding.etChoice.text.toString().toIntOrNull()
                        if(egrnNumber != null) {
                            "egrn"
                        } else
                            "object"
                    }
                    startActivity(Intent(this, MapObjectListFragment::class.java)
                            .putExtra(IntentHelper.EXTRA_SEARCH_TYPE, searchType)
                            .putExtra(IntentHelper.EXTRA_SEARCH_TEXT, mBinding.etChoice.text.toString()))
                }else{
                    setResult(Activity.RESULT_OK, Intent()
                            .putExtra(IntentHelper.EXTRA_QUERY_STRING, etChoice.text.toString())
                            .putExtra(IntentHelper.EXTRA_QUERY_TYPE, getSearchType())
                    )
                    finish()
                    /*val intent = Intent(this, ListObjectsActivity::class.java)
                    intent.putExtras(Bundle().apply { putParcelableArrayList(ListObjectsActivity.KEY_LIST_OBJECT, objects) } )
                    startActivity(intent)*/
                }

            }
        }

        lvObjects.adapter = adapter
        lvObjects.setOnItemClickListener({ _, _, position, _ ->
            startActivity(Intent(this, CatalogObjectDetailsActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_OBJECT_ID, objects[position].id?.toInt()))
        })
    }
    private fun getSearchType():String?
    {
        return when(spinnerChoice.selectedItemPosition)
        {
            0 -> "NAME"
            1 -> "PROPERTY_ADDRESS"
            else -> null
        }
    }

    fun onClearSearch(v:View)
    {
        mBinding.etChoice.setText("")
        UIHelper.hideKeyboard(mBinding.etChoice)
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        indexChoice = p2
        adapter.clear()
    }

    override fun onObjectsLoaded(items: ArrayList<CatalogObjectsModel>) {
        adapter.clear()
        var outArr: List<String?>
        outArr = items.map{it -> it.name}.filter { it -> it != null }
        objects = items
        adapter.addAll(outArr)
        adapter.notifyDataSetInvalidated()
    }

    override fun onFiltersLoaded(items: ArrayList<HightFilterModelLevel>) {

    }
}
