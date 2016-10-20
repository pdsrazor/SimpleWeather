package nuaa.wsm.simpleweather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Fear on 2016/10/17.
 */
public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
