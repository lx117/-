package jianrt.slidetounlock.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.DoubleBounce;

import jianrt.slidetounlock.R;
import jianrt.slidetounlock.TbApplication;

public class WaitDialog {
    private Dialog waitDialog;
    Context context;
    public TextView loading;

    public WaitDialog(Context context) {
        this.context = TbApplication.getInstance();
        if (waitDialog == null)
            waitDialog = new Dialog(context, R.style.TRANSDIALOG);
        waitDialog.setContentView(R.layout.custom);
        loading = (TextView) waitDialog.findViewById(R.id.loading);
        //ProgressBar
        ProgressBar progressBar = (ProgressBar) waitDialog.findViewById(R.id.waitingbar);
        DoubleBounce doubleBounce = new DoubleBounce();
        doubleBounce.setBounds(0, 0, 100, 100);
        doubleBounce.setColor(R.color.material_blue_500);
        progressBar.setIndeterminateDrawable(doubleBounce);
        waitDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparent);
        waitDialog.setCanceledOnTouchOutside(false);

        waitDialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //����
                }
                return false;
            }
        });
    }


    public int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public void showWaitDialog() {
        if (!waitDialog.isShowing())
            waitDialog.show();

    }

    public void cancleWaitDialog() {
        if (waitDialog.isShowing())
            waitDialog.cancel();

    }

    public void setnoTouchOutside() {
        waitDialog.setCanceledOnTouchOutside(true);
    }
}
