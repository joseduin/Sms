package foxhound.ocelotsms.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.telephony.SmsManager;
import android.util.Log;


/**
 * Created by Jose on 1/5/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("NOTIFICATION", remoteMessage.getData().toString());
        Log.d("NOTIFICATION", remoteMessage.getNotification().getBody());

        String numeros = remoteMessage.getData().get("numero");
        // Single Number
        if (!numeros.contains(";")) {
            sendSms(remoteMessage.getNotification().getBody(), numeros);

        } else {
            // Multiple Numbers
            while (numeros.contains(";")) {
                String numero = numeros.substring(0, numeros.indexOf(";"));
                numeros = numeros.replace(numero + ";", "");

                sendSms(remoteMessage.getNotification().getBody(), numero);
            }
            sendSms(remoteMessage.getNotification().getBody(), numeros);
        }
    }

    private void sendSms(String body, String numero) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(numero, null, body, null, null);
    }

}

