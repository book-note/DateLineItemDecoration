package com.merpyzf.itemdecorationdemo

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class RvAdapter(data: MutableList<String>, layout: Int) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId = layout, data = data) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_title, item)
    }
}