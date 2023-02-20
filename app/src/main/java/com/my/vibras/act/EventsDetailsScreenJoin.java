package com.my.vibras.act;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.adapter.EventCommentAdapter;
import com.my.vibras.adapter.EventsImagesAdapter;
import com.my.vibras.databinding.ActivityEventsDetailsScreenBinding;
import com.my.vibras.model.SuccessResAddLike;
import com.my.vibras.model.SuccessResGetEventComment;
import com.my.vibras.model.SuccessResGetEvents;
import com.my.vibras.model.SuccessResMyJoinedEvents;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;
import static io.agora.rtc.gl.VideoFrame.TextureBuffer.TAG;

public class EventsDetailsScreenJoin extends AppCompatActivity implements OnMapReadyCallback {

    ActivityEventsDetailsScreenBinding binding;

    GoogleMap gMap;

    private SuccessResMyJoinedEvents.EventDetails requestModel;
    private SuccessResMyJoinedEvents.Result dert;

    private EventsImagesAdapter multipleImagesAdapter;

    private ArrayList<String> imagesList = new ArrayList<>();

    private VibrasInterface apiInterface;

    private EventCommentAdapter commentAdapter;

    private ArrayList<SuccessResGetEventComment.Result> commentList = new ArrayList<>();

    String strLat = "", strLng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_events_details_screen);


        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        Intent in = getIntent();
        if (in != null) {
            String result = in.getStringExtra("data");
            dert = new Gson().fromJson(result, SuccessResMyJoinedEvents.Result.class);
            requestModel=dert.getEventDetails();
        }

        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.txtViewAllComents.setOnClickListener(v -> {
            startActivity(new Intent(EventsDetailsScreenJoin.this, AllCommentsAct.class)
                    .putExtra("from", "events").putExtra("id", requestModel.getId()));
        });

        binding.llPostComent.setOnClickListener(v -> {
            startActivity(new Intent(EventsDetailsScreenJoin.this, PostCommentAct.class)
                    .putExtra("from", "events").putExtra("id", requestModel.getId()));
        });

        Glide.with(EventsDetailsScreenJoin.this)
                .load(requestModel.getImage())
                .into(binding.ivEvent);

        binding.tvEventName.setText(requestModel.getEventName());
        try {
            Log.e(TAG, "onCreate: " + requestModel.getDateTimeEvent());
            Log.e(TAG, "onCreate: " + parseDateToddMMyyyy(requestModel.getDateTimeEvent()));
            binding.tvEventDate.setText(parseDateToddMMyyyy(requestModel.getDateTimeEvent()));
        } catch (Exception e) {
            e.printStackTrace();
            binding.tvEventDate.setText(requestModel.getDateTimeEvent());
            Log.e("TAG", "onCreate: " + e.getLocalizedMessage());
        }
        binding.tvEventTime.setText(requestModel.getEventStartTime());

        binding.eventLocation.setText(requestModel.getAddress());

        binding.tvDetails.setText(requestModel.getDescription());
        binding.phone.setText(requestModel.getMobile());

        if (dert.getLikeStatus() != null && dert.getLikeStatus().equalsIgnoreCase("false")) {
            binding.ivLikes.setImageResource(R.drawable.likedd);
        } else {
            binding.ivLikes.setImageResource(R.drawable.liked_yes);
        }

        strLat = requestModel.getLat();
        strLng = requestModel.getLon();


        binding.tvLikesCount.setText(dert.getTotalLike() + "");
        binding.tvCommentCount.setText(dert.getTotalComments() + "");

        imagesList.clear();
        for (SuccessResMyJoinedEvents.EventGallery eventGallery : dert.getEventGallery()) {
            imagesList.add(eventGallery.getImageFile());
        }

        multipleImagesAdapter = new EventsImagesAdapter(EventsDetailsScreenJoin.this, imagesList);
        binding.rvImages.setHasFixedSize(true);
        binding.rvImages.setLayoutManager(new LinearLayoutManager(EventsDetailsScreenJoin.this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvImages.setAdapter(multipleImagesAdapter);

        commentAdapter = new EventCommentAdapter(EventsDetailsScreenJoin.this, commentList);
        binding.rvComments.setHasFixedSize(true);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(EventsDetailsScreenJoin.this));
        binding.rvComments.setAdapter(commentAdapter);

        getComment();

        binding.llLikeEvent.setOnClickListener(v ->
                {
                    addLike(requestModel.getId());
                }
        );

        binding.cvSignup.setOnClickListener(v ->
                {
                    joinEvent();
                }
        );
        binding.RLogin.setOnClickListener(v ->
                {
                    joinEvent();
                }
        );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(EventsDetailsScreenJoin.this);
      /*  if (dert.getMemberId() != null) {
           // if (requestModel.getIammember().equalsIgnoreCase("No")) {
                binding.cvSignup.setVisibility(View.VISIBLE);
                binding.RLogin.setVisibility(View.VISIBLE);

            } else {*/
                binding.cvSignup.setVisibility(View.GONE);
                binding.RLogin.setVisibility(View.GONE);
           // }
        //} else {
          //  binding.cvSignup.setVisibility(View.VISIBLE);

     //   }

    }

    public void joinEvent() {

        String userId = SharedPreferenceUtility.getInstance(EventsDetailsScreenJoin.this).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(EventsDetailsScreenJoin.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("event_id", requestModel.getId());
        map.put("member_id", userId);
        Call<ResponseBody> call = apiInterface.joinEvent(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DataManager.getInstance().hideProgressMessage();

                try {
//                    SuccessResAddComment data = response.body();

                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String data = jsonObject.getString("status");

                    String message = jsonObject.getString("message");

                    if (data.equalsIgnoreCase("1")) {

                        String dataResponse = new Gson().toJson(response.body());

                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);

                        binding.cvSignup.setVisibility(View.GONE);

                    } else if (data.equalsIgnoreCase("0")) {
                        showToast(EventsDetailsScreenJoin.this, message);
                    }
                } catch (Exception e) {
                    Log.d("TAG", "onResponse: " + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void addLike(String postId) {

        String userId = SharedPreferenceUtility.getInstance(EventsDetailsScreenJoin.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(EventsDetailsScreenJoin.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("event_id", postId);
        Call<SuccessResAddLike> call = apiInterface.addEventLike(map);
        call.enqueue(new Callback<SuccessResAddLike>() {
            @Override
            public void onResponse(Call<SuccessResAddLike> call, Response<SuccessResAddLike> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddLike data = response.body();
                    Log.e("data", data.status + " : likeUnlike");

                    if (data.status == 0) {
                        binding.ivLikes.setImageResource(R.drawable.ic_rest_unlike);
                    } else {
                        binding.ivLikes.setImageResource(R.drawable.ic_rest_like);
                    }

                    binding.tvLikesCount.setText(data.getTotalLikes());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResAddLike> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void getComment() {
        DataManager.getInstance().showProgressMessage(EventsDetailsScreenJoin.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("event_id", dert.getId());
        boolean val = SharedPreferenceUtility.getInstance(getApplicationContext())
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        Call<SuccessResGetEventComment> call = apiInterface.getEventComments(map);

        call.enqueue(new Callback<SuccessResGetEventComment>() {
            @Override
            public void onResponse(Call<SuccessResGetEventComment> call, Response<SuccessResGetEventComment> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetEventComment data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        commentList.clear();
                        commentList.addAll(data.getResult());
                        commentAdapter.notifyDataSetChanged();
                    } else if (data.status.equals("0")) {
                        commentList.clear();
                        showToast(EventsDetailsScreenJoin.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetEventComment> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = " MMM d, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;

        try {
            if (!time.equalsIgnoreCase("")) {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            gMap = googleMap;
            strLat = requestModel.getLat();
            strLng = requestModel.getLon();
            if (strLat != null && strLng != null) {
                double lat = Double.parseDouble(strLat);
                double lng = Double.parseDouble(strLng);
                LatLng sydney = new LatLng(lat, lng);
                gMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("Marker"));
                //   gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (Exception e) {
            Log.d("TAG", "onMapReady: " + e);
        }


    }
}