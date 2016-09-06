package nuaa.wsm.simpleweather.util;

/**
 * Created by Fear on 2016/9/6.
 */
public interface HttpCallbackListener {

    public void onFinish(String response);

    public void onError(Exception e);
}
