package com.my.vibras.act;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.chat.ChatInnerMessagesActivity;
import com.my.vibras.databinding.ActivityFriendProfileBinding;
import com.my.vibras.fragment.FriendAllPhotosFragment;
import com.my.vibras.fragment.FriendPostsFragment;
import com.my.vibras.fragment.FriendVideoFragment;
import com.my.vibras.model.SuccessFollowersRes;
import com.my.vibras.model.SuccessResAddOtherProfileLike;
import com.my.vibras.model.SuccessResSignup;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.NetworkAvailablity;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;
import static com.my.vibras.utility.Util.hideKeyboard;

public class FriendProfileActivity extends AppCompatActivity {
    private VibrasInterface apiInterface;
    private ActivityFriendProfileBinding binding;
    private SuccessResSignup.Result userDetail;
    private Qr_DetailsAdapter adapter;
    String User_id = "";
    Bundle bundle = new Bundle();

    private void block_user() {
        DataManager.getInstance().showProgressMessage(FriendProfileActivity.this,
                getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(FriendProfileActivity.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("frnd_id", userId);
        map.put("user_id", User_id);
        map.put("reason", "Block by User");

        Call<ResponseBody> call = apiInterface.block_user(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    SuccessResAddComment data = response.body();
                    DataManager.getInstance().hideProgressMessage();

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("status");
                    String result = jsonObject.getString("result");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_blcked), Toast.LENGTH_SHORT).show();

                    String message = jsonObject.getString("message");
                    finish();
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

    private void spam_user() {
        DataManager.getInstance().showProgressMessage(FriendProfileActivity.this,
                getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(FriendProfileActivity.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("frnd_id", userId);
        map.put("user_id", User_id);
        map.put("reason", "Spamed by User");

        Call<ResponseBody> call = apiInterface.spam_user(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    SuccessResAddComment data = response.body();
                    DataManager.getInstance().hideProgressMessage();

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    String result = jsonObject.getString("result");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_spamed), Toast.LENGTH_SHORT).show();

                    finish();

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

    public void showDialog() {

        final Dialog dialog = new Dialog(FriendProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_block_options);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView tvDelete = dialog.findViewById(R.id.tvDelete);
        TextView tvShare = dialog.findViewById(R.id.tvShare);
        String userId = SharedPreferenceUtility.getInstance(getApplicationContext()).getString(USER_ID);
        if (!userId.equalsIgnoreCase(User_id)) {
            tvDelete.setVisibility(View.VISIBLE);
        } else {
            tvDelete.setVisibility(View.GONE);
        }

        tvShare.setOnClickListener(v1 ->
                {
                    spam_user();
                    dialog.dismiss();
                }
        );

        tvDelete.setOnClickListener(v1 ->
                {
                    block_user();
                    dialog.dismiss();

                }
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_friend_profile);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        hideKeyboard(FriendProfileActivity.this);
        try {
            User_id = getIntent().getExtras().getString("id");
            bundle.putString("user_id", User_id);
            if (NetworkAvailablity.checkNetworkStatus(getApplicationContext())) {
                getProfile(User_id);
                //  getPosts();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        binding.likeRecived.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),
                    LikeReceivedActivity.class)
                    .putExtra("id", User_id));
        });
        binding.ivBlock.setOnClickListener(v -> {

            showDialog();
        });
        binding.followers.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),
                    LikeSentActivity.class)
                    .putExtra("id", User_id));
        });
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnAddLike.setOnClickListener(v ->
                {
                    addOtherProfileLike(User_id, "Like");
                }
        );
        binding.llGroup.setOnClickListener(v ->
                {
                    startActivity(new Intent(getApplicationContext(),
                            ViewAllGroupsAct.class).putExtra("from", "other")
                            .putExtra("id", User_id));
                }
        );
        binding.llViewEvents.setOnClickListener(v ->
                {
                    startActivity(new Intent(getApplicationContext(),
                            ViewAllEventAct.class).putExtra("from", "other")
                            .putExtra("id", User_id)
                    );
                }
        );
        binding.sendMsg.setOnClickListener(v ->
                {
                    Intent intent = new Intent(FriendProfileActivity.this,
                            ChatInnerMessagesActivity.class);
                    intent.putExtra("friend_id", userDetail.getId());
                    intent.putExtra("friendimage", userDetail.getImage());
                    intent.putExtra("friend_name", userDetail.getFirstName() +
                            " " + userDetail.getLastName());
                    intent.putExtra("last_message", "hii");
                    intent.putExtra("messagetime", "1");
                    intent.putExtra("status_check", userDetail.getId());
                    intent.putExtra("id", userDetail.getId());
                    intent.putExtra("onlinestatus", userDetail.getImage());
                    intent.putExtra("unique_id", userDetail.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
        );

        setUpUi();
    }

    private void addOtherProfileLike(String otherUserId, String type) {
        String userId = SharedPreferenceUtility.getInstance(FriendProfileActivity.this)
                .getString(USER_ID);
        DataManager.getInstance().showProgressMessage(FriendProfileActivity.this,
                getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("profile_user", otherUserId);
        map.put("type", type);
        Call<SuccessResAddOtherProfileLike> call = apiInterface.addFireLikeLove(map);
        call.enqueue(new Callback<SuccessResAddOtherProfileLike>() {
            @Override
            public void onResponse(Call<SuccessResAddOtherProfileLike> call, Response<SuccessResAddOtherProfileLike> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddOtherProfileLike data = response.body();
                    Log.e("data", data.status + "");
                    if (data.status == 1) {
                        showToast(FriendProfileActivity.this, data.result);
                        binding.btnIlike.setVisibility(View.VISIBLE);
                        binding.btnAddLike.setVisibility(View.GONE);
                    } else if (data.status == 0) {
                        showToast(FriendProfileActivity.this, data.result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResAddOtherProfileLike> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public class Qr_DetailsAdapter extends FragmentPagerAdapter {

        private Context myContext;
        int totalTabs;
        int no;

        public Qr_DetailsAdapter(Context context, FragmentManager fm, int totalTabs, int no) {
            super(fm);
            myContext = context;
            this.totalTabs = totalTabs;
            this.no = no;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //  fm.

                    FriendPostsFragment recents = new FriendPostsFragment();
                    recents.setArguments(bundle);
                    return recents;
                case 1:
                    //  fm.

                    FriendAllPhotosFragment recentsq = new FriendAllPhotosFragment();
                    recentsq.setArguments(bundle);
                    return recentsq;
                case 2:
                    // bundle.putString("type","VIDEO" );
                    FriendVideoFragment recents1 = new FriendVideoFragment();
                    recents1.setArguments(bundle);
                    return recents1;
              /*  case 2:
                    PostsFragment recents11 = new PostsFragment();
                    return recents11;

                case 3:
                    PostsFragment recents113 = new PostsFragment();
                    return recents113;*/

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

    private void getProfile(String userId) {
        DataManager.getInstance().showProgressMessage(FriendProfileActivity.this
                , getString(R.string.please_wait));
        String self_id = SharedPreferenceUtility.getInstance(FriendProfileActivity.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("viewer_id", self_id);
        Call<SuccessResSignup> call = apiInterface.getProfile(map);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call, Response<SuccessResSignup> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResSignup data = response.body();
                    userDetail = data.getResult();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        setProfileDetails(userDetail);
                    } else if (data.status.equals("0")) {
                        showToast(FriendProfileActivity.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResSignup> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void getFollowers(String userId) {
        DataManager.getInstance().showProgressMessage(FriendProfileActivity.this
                , getString(R.string.please_wait));
        String self_id = SharedPreferenceUtility.getInstance(FriendProfileActivity.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        Call<SuccessFollowersRes> call = apiInterface.get_followers(map);
        call.enqueue(new Callback<SuccessFollowersRes>() {
            @Override
            public void onResponse(Call<SuccessFollowersRes> call, Response<SuccessFollowersRes> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessFollowersRes data = response.body();
                    data.getResult().get(0).getUserDetails();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessFollowersRes> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setProfileDetails(SuccessResSignup.Result userDetaile) {

        binding.tvName.setText(StringEscapeUtils.unescapeJava(this.userDetail.getFirstName() + " " + this.userDetail.getLastName()));

        if (!this.userDetail.getBio().equalsIgnoreCase("")) {
            binding.tvBio.setVisibility(View.VISIBLE);
            binding.tvBio.setText(StringEscapeUtils.unescapeJava(this.userDetail.getBio()));
        } else {
            binding.tvBio.setVisibility(View.GONE);
        }
        Log.e("", "setProfileDetailssetProfileDetails: " + this.userDetail.getCoverImage());
        Glide
                .with(getApplicationContext())
                .load(this.userDetail.getCoverImage())
                .into(binding.ivCoverPhoto);
        Glide
                .with(getApplicationContext())
                .load(this.userDetail.getImage())
                .placeholder(R.drawable.ic_user)
                .into(binding.ivProfile);

        binding.tvLikeGiven.setText(this.userDetail.getGivenLikes() + "");
        binding.tvLikeReceived.setText(this.userDetail.getReceviedLikes() + "");
        if (this.userDetail.getMatch_status().equalsIgnoreCase("0")) {
            binding.eventGroup.setVisibility(View.VISIBLE);
        } else {
            binding.eventGroup.setVisibility(View.VISIBLE);

        }
        if (this.userDetail.getLike_status().equalsIgnoreCase("0")) {
            binding.btnIlike.setVisibility(View.GONE);
            binding.btnAddLike.setVisibility(View.VISIBLE);
        } else {

            binding.sendMsg.setVisibility(View.VISIBLE);
            binding.btnIlike.setVisibility(View.VISIBLE);
            binding.btnAddLike.setVisibility(View.GONE);
        }
        if (this.userDetail.getLat() != null) {

            Log.e(TAG, "setProfileDetailssetProfileDetailssetProfileDetailssetProfileDetails: " + this.userDetail.getLat());
            Log.e(TAG, "setProfileDetailssetProfileDetailssetProfileDetailssetProfileDetails: " + this.userDetail.getLon());
            Log.e(TAG, "setProfileDetailssetProfileDetailssetProfileDetailssetProfileDetails: " + CurrentCity(Double.parseDouble(this.userDetail.getLat()), Double.parseDouble(this.userDetail.getLon())));

            if (!this.userDetail.getLat().equalsIgnoreCase("")) {
                binding.tvCity.setText(
                        CurrentCity(Double.parseDouble(this.userDetail.getLat()), Double.parseDouble(this.userDetail.getLon())));
            } else {

                binding.tvCity.setText(" ");


            }
        }

    }

    public String CurrentCity(double latitude, double longitude) {
        if (latitude >= 0.0) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            //List<Address> addresses =geocoder.getFromLocation(latitude, longitude, 1);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getSubLocality();
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                // txt_paddress.setText(address);
                return cityName + " , " + stateName;
                //   txt_state.setText(stateName);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "CurrentCity: " + e.getLocalizedMessage());
                Log.e(TAG, "CurrentCity: " + e.getMessage());
                Log.e(TAG, "CurrentCity: " + e.getCause());
            }
        }

        return "  ";

    }

    private void setUpUi() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.posts));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.all_photos));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.videos));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        adapter = new Qr_DetailsAdapter(getApplicationContext(), getSupportFragmentManager(), binding.tabLayout.getTabCount(), binding.tabLayout.getSelectedTabPosition());
        binding.viewPager.setAdapter(adapter);
        bundle.putString("type", "IMAGE");
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                //tab.getText()
//                if ( tab.getPosition()==0){
//                    bundle.putString("type","IMAGE" );
//                    adapter.notifyDataSetChanged();
//                }else {
//                    bundle.putString("type","VIDEO" );
//                }
//                adapter.notifyDataSetChanged();
//                Log.e("TAG", "onTabSelected: bu"+bundle );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

}
