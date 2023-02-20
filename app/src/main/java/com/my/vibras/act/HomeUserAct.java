package com.my.vibras.act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.model.NotificationCount;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.my.vibras.retrofit.Constant.USER_ID;

public class HomeUserAct extends AppCompatActivity {
    static BottomNavigationView navView;
    VibrasInterface apiInterface;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        navView = findViewById(R.id.nav_view);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_user);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    @Override
    protected void onResume() {
        getNotification();

        super.onResume();
    }

    public void hideBadge() {
        BottomNavigationItemView itemView = navView.findViewById(R.id.navigation_notifications);
        itemView.removeViewAt(2);
    }

    private void getNotification() {
        DataManager.getInstance().showProgressMessage(HomeUserAct.this, getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(HomeUserAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(HomeUserAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        Call<NotificationCount> call = apiInterface.get_total_useen_notification(map);
        call.enqueue(new Callback<NotificationCount>() {
            @Override
            public void onResponse(Call<NotificationCount> call,
                                   Response<NotificationCount> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    NotificationCount data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        if (data.getResult().getCount() >= 1) {
                            showBadge(getApplicationContext(), "" + data.getResult().getCount());
                        }
                        // hideBadge();

                    } else if (data.getStatus().equalsIgnoreCase("0")) {
                        // showToast(HomeUserAct.this, data.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NotificationCount> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    public static void showBadge(Context context
            , String value) {
        // removeBadge( itemId);
        BottomNavigationItemView itemView = navView.findViewById(R.id.navigation_notifications);
        View badge = LayoutInflater.from(context).inflate(R.layout.layout_news_badge,
                navView, false);
        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);
    }

}