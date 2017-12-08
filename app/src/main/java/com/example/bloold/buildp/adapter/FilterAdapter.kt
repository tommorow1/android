package com.example.bloold.buildp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bloold.buildp.R
import com.example.bloold.buildp.model.Category

/**
 * Created by mikha on 29-Oct-17.
 */
class FilterAdapter(var mContext: Context?, var mGroups : Array<Category>) : BaseExpandableListAdapter() {
    val selectedIds=HashSet<String>()
    override fun getGroupCount(): Int = mGroups.size

    override fun getChildrenCount(groupPosition: Int): Int = mGroups[groupPosition].children?.size ?: 0

    override fun getGroup(groupPosition: Int): Any? = mGroups

    override fun getChild(groupPosition: Int, childPosition: Int): Any? =
            mGroups[groupPosition].children?.get(childPosition)

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {

        val convertView2: View = if (convertView == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_filter_start_root, null)
        } else
            convertView

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        val textGroup = convertView2.findViewById<TextView>(R.id.filterRootName)
        textGroup?.text = mGroups[groupPosition].name

        return convertView2
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        val convertView2: View = if (convertView == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_filter_start, null)
        } else {
            convertView
        }

//        val textGroup = convertView2?.findViewById<android.support.v7.widget.AppCompatTextView>(R.id.tvName)
//        textGroup?.text = mGroups?.get(groupPosition)?.get(childPosition)?.name

        val sortObject=mGroups[groupPosition].children?.get(childPosition)
        val checkBox = convertView2.findViewById<CheckBox>(R.id.chbFilter)
        checkBox.tag = sortObject?.id
        checkBox.text = sortObject?.name
        checkBox.setOnCheckedChangeListener(null)
        checkBox.tag?.let { checkBox.isChecked=selectedIds.contains(it as String) }
        checkBox.setOnCheckedChangeListener { btn, checked ->
            if(checked) selectedIds.add(btn.tag as String)
            else selectedIds.remove(btn.tag as String)
        }

        return convertView2
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}