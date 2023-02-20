package com.my.vibras.AudioCalling;
        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.graphics.PorterDuff;
        import android.os.Bundle;

        import android.os.CountDownTimer;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Chronometer;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import com.bumptech.glide.Glide;
        import com.my.vibras.R;

        import org.apache.commons.lang3.StringEscapeUtils;

        import java.util.Locale;

        import de.hdodenhof.circleimageview.CircleImageView;
        import io.agora.rtc.IRtcEngineEventHandler;
        import io.agora.rtc.RtcEngine;

public class VoiceChatViewActivity extends AppCompatActivity {
    private static final String LOG_TAG = VoiceChatViewActivity.class.getSimpleName();
    String token = "";
    String Channel = "";
    String name = "";
    String image = "";
    int Profile = 1;
    CircleImageView user_image_show;
    TextView txtexpname,timer;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    CountDownTimer countDownTimer;
    private RtcEngine mRtcEngine; // Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };
    Chronometer cmTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat_view);
        user_image_show= findViewById(R.id.user_image_show);
        txtexpname= findViewById(R.id.username);
        cmTimer= findViewById(R.id.cmTimer);
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            Channel = getIntent().getStringExtra("channel_name");
            Profile = getIntent().getIntExtra("Profile",0);
         name = getIntent().getStringExtra("name");
           image = getIntent().getStringExtra("userimage");
            txtexpname.setText(StringEscapeUtils.unescapeJava(name));
            Glide.with(getApplicationContext()).load(image).into(user_image_show);
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        joinChannel();               // Tutorial Step 2
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.green_new), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.green_new), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        finish();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), "d4311c64c69d4615afab65cc233605fb", mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
        String accessToken = token;
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        mRtcEngine.setChannelProfile(0);

        mRtcEngine.joinChannel(accessToken, Channel, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
        cmTimer.start();
    }

    // Tutorial Step 3
    private void leaveChannel() {

        cmTimer.stop();mRtcEngine.leaveChannel();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        tipMsg.setVisibility(View.VISIBLE);
        finish();
        cmTimer.stop();
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }
}
