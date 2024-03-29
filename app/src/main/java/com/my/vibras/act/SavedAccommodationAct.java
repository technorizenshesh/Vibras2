package com.my.vibras.act;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.adapter.MyAccAdapter;
import com.my.vibras.adapter.MyEventsAdapter;
import com.my.vibras.databinding.ActivitySavedEventsBinding;
import com.my.vibras.model.SuccessResAddLike;
import com.my.vibras.model.SuccessResMyAccom;
import com.my.vibras.model.SuccessResMyEventRes;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.PostClickListener;
import com.my.vibras.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedAccommodationAct extends AppCompatActivity implements PostClickListener {

    ActivitySavedEventsBinding binding;

    private VibrasInterface apiInterface;

    private ArrayList<SuccessResMyAccom.Result> eventsList = new ArrayList<>();

    private MyAccAdapter myEventsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_saved_events);

        binding.RRback.setOnClickListener(v -> finish());
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
binding.savedRestaurant.setText(getString(R.string.accommodation));
        myEventsAdapter = new MyAccAdapter(this, eventsList, this);

        binding.rvRestaurants.setHasFixedSize(true);

        binding.rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        binding.rvRestaurants.setAdapter(myEventsAdapter);
        getMyRestaurantsApi();
    }


    public void getMyRestaurantsApi() {
        eventsList.clear();
        DataManager.getInstance().showProgressMessage(SavedAccommodationAct.this, getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(SavedAccommodationAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(SavedAccommodationAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        Call<SuccessResMyAccom> call = apiInterface.getUseraccommodation(map);
        call.enqueue(new Callback<SuccessResMyAccom>() {
            @Override
            public void onResponse(Call<SuccessResMyAccom> call, Response<SuccessResMyAccom> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResMyAccom data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {

                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        eventsList.clear();
                        eventsList.addAll(data.getResult());
                        myEventsAdapter.notifyDataSetChanged();

                    } else if (data.getStatus().equals("0")) {
                        showToast(SavedAccommodationAct.this, data.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResMyAccom> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void selectLike(int position, String status) {
        addLike(eventsList.get(position).getId());
    }

    @Override
    public void bottomSheet(View param1, String postID, boolean isUser, int position) {

        showDialog(eventsList.get(position).getId(), position);

    }

    @Override
    public void savePost(View param1, String postID, boolean isUser, int position) {

        saveRestaurant(eventsList.get(position).getId());

    }

    private void addLike(String postId) {

        String userId = SharedPreferenceUtility.getInstance(SavedAccommodationAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(SavedAccommodationAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("accommodation_id", postId);
        Call<SuccessResAddLike> call = apiInterface.addaccommodationlike(map);
        call.enqueue(new Callback<SuccessResAddLike>() {
            @Override
            public void onResponse(Call<SuccessResAddLike> call, Response<SuccessResAddLike> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddLike data = response.body();
                    Log.e("data", data.status + "");
                    getMyRestaurantsApi();
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

    private void saveRestaurant(String postId) {
        String userId = SharedPreferenceUtility.getInstance(SavedAccommodationAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(SavedAccommodationAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("save_post_id", postId);
        map.put("type", "event");
        Call<SuccessResAddLike> call = apiInterface.saveEventRestaurant(map);
        call.enqueue(new Callback<SuccessResAddLike>() {
            @Override
            public void onResponse(Call<SuccessResAddLike> call, Response<SuccessResAddLike> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddLike data = response.body();
                    Log.e("data", data.status + "");
                    getMyRestaurantsApi();

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

    public void showDialog(String postId, int position) {

        final Dialog dialog = new Dialog(SavedAccommodationAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_options);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView tvDelete = dialog.findViewById(R.id.tvDelete);
        TextView tvShare = dialog.findViewById(R.id.tvShare);
        String userId = SharedPreferenceUtility.getInstance(SavedAccommodationAct.this).getString(USER_ID);

        if (userId.equalsIgnoreCase(eventsList.get(position).getUserId())) {
            tvDelete.setVisibility(View.VISIBLE);
        } else {
            tvDelete.setVisibility(View.GONE);
        }

        tvDelete.setOnClickListener(v1 ->
                {
                    dialog.dismiss();
                    new AlertDialog.Builder(SavedAccommodationAct.this)
                            .setTitle(getString(R.string.delete_post))
                            .setMessage(getString(R.string.are_you_sure_want_to_delete_post))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePost(postId);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void deletePost(String postId) {

        String userId = SharedPreferenceUtility.getInstance(SavedAccommodationAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(SavedAccommodationAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("accommodation_id", postId);
        Call<SuccessResAddLike> call = apiInterface.delete_accommodation(map);
        call.enqueue(new Callback<SuccessResAddLike>() {
            @Override
            public void onResponse(Call<SuccessResAddLike> call, Response<SuccessResAddLike> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddLike data = response.body();
                    Log.e("data", data.status + "");
                    if (data.status == 1) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);

                        getMyRestaurantsApi();

                    } else if (data.status == 0) {
                        showToast(SavedAccommodationAct.this, data.message);
                    }
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
}