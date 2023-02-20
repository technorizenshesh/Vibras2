package com.my.vibras.act;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.my.vibras.R;
import com.my.vibras.adapter.BlockedUsersAdapter;
import com.my.vibras.adapter.TrasactionAdapter;
import com.my.vibras.databinding.ActivityBlockUsersBinding;
import com.my.vibras.databinding.ActivityTransactionBinding;
import com.my.vibras.model.SuccessResBlockedUser;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;

public class BlockUsersAct extends AppCompatActivity  {

    ActivityBlockUsersBinding binding;
    BlockedUsersAdapter mAdapter;
    private ArrayList<SuccessResBlockedUser.Result> modelListbrouse=new ArrayList<>();

    private VibrasInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_block_users);

        apiInterface = ApiClient.getClient().create(VibrasInterface.class);

        setAdapter();
        getTransaction();

        binding.RRback.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setAdapter()
    {
        mAdapter = new BlockedUsersAdapter(BlockUsersAct.this,modelListbrouse);
        binding.recycleTrasaction.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BlockUsersAct.this);
        binding.recycleTrasaction.setLayoutManager(linearLayoutManager);
        //binding.recyclermyAccount.setLayoutManager(linearLayoutManager);
        binding.recycleTrasaction.setAdapter(mAdapter);
      mAdapter.SetOnItemClickListener(new BlockedUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, SuccessResBlockedUser.Result model) {
                Log.e("TAG", "onItemClick: " );

                block_user(model.getBlockedUserId());
            }
        });
    }
    private void block_user(String User_id) {
        DataManager.getInstance().showProgressMessage(BlockUsersAct.this,
                getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(BlockUsersAct.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("frnd_id", User_id);
        map.put("user_id", userId);
        map.put("reason", "UnBlock by User");
        Call<ResponseBody> call = apiInterface.block_user(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    SuccessResAddComment data = response.body();
                    DataManager.getInstance().hideProgressMessage();

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("status");
                //    String result = jsonObject.getString("result");
                  //  Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_blcked), Toast.LENGTH_SHORT).show();
getTransaction();
//                    String message = jsonObject.getString("message");
                //    finish();
                    if (data.equalsIgnoreCase("1")) {

                    } else if (data.equalsIgnoreCase("0")) {
                    }
                } catch (Exception e) {
                    Log.d("TAG", "onResponse: " + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }

    public void getTransaction()
    {
        String userId = SharedPreferenceUtility.getInstance(BlockUsersAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(BlockUsersAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id",userId);
      /*  boolean val = SharedPreferenceUtility.getInstance(BlockUsersAct.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }*/
        Call<SuccessResBlockedUser> call = apiInterface.get_block_user(map);
        call.enqueue(new Callback<SuccessResBlockedUser>() {
            @Override
            public void onResponse(Call<SuccessResBlockedUser> call, Response<SuccessResBlockedUser> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResBlockedUser data = response.body();
                //    if (data.getStatus().equalsIgnoreCase("1")) {
                        modelListbrouse.clear();
                        modelListbrouse.addAll(data.getResult());
                        mAdapter.notifyDataSetChanged();
                  //  } else {
                  //      showToast(BlockUsersAct.this, data.getMessage());
                   // }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResBlockedUser> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


}