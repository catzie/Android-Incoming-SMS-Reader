package net.catzie.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SmsReceiver receiver;

    private TextView tv_sms_from;
    private TextView tv_sms_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_sms_from = (TextView) findViewById(R.id.tv_sms_from);
        tv_sms_message = (TextView) findViewById(R.id.tv_sms_message);

        receiver = new SmsReceiver(new Handler()); // create receiver
        registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

    }

    public class SmsReceiver extends BroadcastReceiver {

        private String TAG = "SmsReceiver";

        private final Handler handler; // for executing code on UI thread

        public SmsReceiver(Handler handler){
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();

                //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                if (bundle != null){
                    //---retrieve the SMS message received---
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            final String msgFrom = msgs[i].getOriginatingAddress();
                            final String msgBody = msgs[i].getMessageBody();

                            Log.d(TAG, "SMS Received! From: " + msgFrom + ", Message: " + msgBody);

                            // post the UI-updating code to our Handler
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "SMS Received!", Toast.LENGTH_LONG).show();
                                    tv_sms_from.setText("From: " + msgFrom);
                                    tv_sms_message.setText("Message: " + msgBody);
                                }
                            });

                        }
                    }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                    }
                }
            }
        }
    }

}
