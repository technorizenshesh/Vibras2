package com.my.vibras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.vibras.act.HomeUserAct;
import com.my.vibras.adapter.PaymentCardRecyclerViewAdapter;
import com.my.vibras.databinding.ActivityLikeInterestsBinding;
import com.my.vibras.model.PaymentModel;
import com.my.vibras.model.SuccessResGetInterest;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.NetworkAvailablity;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;

public class LikeInterestsAct extends AppCompatActivity implements OnItemClickListener {
    ActivityLikeInterestsBinding binding;
    PaymentCardRecyclerViewAdapter mAdapter;
    private ArrayList<PaymentModel> modelList = new ArrayList<>();

    private ArrayList<SuccessResGetInterest.Result> interestList = new ArrayList<>();

    private VibrasInterface apiInterface;
String selected="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_like_interests);
        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.RContinew.setOnClickListener(v -> {
            setInterests(selected);
            SharedPreferenceUtility.getInstance(getApplication())
                    .putBoolean(Constant.IS_USER_LOGGED_IN, true);
            startActivity(new Intent(LikeInterestsAct.this,
                    HomeUserAct.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));

        });
        setAdapter();
    }





    private void setAdapter() {
        mAdapter = new PaymentCardRecyclerViewAdapter(
                LikeInterestsAct.this,interestList,this);
        binding.recyclelikeintrest.setHasFixedSize(true);
        binding.recyclelikeintrest.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclelikeintrest.setAdapter(mAdapter);
        if (NetworkAvailablity.checkNetworkStatus(this)) {
            getInterest();
        } else {
            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
        }

    }

    private void getInterest() {

        DataManager.getInstance().showProgressMessage(LikeInterestsAct.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        boolean val = SharedPreferenceUtility.getInstance(getApplicationContext())
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        Call<SuccessResGetInterest> call = apiInterface.getPassion(map);
        call.enqueue(new Callback<SuccessResGetInterest>() {
            @Override
            public void onResponse(Call<SuccessResGetInterest> call,
                                   Response<SuccessResGetInterest> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetInterest data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        interestList.clear();
                        interestList.addAll(data.getResult());
                        mAdapter.notifyDataSetChanged();
                    } else if (data.status.equals("0")) {
                        showToast(LikeInterestsAct.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SuccessResGetInterest> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
    private void setInterests(String data) {
        Map<String,String> map = new HashMap<>();
        String userId = SharedPreferenceUtility.getInstance(LikeInterestsAct.this).getString(USER_ID);
        map.put("user_id", userId);
        map.put("post_filter", data);
        Call<ResponseBody> call = apiInterface.setPassion(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                } catch (Exception e) {e.printStackTrace();}}
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();}});}
    @Override
    public void onItemClick(View view, int position, SuccessResGetInterest.Result model, String status) {
        if (status.equalsIgnoreCase("1")){
            if (selected.equalsIgnoreCase("")){selected=model.getId();}else {selected = selected + "," + model.getId();}
        }else {
            if (selected.contains(model.getId())){selected=selected.replace(model.getId()," " );}}
        selected = selected.replace(", ","");
        selected = selected.replace(" ","");
        Log.e("TAG", "onItemClick:   selected  --   "+selected);
    }
}