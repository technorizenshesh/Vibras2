package com.my.vibras;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.my.vibras.databinding.ActivitySelectViberLoginBinding;
import com.my.vibras.model.SuccessResGetPP;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectViberLoginAct extends AppCompatActivity {

    ActivitySelectViberLoginBinding binding;
    String loginType = "user";
    private VibrasInterface apiInterface;
    boolean check= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_viber_login);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);

        binding.cardLoginUser.setOnClickListener(v -> {
            loginType = "user";
            binding.RadiobtnPlumber.setChecked(true);
            binding.RadiobtnUser.setChecked(false);
        });

        binding.cardLoginCompany.setOnClickListener(v -> {
            loginType = "company";
            binding.RadiobtnPlumber.setChecked(false);
            binding.RadiobtnUser.setChecked(true);
        });
        binding.privacy.setOnClickListener(v -> {

            openDialog("privacy");
        });
        binding.coockis.setOnClickListener(v -> {

            openDialog("cookies");
        });
        binding.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
             check = isChecked;

        });

        binding.RRContinew.setOnClickListener(v -> {
            if (check){
            Intent intent = new Intent(
                    SelectViberLoginAct.this, LoginAct.class)
                    .putExtra("loginType", loginType);
            startActivity(intent);
        }else {
             //   binding.check.setError("");
                Toast.makeText(this, getString(R.string.please_check_the_box), Toast.LENGTH_SHORT).show();
            }});
    }

    private void openDialog(String privacy) {
        final Dialog dialog = new Dialog(SelectViberLoginAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations
                = android.R.style.Widget_Material_PopupWindow;
        dialog.setContentView(R.layout.dialog_privacy);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        TextView txtName = dialog.findViewById(R.id.txtName);
        RelativeLayout RRback = dialog.findViewById(R.id.RRback);
        WebView webView = dialog.findViewById(R.id.webView);
        if (privacy.equalsIgnoreCase("privacy")) {
            txtName.setText(R.string.privacy_policy);
            getPrivacy(webView);
        } else {
            txtName.setText(R.string.cookies_policies);
            getCookies(webView);
        }
        RRback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void getCookies(WebView webView) {
        Map<String, String> map = new HashMap<>();
        boolean val = SharedPreferenceUtility.getInstance(SelectViberLoginAct.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        Call<SuccessResGetPP> call = apiInterface.get_cookies(map);
        call.enqueue(new Callback<SuccessResGetPP>() {
            @Override
            public void onResponse(Call<SuccessResGetPP> call, Response<SuccessResGetPP> response) {

                try {
                    SuccessResGetPP data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        //  description = data.getResult().getDescription();
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadData(data.getResult().getDescription(), "text/html; charset=utf-8", "UTF-8");
                        DataManager.getInstance().hideProgressMessage();

                    } else if (data.status.equals("0")) {
                        //   showToast(PrivacyPolicyAct.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetPP> call, Throwable t) {

                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }


    private void getPrivacy(WebView webView) {
        DataManager.getInstance().showProgressMessage(SelectViberLoginAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        boolean val = SharedPreferenceUtility.getInstance(SelectViberLoginAct.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        Call<SuccessResGetPP> call = apiInterface.getPrivacyPolicy(map);
        call.enqueue(new Callback<SuccessResGetPP>() {
            @Override
            public void onResponse(Call<SuccessResGetPP> call, Response<SuccessResGetPP> response) {

                try {
                    SuccessResGetPP data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        //  description = data.getResult().getDescription();
                        webView.getSettings().setJavaScriptEnabled(true);

                        webView.loadData(data.getResult().getDescription(), "text/html; charset=utf-8", "UTF-8");

                        DataManager.getInstance().hideProgressMessage();

                    } else if (data.status.equals("0")) {
                        //   showToast(PrivacyPolicyAct.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetPP> call, Throwable t) {
                call.cancel();

                DataManager.getInstance().hideProgressMessage();

            }
        });
    }
}