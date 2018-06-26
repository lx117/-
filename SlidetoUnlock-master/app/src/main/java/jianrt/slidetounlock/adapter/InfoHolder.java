package jianrt.slidetounlock.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import jianrt.slidetounlock.R;
import jianrt.slidetounlock.util.InfoUtils;

/**
 * Author:11719<p>
 * CreateDate:2017/4/21<p>
 * Fuction:<p>
 */

public class InfoHolder extends BaseViewHolder<String> {
    ImageView icon;
    TextView name;
    InfoUtils infoUtils;

    public InfoHolder(ViewGroup parent) {
        super(parent, R.layout.item_info);
        icon = $(R.id.icon);
        name = $(R.id.name);
        infoUtils = new InfoUtils(getContext());
    }

    @Override
    public void setData(String data) {
        super.setData(data);
        icon.setImageDrawable(infoUtils.getAppIcon(data));
        name.setText(infoUtils.getAppName(data));
    }
}
