package com.example.bloold.buildp.components;

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 23.08.16.
 */
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class BindingViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final B mLayoutBinding;

    public BindingViewHolder(B binding) {
        super(binding.getRoot());
        this.mLayoutBinding = binding;
    }
}
