package com.my.vibras.chat.GroupChat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.act.SearchLocationMapAct;
import com.my.vibras.act.ui.GroupDetailAct;
import com.my.vibras.adapter.GroupOne2OneChatAdapter;
import com.my.vibras.chat.ChatInnerMessagesActivity;
import com.my.vibras.chat.ChatMessage;
import com.my.vibras.databinding.ActivityGroupChatBinding;
import com.my.vibras.model.SuccessResGetGroupChat;
import com.my.vibras.model.SuccessResInsertGroupChat;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.GPSTracker;
import com.my.vibras.utility.Session;
import com.my.vibras.utility.SharedPreferenceUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;

public class GroupChatInnerActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    private DatabaseReference mReference;

    private String id = "", name = "";

    private VibrasInterface apiInterface;

    RecyclerView chat_messages_list;
    GroupChatAdaapter chatListAdapter;
    ImageView live_location;
    GPSTracker gpsTracker;
    private ArrayList<SuccessResGetGroupChat.Result> chatList = new ArrayList<>();
    ArrayList<ChatMessage> allmessagelist = new ArrayList<>();
    Session session;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        binding.RRFrnd.setOnClickListener(v -> finish());
        chat_messages_list = findViewById(R.id.rvMessageItem);
        binding.txtName.setText(name);
        session = new Session(this);
        mReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        checkPermisssionForReadStorage();

        gpsTracker = new GPSTracker(this);
        live_location = findViewById(R.id.live_location);
        binding.txtName.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), GroupDetailAct.class)
                    .putExtra("id", id));
        });
        binding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    /*    mReference.child("group_typing")
                .child(id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Map<String, Object> params = new HashMap<>();
                params.put("first_user", charSequence.length() > 0);
                mReference.child("group_typing")
                        .child(id).child(session.getUser_name()).setValue("true");
               *//* HashMap<String, Object> HashMap = new HashMap<String, Object>();
                HashMap.put("UnReadMessageStatus", 1);*//*
            }

            @Override
            public void afterTextChanged(Editable editable) {
              *//*  Map<String, Object> params = new HashMap<>();
                params.put("first_user", charSequence.length() > 0);
               *//*// mReference.child("type" + useriddeivce + "To" + friend_idlast).setValue("false");

                //     mReference.child("From" + useriddeivce + "To" + friend_idlast).child("Typing").setValue(false);

            }
        });*/
        live_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(GroupChatInnerActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constant.LOCATION_REQUEST);
                } else {

                    final CharSequence[] options = {getString(R.string.share_current_location),
                            getString(R.string.pick_on_map), getString(R.string.cancel)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatInnerActivity.this);
                    builder.setTitle(R.string.share_location);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.share_current_location))) {
                                if (gpsTracker.canGetLocation()) {
                                    Log.e("sendmessage", "onClick: ");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(" hh:mm");
                                    Log.e("TAG", "onClick: " + dateFormat.format(new Date()));
                                    String time = dateFormat.format(new Date());
                                    LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLatitude());
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("group_chat").child(id)
                                            .push()
                                            .setValue(new ChatMessage(session.getUserId(), "group",
                                                    "", session.getChatName()
                                                    , "", "", time, "", session.getChatImage(),
                                                    session.getChatImage(), "" + latLng.latitude,
                                                    "" + latLng.longitude));
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.turn_on_gps, Toast.LENGTH_SHORT).show();
                                }


                            } else if (options[item].equals(getString(R.string.pick_on_map))) {

                                startActivity(new Intent(GroupChatInnerActivity.this,
                                        SearchLocationMapAct.class)
                                        .putExtra("from", "group")
                                        .putExtra("id", id));

                            } else if (options[item].equals(getString(R.string.cancel))) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        if (id != null) {
            mReference.child("group_chat").child(id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                allmessagelist.clear();
                                for (DataSnapshot chatt : snapshot.getChildren()) {
                                    String message = chatt.child("message").getValue(String.class);
                                    String senderID = chatt.child("senderID").getValue(String.class);
                                    String receiveerID = chatt.child("receiveerID").getValue(String.class);
                                    String username = chatt.child("username").getValue(String.class);
                                    String image = chatt.child("image").getValue(String.class);
                                    String video = chatt.child("video").getValue(String.class);
                                    String time = chatt.child("time").getValue(String.class);
                                    String status = chatt.child("status").getValue(String.class);
                                    String friendImage = chatt.child("friendImage").getValue(String.class);
                                    String lattitude = chatt.child("lattitude").getValue(String.class);
                                    String longitude = chatt.child("longitude").getValue(String.class);
                                    // FireCons.currentuser=currentuser;
                                    Log.e("useriddeivce", "onDataChange: " + session.getUserId());
                                    Log.e("-->>message", "onDataChange: " + image);
                                    Log.e("-->>message", "onDataChange: " + message);
                                    Log.e("-->>kisko", "onDataChange: " + receiveerID);
                                    Log.e("-->>kisne", "onDataChange: " + senderID);
                                    ChatMessage chatMessage = new ChatMessage(senderID,
                                            receiveerID, message, username, image, video, time,
                                            "", friendImage, session.getChatImage(), lattitude, longitude);
                                    Log.e("insertID", "onDataChange: " + message);

                                    allmessagelist.add(chatMessage);

                                    System.out.println("conditiionif");

                                    chatListAdapter = new GroupChatAdaapter(GroupChatInnerActivity
                                            .this, allmessagelist);
                                    RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(
                                            GroupChatInnerActivity.this);
                                    chat_messages_list.setLayoutManager(mLayoutManger);
                                    chat_messages_list.setLayoutManager(new LinearLayoutManager(
                                            GroupChatInnerActivity.this, RecyclerView.VERTICAL, false));
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


        binding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(" hh:mm");
                Log.e("TAG", "onClick: " + dateFormat.format(new Date()));
                String time = dateFormat.format(new Date());
                String messagesend = (binding.etText.getText().toString());
                if (!messagesend.equalsIgnoreCase("")) {
                    Log.e("sendmessage", "onClick: ");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("group_chat").child(id)
                            .push()
                            .setValue(new ChatMessage
                                    (session.getUserId(), "group",
                                            messagesend, session.getChatName(),
                                            "", "", time, ""
                                            , session.getChatImage(), session.getChatImage(), "0.0", "0.0"));
                    //  sendmessage(useriddeivce, messagesend, friend_idlast);
                    uploadImageVideoPost(messagesend);
                    binding.etText.setText("");

                }
            }
        });
        binding.llRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.select_from_gallery), getString(R.string.cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatInnerActivity.this);
                builder.setTitle(getString(R.string.add_photos));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getString(R.string.take_photo))) {
                            if (checkPermisssionForReadStorage()){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            startActivityForResult(intent, 1);}else {

                            }
                        } else if (options[item].equals(getString(R.string.select_from_gallery))) {
                            if (checkPermisssionForReadStorage()){
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 1000);}else {

                            }
                        } else if (options[item].equals(getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    public static String encodeEmoji(String message) {
        try {
            return URLEncoder.encode(message,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
                Log.e("BITMAAP", "onActivityResult: " + scaled);
                Uri img = getImageUri(GroupChatInnerActivity.this, scaled);
                //String base64String = bitmapToBase64(scaled);
                uploadImage(img);
             //   String base64String = bitmapToBase64(scaled);
              //  Log.e("base64String", "onActivityResult: " + base64String);


            } else if (requestCode == 1000) {

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(GroupChatInnerActivity
                            .this.getContentResolver(), data.getData());
                    int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    Log.e("BITMAAP", "onActivityResult: " + bitmap);
                    Uri img = getImageUri(GroupChatInnerActivity.this, scaled);
                    //String base64String = bitmapToBase64(scaled);
                    uploadImage(img);


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
    private boolean isLastVisible() {
        if (chatList != null && chatList.size() != 0) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) binding.rvMessageItem.getLayoutManager());
            int pos = layoutManager.findLastCompletelyVisibleItemPosition();
            int numItems = binding.rvMessageItem.getAdapter().getItemCount();
            return (pos >= numItems - 1);
        }
        return false;
    }

    private void getChat() {
        String userId = SharedPreferenceUtility.getInstance(GroupChatInnerActivity.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("group_id", id);
        Call<SuccessResGetGroupChat> call = apiInterface.getGroupChat(map);
        call.enqueue(new Callback<SuccessResGetGroupChat>() {
            @Override
            public void onResponse(Call<SuccessResGetGroupChat> call, Response<SuccessResGetGroupChat> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetGroupChat data = response.body();
                    Log.e("data", data.status + "");
                    if (data.status == 1) {
                        String dataResponse = new Gson().toJson(response.body());
                        chatList.clear();
                        chatList.addAll(data.getResult());
                        binding.rvMessageItem.setLayoutManager(new LinearLayoutManager(GroupChatInnerActivity.this));
                        binding.rvMessageItem.setAdapter(new GroupOne2OneChatAdapter(GroupChatInnerActivity.this, chatList, userId));
                        binding.rvMessageItem.scrollToPosition(chatList.size() - 1);
                    } else if (data.status == 0) {
                        showToast(GroupChatInnerActivity.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetGroupChat> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public void uploadImageVideoPost(String txt) {
        String strUserId = SharedPreferenceUtility.getInstance(GroupChatInnerActivity.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(GroupChatInnerActivity.this, getString(R.string.please_wait));
        RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), session.getUserId());
        RequestBody receiverId = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody messageText = RequestBody.create(MediaType.parse("text/plain"), txt);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "text");
        RequestBody caption = RequestBody.create(MediaType.parse("text/plain"), "");
        Call<SuccessResInsertGroupChat> loginCall = apiInterface.insertGroupImageVideoChat
                (senderId, receiverId, messageText, type);
        loginCall.enqueue(new Callback<SuccessResInsertGroupChat>() {
            @Override
            public void onResponse(Call<SuccessResInsertGroupChat> call, Response<SuccessResInsertGroupChat> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResInsertGroupChat data = response.body();
                    Log.e("data", data.status);
                    //   showToast(GroupChatInnerActivity.this, data.result);
                    //  binding.etText.setText("");
                    // getChat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResInsertGroupChat> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
                //  getChat();
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
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
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uri.isComplete());
                                Uri url = uri.getResult();
                                Log.e("FBApp1 URL ", url.toString());
                                String sendImg = url.toString();
                                if (!sendImg.equals("")) {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("group_chat").child(id)
                                            .push()
                                            .setValue(new ChatMessage(session.getUserId(), "group",
                                                    "", session.getChatName()
                                                    , sendImg, "", "", "", session.getChatImage(),
                                                    session.getChatImage(), "0.0", "0.0"));
                                }

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
        if (ContextCompat.checkSelfPermission(GroupChatInnerActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(GroupChatInnerActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(GroupChatInnerActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupChatInnerActivity.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(GroupChatInnerActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(GroupChatInnerActivity.this,
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
                        Toast.makeText(GroupChatInnerActivity.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GroupChatInnerActivity.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private static final int MY_PERMISSION_CONSTANT = 5;
   /* @Override
    protected void onStop() {
        Log.e("hererer", "onStop:");
        mReference.child("group_typing")
                  .child(id)
                  .child(session.getUserId())
                  .setValue("false");
        super.onStop();
    }*/

}