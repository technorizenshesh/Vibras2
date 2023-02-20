package com.my.vibras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.vibras.Company.HomeComapnyAct;
import com.my.vibras.act.HomeUserAct;
import com.my.vibras.act.StoryDetailAct;
import com.my.vibras.databinding.ActivityChooseLanguageBinding;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;
import static com.my.vibras.retrofit.Constant.updateResources;

public class ChooseLanguage extends AppCompatActivity {

    ActivityChooseLanguageBinding binding;
    String from = "log";
    private VibrasInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_language);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);

        if (getIntent().getExtras() != null) {
            from = getIntent().getStringExtra("from");

        }
        boolean val = SharedPreferenceUtility.getInstance(ChooseLanguage.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            updateResources(ChooseLanguage.this, "en");
            binding.radio1.setChecked(true);
            binding.radio3.setChecked(false);
        } else {
            updateResources(ChooseLanguage.this, "es");
            binding.radio3.setChecked(true);
            binding.radio1.setChecked(false);
        }

        binding.radio1.setOnClickListener(v ->
                {
                    updateResources(ChooseLanguage.this, "en");
                    binding.radio3.setChecked(false);
                    SharedPreferenceUtility.getInstance(ChooseLanguage.this).putBoolean(Constant.SELECTED_LANGUAGE, false);
                }
        );

        binding.radio3.setOnClickListener(v ->
                {
                    updateResources(ChooseLanguage.this, "es");
                    binding.radio1.setChecked(false);
                    SharedPreferenceUtility.getInstance(ChooseLanguage.this).putBoolean(Constant.SELECTED_LANGUAGE, true);
                }
        );

        binding.btnNext.setOnClickListener(v ->
                {

                    int id = binding.radioGroup.getCheckedRadioButtonId();

                    if (binding.radio1.getId() == id) {
                        SharedPreferenceUtility.getInstance(ChooseLanguage.this).putBoolean(Constant.SELECTED_LANGUAGE, false);
                    } else {
                        SharedPreferenceUtility.getInstance(ChooseLanguage.this).putBoolean(Constant.SELECTED_LANGUAGE, true);
                    }

                    if (from.equalsIgnoreCase("login")) {

                        startActivity
                                (new Intent(
                                        ChooseLanguage.this,
                                        SelectViberLoginAct.class));
                     //   finish();
                    } else {

                        if (from.equalsIgnoreCase("company")) {
                            change_language();

                            startActivity
                                    (new Intent(
                                            ChooseLanguage.this,
                                            HomeComapnyAct.class).addFlags(Intent
                                            .FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            change_language();
                            startActivity
                                    (new Intent(
                                            ChooseLanguage.this,
                                            HomeUserAct.class).addFlags(Intent
                                            .FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    }

                }
        );
    }
    private void change_language() {
        String userId = SharedPreferenceUtility.getInstance(ChooseLanguage.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        boolean val = SharedPreferenceUtility.getInstance(ChooseLanguage.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        Call<ResponseBody> call = apiInterface.change_language(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    SuccessResAddComment data = response.body();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
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
                call.cancel();
            }
        });
    }

}