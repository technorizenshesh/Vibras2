package com.my.vibras.chat;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.my.vibras.AudioCalling.VoiceChatViewActivity;
import com.my.vibras.R;
import com.my.vibras.VideoCalling.VideoCallingAct;
import com.my.vibras.act.FriendProfileActivity;
import com.my.vibras.act.SearchLocationMapAct;
import com.my.vibras.media.RtcTokenBuilder;
import com.my.vibras.model.SuccessResInsertChat;
import com.my.vibras.model.SuccessResMakeCall;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.NetworkAvailablity;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.GPSTracker;
import com.my.vibras.utility.Session;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;
import static com.my.vibras.utility.RandomString.getAlphaNumericString;

public class ChatInnerMessagesActivity extends AppCompatActivity {
    static final int OPEN_MEDIA_PICKER = 1;
    private static final int SELECT_VIDEO = 1;
    ImageView sendbutton, camerabutton, uploadmultimedia, backbuttonchat;
    Session session;
    String useriddeivce;
    CircleImageView friednimage;
    TextView friend_name;
    RecyclerView chat_messages_list;
    String friend_idlast;
    ArrayList<ChatMessage> allmessagelist = new ArrayList<>();
    ChatListAdapter chatListAdapter;

    String friendnamelast;
    int IMAGE_REQ_CODE = 102;
    RelativeLayout alltimelayout, firsttimelayout;
    //=------firstimelayoutDATA----------
    CircleImageView profileuserfirsttime;
    TextView usernametextfirstime;
    CardView replybuttonfirst;
String id;
    ImageView backbuttonfirsttime;
    TextView statustextshow;
    String status = "1";
    int messageunreadcount;
    ImageView menu;
    RelativeLayout backbutton;
    String unique_id;
    private DatabaseReference mReference;
    private DatabaseReference mReference1;
    RelativeLayout audio_call_rela, call_relat;
    EditText chatmessage_edit;
    String friendimage;
    TextView username;
    private VibrasInterface apiInterface;

    static String appId = "d4311c64c69d4615afab65cc233605fb";
    static String appCertificate = "24c609f228764230a692c6bca95b40a9";
    static String channelName = "Vibras";
    static String userAccount = "427554";
    /*  static String appId = "d4311c64c69d4615afab65cc233605fb";
    static String appCertificate = "24c609f228764230a692c6bca95b40a9";
    static String channelName = "Vibras";
    static String userAccount = "427554";*/
    static int uid = 0;
    static int expirationTimeInSeconds = 43200;
    private String token;
    ImageView live_location;
    GPSTracker gpsTracker;
    LinearLayout typing_layout;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onStop() {
        Log.e("hererer", "onStop:");
        mReference.child("typing").child("type" + useriddeivce + "To" + friend_idlast).setValue("false");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_inner_messages);
        gpsTracker = new GPSTracker(this);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        checkPermisssionForReadStorage();
        typing_layout = findViewById(R.id.typing_layout);
        chat_messages_list = findViewById(R.id.chat_messages_list);
        audio_call_rela = findViewById(R.id.audio_call_rela);
        call_relat = findViewById(R.id.call_relat);
        sendbutton = findViewById(R.id.img);
        camerabutton = findViewById(R.id.camerabutton);
        backbutton = findViewById(R.id.backbutton);
        chatmessage_edit = findViewById(R.id.etText);
        username = findViewById(R.id.username);
        live_location = findViewById(R.id.live_location);
        session = new Session(ChatInnerMessagesActivity.this);
        useriddeivce = session.getUserId();

    mReference = FirebaseDatabase.getInstance().getReference();
        if (getIntent() != null) {
            friend_idlast = getIntent().getStringExtra("id");
            friendimage = getIntent().getStringExtra("friendimage");
            friendnamelast = getIntent().getStringExtra("friend_name");
            String status_check = getIntent().getStringExtra("status_check");
            unique_id = getIntent().getStringExtra("unique_id");
            Log.e("unique_id", "unique_id: " + unique_id);
            id = getIntent().getStringExtra("id");
            Log.e("->>", "onCreate: friend_idlastfriend_idlastfriend_idlastfriend_idlast  " + friend_idlast + status_check);
            Log.e("->>", "onCreate: friend_idlastfriend_idlastfriend_idlastfriend_idlast    idd " + id + "----" + status_check);
            username.setText(StringEscapeUtils.unescapeJava(friendnamelast));
            session.setLastChatId(friend_idlast);
        }

        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        username.setOnClickListener(v -> {
            Intent intent = new Intent(ChatInnerMessagesActivity.this, FriendProfileActivity.class);
            intent.putExtra("id", friend_idlast);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        if (Double.parseDouble(useriddeivce) > Double.parseDouble(friend_idlast)) {
            mReference1 = FirebaseDatabase.getInstance()

                    //   https://decoded-reducer-294611.firebaseio.com/
                    .getReferenceFromUrl("https://vibras-f3c99-default-rtdb.firebaseio.com/chat_ios/" + "messages" + "_" + useriddeivce + "_" + friend_idlast);
        } else {
            mReference1 = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://vibras-f3c99-default-rtdb.firebaseio.com/chat_ios/" + "messages" + "_" + friend_idlast + "_" + useriddeivce);
        }
        if (mReference1 != null) {
            mReference1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChangeUNSEENCOUNTSTATUS();

                    //    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator =
                            new GenericTypeIndicator<Map<String, String>>() {
                            };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                    Log.e("ttttt", map + "");

                    if (map != null) {
                        String message = map.get("message").toString();
                        String senderID = map.get("senderID").toString();
                        String receiveerID = map.get("receiveerID").toString();
                        String username = map.get("username").toString();
                        String image = map.get("image").toString();
                        String video = map.get("video").toString();
                        String time = map.get("time").toString();
                        String status = map.get("status").toString();
                        String Lattitude = map.get("lattitude").toString();
                        String longitude = map.get("longitude").toString();
                        ChatMessage chatMessage = new ChatMessage(senderID,
                                receiveerID, message, username, image, video, time,
                                "", friendimage, session.getChatImage(), Lattitude,
                                longitude);
                        Log.e("insertID", "onDataChange: " + message);
                        allmessagelist.add(chatMessage);
                        System.out.println("conditiionif");
                        chatListAdapter = new ChatListAdapter(
                                ChatInnerMessagesActivity.this, allmessagelist);
                        chat_messages_list.setLayoutManager(new LinearLayoutManager(
                                ChatInnerMessagesActivity.this, RecyclerView.VERTICAL, false));
                        chat_messages_list.setItemAnimator(new DefaultItemAnimator());
                        chat_messages_list.setAdapter(chatListAdapter);
                        chat_messages_list.setItemViewCacheSize(allmessagelist.size());
                        chatListAdapter.notifyDataSetChanged();
                        chat_messages_list.scrollToPosition(chat_messages_list.getAdapter().getItemCount() - 1);

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mReference.child("count").child(friend_idlast).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot chatt : snapshot.getChildren()) {
                    Log.e("Status", "onDataChange: " + chatt.getValue(String.class));

                    // statustextshow.setText(chatt.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        channelName = getAlphaNumericString(10);
        token = getToken();
        call_relat.setOnClickListener(v -> {
            Log.e(TAG, "onCreate: channelName ----" + channelName);
            Log.e(TAG, "onCreate: token----------" + token);
            if (NetworkAvailablity.checkNetworkStatus(ChatInnerMessagesActivity.this)) {
                addNotification("Video");
              /*  startActivity(new Intent(ChatInnerMessagesActivity.this, VideoCallingAct.class).putExtra(session.getUserId(),friend_idlast)
                        .putExtra("channel_name",channelName) .putExtra("token",token)
                        .putExtra("from","user"));*/
            } else {
                Toast.makeText(ChatInnerMessagesActivity.this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
            }

        });
        audio_call_rela.setOnClickListener(v -> {
            Log.e(TAG, "onCreate: channelName ----" + channelName);
            Log.e(TAG, "onCreate: token----------" + token);
            if (NetworkAvailablity.checkNetworkStatus(ChatInnerMessagesActivity.this)) {
                addNotification("Audio");
              /*  startActivity(new Intent(ChatInnerMessagesActivity.this, VideoCallingAct.class).putExtra(session.getUserId(),friend_idlast)
                        .putExtra("channel_name",channelName) .putExtra("token",token)
                        .putExtra("from","user"));*/
            } else {
                Toast.makeText(ChatInnerMessagesActivity.this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
            }

        });
      /*  if (unique_id != null) {
            mReference.child("chats").child("messages_" + useriddeivce + "_" + friend_idlast)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                for (DataSnapshot chatt : snapshot.getChildren()) {
                                    //    if (status.equalsIgnoreCase("1")) {
                                    ChangeUNSEENCOUNTSTATUS();
                                    // }
                                    String message = chatt.child("message").getValue(String.class);
                                    String senderID = chatt.child("senderID").getValue(String.class);
                                    String receiveerID = chatt.child("receiveerID").getValue(String.class);
                                    String username = chatt.child("username").getValue(String.class);
                                    String image = chatt.child("image").getValue(String.class);
                                    String video = chatt.child("video").getValue(String.class);
                                    String time = chatt.child("time").getValue(String.class);
                                    String status = chatt.child("status").getValue(String.class);
                                    String Lattitude = chatt.child("lattitude").getValue(String.class);
                                    String longitude = chatt.child("longitude").getValue(String.class);
                                    // FireCons.currentuser=currentuser;
                                    Log.e("useriddeivce", "onDataChange: " + useriddeivce);
                                    Log.e("-->>message", "onDataChange: " + image);
                                    Log.e("-->>message", "onDataChange: " + message);
                                    Log.e("-->>kisko", "onDataChange: " + receiveerID);
                                    Log.e("-->>kisne", "onDataChange: " + senderID);
                                    if (useriddeivce.equalsIgnoreCase(senderID) &&
                                            friend_idlast.equalsIgnoreCase(receiveerID)) {
                                        ChatMessage chatMessage = new ChatMessage(senderID,
                                                receiveerID, message, username, image, video, time,
                                                "", friendimage, session.getChatImage(), Lattitude,
                                                longitude);
                                        Log.e("insertID", "onDataChange: " + message);
                                        allmessagelist.add(chatMessage);

                                        System.out.println("conditiionif");

                                    } else if (useriddeivce.equals(receiveerID) && friend_idlast.equals(senderID)) {
                                        ChatMessage chatMessage = new ChatMessage(senderID,
                                                receiveerID, message, username, image, video, time,
                                                "", friendimage, session.getChatImage(), Lattitude,
                                                longitude);
                                        allmessagelist.add(chatMessage);
                                        System.out.println("conditiionelseif");
                                    } else {
                                        System.out.println("else<><><><userid");

                                    }
                                    chatListAdapter = new ChatListAdapter(
                                            ChatInnerMessagesActivity.this, allmessagelist);
                                    chat_messages_list.setLayoutManager(new LinearLayoutManager(ChatInnerMessagesActivity.this, RecyclerView.VERTICAL, false));
                                    chat_messages_list.setItemAnimator(new DefaultItemAnimator());
                                    chat_messages_list.setAdapter(chatListAdapter);
                                    chat_messages_list.setItemViewCacheSize(allmessagelist.size());
                                    chatListAdapter.notifyDataSetChanged();
                                    chat_messages_list.scrollToPosition(chat_messages_list.getAdapter().getItemCount() - 1);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });
        }
        if (unique_id != null) {
            mReference.child("chats").child("messages_" + friend_idlast + "_" + useriddeivce)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                for (DataSnapshot chatt : snapshot.getChildren()) {
                                    //    if (status.equalsIgnoreCase("1")) {
                                    ChangeUNSEENCOUNTSTATUS();
                                    // }
                                    String message = chatt.child("message").getValue(String.class);
                                    String senderID = chatt.child("senderID").getValue(String.class);
                                    String receiveerID = chatt.child("receiveerID").getValue(String.class);
                                    String username = chatt.child("username").getValue(String.class);
                                    String image = chatt.child("image").getValue(String.class);
                                    String video = chatt.child("video").getValue(String.class);
                                    String time = chatt.child("time").getValue(String.class);
                                    String status = chatt.child("status").getValue(String.class);
                                    String Lattitude = chatt.child("lattitude").getValue(String.class);
                                    String longitude = chatt.child("longitude").getValue(String.class);

                                    // FireCons.currentuser=currentuser;
                                    Log.e("useriddeivce", "onDataChange: " + useriddeivce);


                                    Log.e("-->>message", "onDataChange: " + image);
                                    Log.e("-->>message", "onDataChange: " + message);
                                    Log.e("-->>kisko", "onDataChange: " + receiveerID);
                                    Log.e("-->>kisne", "onDataChange: " + senderID);
                                    if (useriddeivce.equalsIgnoreCase(senderID) &&
                                            friend_idlast.equalsIgnoreCase(receiveerID)) {
                                        ChatMessage chatMessage = new ChatMessage(senderID,
                                                receiveerID, message, username, image, video, time,
                                                "", friendimage, session.getChatImage(), Lattitude,
                                                longitude);
                                        Log.e("insertID", "onDataChange: " + message);
                                        allmessagelist.add(chatMessage);
                                        System.out.println("conditiionif");

                                    } else if (useriddeivce.equals(receiveerID) && friend_idlast.equals(senderID)) {
                                        ChatMessage chatMessage = new ChatMessage(senderID,
                                                receiveerID, message, username, image, video, time,
                                                "", friendimage, session.getChatImage(), Lattitude,
                                                longitude);
                                        allmessagelist.add(chatMessage);
                                        System.out.println("conditiionelseif");
                                    } else {
                                        System.out.println("else<><><><userid");

                                    }
                                    chatListAdapter = new ChatListAdapter(
                                            ChatInnerMessagesActivity.this, allmessagelist);
                                    chat_messages_list.setLayoutManager(new LinearLayoutManager(
                                            ChatInnerMessagesActivity.this, RecyclerView.VERTICAL, false));
                                    chat_messages_list.setItemAnimator(new DefaultItemAnimator());
                                    chat_messages_list.setAdapter(chatListAdapter);
                                    chat_messages_list.setItemViewCacheSize(allmessagelist.size());
                                    chatListAdapter.notifyDataSetChanged();
                                    chat_messages_list.scrollToPosition(chat_messages_list.getAdapter().getItemCount() - 1);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });
        }*/
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "2";
                onBackPressed();
            }
        });
        //    mReference.child("type" + useriddeivce + "To" + friend_idlast).setValue("false");
        Log.e(TAG, "onCreate: ----------------------______+++&&&&&(((((((((((" +
                " " + "type" + friend_idlast + "To"
                + session.getUserId());
        mReference.child("typing").child("type" + friend_idlast + "To"
                + session.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("UnReadCount->00", "typetypetypetype: " + snapshot.getValue());
                Log.e("UnReadCount->01", "typetypetypetype: " + snapshot.getClass());
                if (snapshot.getValue() != null) {
                    Log.e(Constants.TAG, "TypingTypingTypingTyping: " + snapshot.getValue());
                    if (snapshot.getValue().toString().equalsIgnoreCase("true")) {
                        typing_layout.setVisibility(View.VISIBLE);
                    } else {
                        typing_layout.setVisibility(View.GONE);

                    }

                    /*String[] parts = snapshot.getValue().toString().split("=");

                    String part2 = parts[1];

                    String unreadstatus = part2.replace("}", "");*/
                    //    Log.e("statusget", "UnReadCount: " + unreadstatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        chatmessage_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Map<String, Object> params = new HashMap<>();
                params.put("first_user", charSequence.length() > 0);
                mReference.child("typing").child("type" + useriddeivce + "To" + friend_idlast).setValue("true");
               /* HashMap<String, Object> HashMap = new HashMap<String, Object>();
                HashMap.put("UnReadMessageStatus", 1);*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
              /*  Map<String, Object> params = new HashMap<>();
                params.put("first_user", charSequence.length() > 0);
               */// mReference.child("type" + useriddeivce + "To" + friend_idlast).setValue("false");

                //     mReference.child("From" + useriddeivce + "To" + friend_idlast).child("Typing").setValue(false);

            }
        });
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("UserID", "onClick: " + useriddeivce);
                Log.e("friendID", "onClick: " + friend_idlast);

                if (!useriddeivce.equalsIgnoreCase(friend_idlast)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(" hh:mm");
                    Log.e("TAG", "onClick: " + dateFormat.format(new Date()));
                    String time = dateFormat.format(new Date());
                    String messagesend = (chatmessage_edit.getText().toString());
                    if (!messagesend.equalsIgnoreCase("")) {
                        Log.e("sendmessage", "onClick: ");
                       mReference1 .push()
                                .setValue(new ChatMessage(useriddeivce, friend_idlast, messagesend, friendnamelast,
                                        "", "", time, "", friendimage, session.getChatImage(),
                                        "0.0", "0.0"));
                        chatmessage_edit.setText("");
                        sendmessage(useriddeivce, messagesend, friend_idlast);
                        SETUNSEENCOUNTSTATUS();
                    }
                } else {
                    Toast.makeText(ChatInnerMessagesActivity.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                }

            }
        });
        live_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatInnerMessagesActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constant.LOCATION_REQUEST);
                } else {

                    final CharSequence[] options = {"Share Current Location", "Pick on Map", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatInnerMessagesActivity.this);
                    builder.setTitle("Share Location!");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Share Current Location")) {
                                if (gpsTracker.canGetLocation()) {
                                    Log.e("sendmessage", "onClick: ");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(" hh:mm");
                                    Log.e("TAG", "onClick: " + dateFormat.format(new Date()));
                                    String time = dateFormat.format(new Date());
                                    LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLatitude());
                                    mReference1  .push()
                                            .setValue(new ChatMessage(useriddeivce, friend_idlast,
                                                    "", friendnamelast,
                                                    "", "", time, "",
                                                    friendimage, session.getChatImage(),
                                                    "" + gpsTracker.getLatitude()
                                                    , "" + gpsTracker.getLongitude()));
                                    chatmessage_edit.setText("");
                                } else {


                                }


                            } else if (options[item].equals("Pick on Map")) {

                                startActivity(new Intent(ChatInnerMessagesActivity.this,
                                        SearchLocationMapAct.class)
                                        .putExtra("from", "121")
                                        .putExtra("useriddeivce", useriddeivce)
                                        .putExtra("friend_idlast", friend_idlast)
                                        .putExtra("friendnamelast", friendnamelast)
                                        .putExtra("friendimage", friendimage)
                                        .putExtra("getChatImage", session.getChatImage()));

                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatInnerMessagesActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 123);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {


                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                Log.e("BITMAAP", "onActivityResult: " + scaled);
                Uri img = getImageUri(ChatInnerMessagesActivity.this, scaled);
                //String base64String = bitmapToBase64(scaled);
                uploadImage(img);
                //  Log.e("base64String", "onActivityResult: " + );


            } else if (requestCode == 123) {

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(ChatInnerMessagesActivity.this.getContentResolver(), data.getData());
                    int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    Log.e("BITMAAP", "onActivityResult: " + bitmap);
                    Uri img = getImageUri(ChatInnerMessagesActivity.this, scaled);
                    //String base64String = bitmapToBase64(scaled);
                    uploadImage(img);
                   /* FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chat").child("message_"+useriddeivce+"to"+friend_idlast)
                            .push()
                            .setValue(new ChatMessage(useriddeivce, friend_idlast, "", friendnamelast, base64String, "", "", "",
                                    friendimage, session.getChatImage(), "0.0", "0.0"));
                    chatmessage_edit.setText("");*/
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        } catch (Exception exception) {
            Log.e("-->>", "onActivityResult: " + exception.getMessage());
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void SETUNSEENCOUNTSTATUS() {
        mReference.child("typing").child("type" + useriddeivce + "To" + friend_idlast).setValue("false");
        typing_layout.setVisibility(View.GONE);
        mReference.child("count").child("From" + useriddeivce + "To" + friend_idlast)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        Log.e("UnReadCount->00", "UnReadCount: " + snapshot.getValue());
                        Log.e("UnReadCount->01", "UnReadCount: " + snapshot.getClass());
                        if (snapshot.getValue() != null) {
                            String[] parts = snapshot.getValue().toString().split("=");

                            String part2 = parts[1];


                            messageunreadcount = Integer.parseInt(part2.replace("}", ""));
                            Log.e("onDataChange-0", "onDataChange: " + messageunreadcount);

                            HashMap<String, Object> HashMap = new HashMap<String, Object>();
                            HashMap.put("UnReadMessageStatus", messageunreadcount + 1);
                            // mReference.child("From" + useriddeivce + "To" + friend_idlast).removeValue();
                            mReference.child("count").child("From" + useriddeivce + "To" + friend_idlast).setValue(HashMap);

                        } else {
                            HashMap<String, Object> HashMap = new HashMap<String, Object>();
                            HashMap.put("UnReadMessageStatus", 1);
                            // mReference.child("From" + useriddeivce + "To" + friend_idlast).removeValue();
                            mReference.child("count").child("From" + useriddeivce + "To" + friend_idlast).setValue(HashMap);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Log.e("SETUNSEENCOUNTSTATUS", "SETUNSEENCOUNTSTATUS: " + useriddeivce);


        //  mReference.child("From" + useriddeivce + "To" + friend_idlast).update({'dateOfBirth': moment(value.dateOfBirth).toDate().getTime()})
    }

    private void ChangeUNSEENCOUNTSTATUS() {
        Log.e("manageconnection", "manageconnection: " + useriddeivce);
        HashMap<String, Object> HashMap = new HashMap<String, Object>();
        HashMap.put("UnReadMessageStatus", 0);
        mReference.child("count").child("From" + friend_idlast + "To" + useriddeivce).setValue(HashMap);
    }

    private void sendmessage(String UserId, String message, String FriednId) {
        //  DataManager.getInstance().showProgressMessage(ChatInnerMessagesActivity.this,getString(R.string.please_wait));
        RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), UserId);
        RequestBody receiverId = RequestBody.create(MediaType.parse("text/plain"), FriednId);
        RequestBody messageText = RequestBody.create(MediaType.parse("text/plain"), StringEscapeUtils.escapeJava(
                message));
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "Text");
        RequestBody caption = RequestBody.create(MediaType.parse("text/plain"), "");
        Call<SuccessResInsertChat> loginCall = apiInterface.insertImageVideoChat(senderId, receiverId, messageText, type);
        loginCall.enqueue(new Callback<SuccessResInsertChat>() {
            @Override
            public void onResponse(Call<SuccessResInsertChat> call, Response<SuccessResInsertChat> response) {
                //   DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResInsertChat data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        // showToast(ChatInnerMessagesActivity.this, data.message);

                        // dialog.dismiss();

                    } else if (data.status.equals("0")) {
                        //   showToast(ChatInnerMessagesActivity.this, data.message);
                        //  dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResInsertChat> call, Throwable t) {
                call.cancel();
                // DataManager.getInstance().hideProgressMessage();
                // dialog.dismiss();
            }
        });
    }


    @Override
    public void onBackPressed() {
        status = "2";
        Log.e("hererer", "onBackPressed:");
        //   mReference.child("type" + useriddeivce + "To" + friend_idlast).setValue("false");
        super.onBackPressed();
    }

    public String getToken() {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);

        Log.d("TAG", "onCreate: My Token1" + result);

        result = token.buildTokenWithUid(appId, appCertificate,
                channelName, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);

        Log.d("TAG", "onCreate: My Token2" + result);

        return result;
    }

    public void addNotification(String type) {
        String userId = SharedPreferenceUtility.getInstance(ChatInnerMessagesActivity.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(ChatInnerMessagesActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("sender_id", userId);
        map.put("receiver_id", friend_idlast);
        map.put("username", session.getChatName());
        map.put("token", token);
        map.put("channel", channelName);
        map.put("call_type", type);

        //  sender_id=1&receiver_id=3&username=testuser&token=this123ve&channel=testchannel
        Call<SuccessResMakeCall> call = apiInterface.addNotification(map);
        call.enqueue(new Callback<SuccessResMakeCall>() {
            @Override
            public void onResponse(Call<SuccessResMakeCall> call, Response<SuccessResMakeCall> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    SuccessResMakeCall data = response.body();
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        if (type.equalsIgnoreCase("Video")) {
                            startActivity(new Intent(ChatInnerMessagesActivity.this,
                                    VideoCallingAct.class).putExtra("id"
                                            , friend_idlast)
                                    .putExtra("channel_name", channelName)
                                    .putExtra("token", token)
                                    .putExtra("id", friend_idlast)
                                    .putExtra("from", "user")
                                    .putExtra("name", friendnamelast)
                                    .putExtra("userimage", friendimage)
                                    .putExtra("call_type", type)
                                    .putExtra("Profile", 0));
                            finish();
                        } else {
                            startActivity(new Intent(ChatInnerMessagesActivity.this,
                                    VoiceChatViewActivity.class).putExtra("id"
                                            , friend_idlast)
                                    .putExtra("channel_name", channelName)
                                    .putExtra("token", token)
                                    .putExtra("call_type", type)
                                    .putExtra("Profile", 0)
                                    .putExtra("name", friendnamelast)
                                    .putExtra("userimage", friendimage)
                                    .putExtra("from", "user"));
                            finish();
                        }


                    } else {
                        showToast(ChatInnerMessagesActivity.this, data.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResMakeCall> call, Throwable t) {

                call.cancel();
                DataManager.getInstance().hideProgressMessage();

            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("hererer", "onDestroy:");
        mReference.child("typing").child("type" + useriddeivce + "To" + friend_idlast).setValue("false");
        super.onDestroy();
    }

    // UploadImage method
    private void uploadImage(Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("image/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uri.isComplete());
                                Uri url = uri.getResult();
                                Log.e("FBApp1 URL ", url.toString());
                               String sendImg = url.toString();
                                if (!sendImg.equals("")) {
                                    mReference1 .push()
                                            .setValue(new ChatMessage(useriddeivce, friend_idlast, "", friendnamelast,
                                                    sendImg, "", "", "", friendimage, session.getChatImage(),
                                                    "0.0", "0.0"));

                                }
                                progressDialog.dismiss();

                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int) progress + "%");

                    });
        }
    }

    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(ChatInnerMessagesActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(ChatInnerMessagesActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(ChatInnerMessagesActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatInnerMessagesActivity.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(ChatInnerMessagesActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ChatInnerMessagesActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)

            ) {

                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            } else {
                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {
            //  explain("Please Allow Location Permission");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Latittude====", gpsTracker.getLatitude() + "");
                    live_location.performClick();

                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.permisson_denied),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            }

            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
//                        showImageSelection();
                        //  openCamera();

                    } else {
                        Toast.makeText(ChatInnerMessagesActivity.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatInnerMessagesActivity.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private static final int MY_PERMISSION_CONSTANT = 5;

}