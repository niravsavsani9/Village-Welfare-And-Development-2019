package grammitra2019.com;

import android.app.Application;

import com.onesignal.OneSignal;

public class AppNotification extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
