package jianrt.slidetounlock.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import jianrt.slidetounlock.R;
import jianrt.slidetounlock.view.SwipeBackLayout;

public class LockMainActivity extends Activity {

  private TextView lock_date;
  private ShimmerTextView lockmain_shimmer;
  private SwipeBackLayout swipeback;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lockmain);
    //Remove notification bar
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //disable the notification's expand feature
    lockmain_shimmer = (ShimmerTextView) findViewById(R.id.lockmain_shimmer);
    Shimmer shimmer = new Shimmer();
    shimmer.start(lockmain_shimmer);
    lock_date = (TextView) findViewById(R.id.lock_date);
    swipeback = (SwipeBackLayout) findViewById(R.id.swipeback);
    swipeback.setSwipeBackListener(new SwipeBackLayout.SwipeBackFinishActivityListener(this));
    lock_date.setText(timelongTOdate(System.currentTimeMillis(), "dd/MM/EE"));
  }

  public static String timelongTOdate(Long timelong, String style_yyMMddHHmmss) {
    return (new SimpleDateFormat(style_yyMMddHHmmss, Locale.getDefault())).format(timelong);
  }
  @Override
  public void onAttachedToWindow() {
    //关键：在onAttachedToWindow中设置FLAG_HOMEKEY_DISPATCHED
    //this.getWindow().addFlags(WindowManager.LayoutParams. FLAG_HOMEKEY_DISPATCHED);
    super.onAttachedToWindow();
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    // 返回true，不响应其他key
    return true;
  }
}
