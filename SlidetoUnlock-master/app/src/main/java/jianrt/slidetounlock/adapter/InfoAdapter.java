package jianrt.slidetounlock.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Author:11719<p>
 * CreateDate:2017/4/21<p>
 * Fuction:<p>
 */

public class InfoAdapter extends RecyclerArrayAdapter<String> {

    public InfoAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoHolder(parent);
    }

}
