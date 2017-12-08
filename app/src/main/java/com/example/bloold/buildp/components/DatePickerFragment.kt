package com.example.bloold.buildp.components

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.DatePicker

import java.util.Calendar

/**
 * Created by oleg on 19.04.15.
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var onDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var defaultValue = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = defaultValue.get(Calendar.YEAR)
        val month = defaultValue.get(Calendar.MONTH)
        val day = defaultValue.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year, month, day)
    }

    fun setDefaultValue(defaultValue: Calendar?) {
        if (defaultValue != null) this.defaultValue = defaultValue
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onDateSetListener?.onDateSet(view, year, month, day)
    }
}
