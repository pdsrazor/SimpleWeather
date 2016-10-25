package nuaa.wsm.simpleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import nuaa.wsm.simpleweather.R;

/**
 * Created by Fear on 2016/10/20.
 */
public class StartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏title和状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = StartActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        final View view = View.inflate(this, R.layout.start_activity_layout, null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });


    }

    /**
     * 跳转到...
     */
    private void redirectTo(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
        if(sharedPreferences.getBoolean("area_selected", false)) {/* 已经选择过区域 则直接跳转显示上一次选中的area */
            /* 事先准备好数据 */
            String last_select_area_code = sharedPreferences.getString("county_code", "");
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("county_code", last_select_area_code);
            intent.putExtra("from", "StartActivity");
            /* 跳转 */
            startActivity(intent);
        } else {
            /* 否则跳转到选择界面 */
            Intent intent = new Intent(this, SelectedAreaDisplayActivity.class);
            intent.putExtra("from", "StartActivity");
            startActivity(intent);
        }

        finish();
    }
}
