package com.example.bloold.buildp.single.`object`

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bloold.buildp.R

class DescriptionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_description, container, false)
        arguments?.getString(DESCRIPTION_KEY)?.let {
            (view?.findViewById(R.id.tvDescription) as TextView).text = it
        }
        return view
    }
    companion object {
        private val DESCRIPTION_KEY = "description"

        fun newInstance(description: String?): DescriptionFragment {
            return DescriptionFragment().apply {
                if(!description.isNullOrEmpty()){
                    arguments = Bundle().apply { putString(DESCRIPTION_KEY, description)} }
            }
        }
    }
}
