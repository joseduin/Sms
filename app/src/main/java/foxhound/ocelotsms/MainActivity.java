package foxhound.ocelotsms;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zagum.switchicon.SwitchIconView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends RuntimePermissionsActivity {

    @BindView(R.id.notificacion) LinearLayout notificacion;
    @BindView(R.id.btn_notification) SwitchIconView btn_notification;
    @BindView(R.id.txt_notificacion) TextView txt_notificacion;

    @BindString(R.string.notificaciones_activas) String notificaciones_activas;
    @BindString(R.string.notificaciones_inactivas) String notificaciones_inactivas;
    @BindString(R.string.topic) String topic;
    @BindString(R.string.preference_name) String preference_name;
    @BindString(R.string.preference_mode) String preference_mode;

    private SharedPreferences pref;
    private static final int REQUEST_PERMISSIONS = 20;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setPreferece();
        getFirebaseToken();
        notificationStatus();
        clickNotification();
        openNotificationAndSendSms();

        permissions();
    }

    private void permissions() {
        MainActivity.super.requestAppPermissions(new
                        String[]{android.Manifest.permission.SEND_SMS},
                R.string.app_name, REQUEST_PERMISSIONS);
    }


    @Override
    public void onPermissionsGranted(int requestCode) {
        if(requestCode == REQUEST_PERMISSIONS) {
            askForContactPermission(requestCode);

        }
    }

    public void askForContactPermission(int code){
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        //    Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
        //}
        Log.d("CODE------", "" + code);
    }

    private void openNotificationAndSendSms() {
        if (getIntent().getExtras() != null) {
            String numero = "";
            String body = "";
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);

                if (key.equals("numero")) {
                    numero = value.toString();
                }
                if (key.equals("body")) {
                    body = value.toString();
                }
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, body, null, null);
        }
    }

    private void setPreferece() {
        pref = getSharedPreferences(preference_name, Integer.valueOf(preference_mode));
        editor = pref.edit();
    }

    private void getFirebaseToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Refreshed token", refreshedToken +" .");
    }

    private void clickNotification() {
        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationChange();
            }
        });
    }

    private void notificationStatus() {
        if (pref.getString(topic, null) == null) {
            // Desactivada las notificaciones
            btn_notification.setIconEnabled(false, true);
            txt_notificacion.setText(notificaciones_activas);
        } else {
            // Activar las notificaciones
            btn_notification.setIconEnabled(true, true);
            txt_notificacion.setText(notificaciones_inactivas);
        }
    }

    public void notificationChange() {
        btn_notification.switchState(true);
        if (btn_notification.isIconEnabled()) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            setPreferences(topic, topic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
            setPreferences(topic, null);
        }
    }

    private void setPreferences(String key, String value) {
        editor.putString(key, value);
        editor.apply();
        editor.commit();

        notificationStatus();
    }

}
