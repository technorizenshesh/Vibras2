package com.my.vibras.act;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bolaware.viewstimerstory.Momentz;
import com.bolaware.viewstimerstory.MomentzCallback;
import com.bolaware.viewstimerstory.MomentzView;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.VideoCalling.AppController;
import com.my.vibras.act.ui.home.HomeFragment;
import com.my.vibras.model.SuccessResGetStories;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.OnKeyboardVisibilityListener;
import com.my.vibras.utility.SharedPreferenceUtility;
import com.my.vibras.utility.SoftKeyboardLsnedRelativeLayout;
import com.my.vibras.utility.TimeAgo2;

import org.jetbrains.annotations.NotNull;
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

public class StoryDetailAct extends AppCompatActivity implements MomentzCallback, OnKeyboardVisibilityListener {

    ArrayList<MomentzView> storyView = new ArrayList<>();
    private ArrayList<SuccessResGetStories.UserStory> stories = new ArrayList<>();
    private String userName = "";
    private String userImage = "";
    private String story_user_id = "", story_id = "";
    private Momentz momentz;
    private RelativeLayout container;
    private TextView tvDateTime, tvSent;
    private EditText editText;
    private LinearLayout llContainer;
    private VibrasInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        container = findViewById(R.id.container);
        llContainer = findViewById(R.id.llContainer);
        tvDateTime = findViewById(R.id.tvTimeAgo);
        editText = findViewById(R.id.etComment);
        tvSent = findViewById(R.id.tvSent);
        initializeComponents();
        setKeyboardVisibilityListener(this);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        tvSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equalsIgnoreCase("")) {
                    editText.setError("Empty");
                } else {
                    add_story_reply(editText.getText().toString());
                    editText.setText("");
                    Toast.makeText(StoryDetailAct.this, " Message Sent ..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void add_story_reply(String s) {
        DataManager.getInstance().showProgressMessage(StoryDetailAct.this, getString(R.string.please_wait));

        String userId = SharedPreferenceUtility.getInstance(StoryDetailAct.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("story_user_id", story_user_id);
        map.put("story_id", story_id);
        map.put("reply", s);
        //user_id=34&story_user_id=4&story_id=117&reply=this%20is%20test%20Replay

        Call<ResponseBody> call = apiInterface.add_story_reply(map);
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
                      /*  SharedPreferenceUtility.getInstance(getApplicationContext())
                                .putBoolean(Constant.IS_USER_LOGGED_IN, false);
                        Intent intent = new Intent(StoryDetailAct.this,
                                SelectViberLoginAct.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                    } else if (data.equalsIgnoreCase("0")) {
                        showToast(StoryDetailAct.this, message);
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

    private void initializeComponents() {
        Bundle extras = getIntent().getExtras();
        SuccessResGetStories.Result storyObject = HomeFragment.story;
        userImage = extras.getString("UserImage");
        userName = extras.getString("UserName");
        stories.addAll(storyObject.getUserStory());

        if (stories != null) {
            for (SuccessResGetStories.UserStory story : stories) {
                if (story.getStoryType().equalsIgnoreCase("image") ||
                        story.getStoryType().equalsIgnoreCase("Image")) {
                    ImageView internetLoadedImageView = new ImageView(this);
                    storyView.add(new MomentzView(internetLoadedImageView, 10));
                } else {
                    VideoView videoView = new VideoView(this);
                    //        videoView.setBackground(getResources().getDrawable(R.color.black));
                    storyView.add(new MomentzView(videoView, 60));
                }
            }
        }
        momentz = new Momentz(this, storyView, container,
                this, R.drawable.green_lightgrey_drawable);
        momentz.start();
        SoftKeyboardLsnedRelativeLayout layout = (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.myLayout);
        layout.addSoftKeyboardLsner(new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {
                Log.d("SoftKeyboard", "Soft keyboard shown");
            }

            @Override
            public void onSoftKeyboardHide() {
                Log.d("SoftKeyboard", "Soft keyboard hidden");
            }
        });
    }

    @Override
    public void done() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "onPause: ");
    }

    @Override
    public void onNextCalled(@NotNull View view, @NotNull Momentz momentz, int i) {
        Log.d("TAG", "onNextCalled: " + i);
        /*try {
            String time = stories.get(i).getDateTime();
            TimeAgo2 timeAgo2 = new TimeAgo2();
            String MyFinalValue = timeAgo2.covertTimeToText(time);
            tvDateTime.setText(MyFinalValue);
        } catch (Exception e) {

        }*/
        tvDateTime.setText(stories.get(i).getTimeAgo());
        llContainer.removeAllViews();
        ImageView imgView = new ImageView(StoryDetailAct.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgView.setLayoutParams(lp);
        llContainer.addView(imgView);
        story_user_id = stories.get(i).getUserId();
        story_id = stories.get(i).getStoryId();
        if (stories.get(i).getIliked().equalsIgnoreCase("Yes")) {
            imgView.setBackground(getResources().getDrawable(R.drawable.red_heart_filled));
        } else {
            imgView.setBackground(getResources().getDrawable(R.drawable.ic_stroke_black));
        }

        imgView.setOnClickListener(v ->
                {
                    if (stories.get(i).getIliked().equalsIgnoreCase("Yes")) {
                        imgView.setBackground(getResources().getDrawable(R.drawable.ic_stroke_black));
                        stories.get(i).setIliked("No");
                    } else {
                        imgView.setBackground(getResources().getDrawable(R.drawable.red_heart_filled));
                        stories.get(i).setIliked("Yes");
                    }
                    addLike(stories.get(i).getStoryId(), stories.get(i).getId());
                }
        );

        if (view instanceof VideoView) {
            momentz.pause(true);
            playVideo((VideoView) view, i, momentz);
        } else {
            momentz.pause(true);
            if (!isFinishing()) {
                Glide.with(this).load(stories.get(i).getStoryData()).centerCrop().into((ImageView) view);
                momentz.resume();
            }
        }
    }

    private void addLike(String storyID, String storySubId) {
        String userId = SharedPreferenceUtility.getInstance(StoryDetailAct.this).getString(USER_ID);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("story_id", storyID);
        map.put("story_sub_id", storySubId);
        Call<ResponseBody> call = apiInterface.addStoryLike(map);
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

                    } else if (data.equalsIgnoreCase("0")) {
                        showToast(StoryDetailAct.this, message);
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

    public void playVideo(VideoView videoView, int index, Momentz momentz) {
       /* DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);*/
        HttpProxyCacheServer proxy;
        String str = stories.get(index).getStoryData();
        Uri uri = Uri.parse(str);
        proxy = AppController.getProxy(StoryDetailAct.this);
        String proxyUrl = proxy.getProxyUrl(str);
        videoView.setVideoURI(Uri.parse(proxyUrl));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                momentz.editDurationAndResume(index, (videoView.getDuration()) / 1000);
                return true;
            }
            return false;
        });
    }


    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {

        if (visible) {
            momentz.pause(false);
        } else {
            momentz.resume();
        }
    }
}