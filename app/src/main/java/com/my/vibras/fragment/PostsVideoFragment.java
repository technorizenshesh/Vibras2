package com.my.vibras.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.my.vibras.R;
import com.my.vibras.adapter.PostsVideoAdapter;
import com.my.vibras.databinding.FragmentPostsBinding;
import com.my.vibras.databinding.FragmentPostsVideoBinding;
import com.my.vibras.model.SuccessResAddLike;
import com.my.vibras.model.SuccessResGetPosts;
import com.my.vibras.model.SuccessResGetStories;
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

import static com.my.vibras.retrofit.Constant.USER_ID;
import static com.my.vibras.retrofit.Constant.showToast;


public class PostsVideoFragment  extends Fragment implements PostClickListener {

    private FragmentPostsVideoBinding binding;

    private ArrayList<SuccessResGetStories> storyList = new ArrayList<>();

    private ArrayList<SuccessResGetPosts.Result> postList = new ArrayList<>();

    private VibrasInterface apiInterface;

    private PostsVideoAdapter postsAdapter;

    @Override
    public void onPause() {
       postsAdapter.StopPlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        postsAdapter.StopPlayer();
        super.onStop();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_posts_video,container, false);
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        postsAdapter = new PostsVideoAdapter(getActivity(),postList,PostsVideoFragment.this,"Mine");
        binding.rvPosts.setHasFixedSize(true);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPosts.setAdapter(postsAdapter);
       //getPosts();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getPosts();
        super.onResume();
    }

    private void getPosts() {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("viewer_id",userId);
        map.put("type_status","VIDEO");
        Call<SuccessResGetPosts> call = apiInterface.getPost(map);
        call.enqueue(new Callback<SuccessResGetPosts>() {
            @Override
            public void onResponse(Call<SuccessResGetPosts> call, Response<SuccessResGetPosts> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetPosts data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        postList.clear();
                        postList.addAll(data.getResult());
                        postsAdapter.notifyDataSetChanged();

                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResGetPosts> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void selectLike(int position, String status) {
        addLike(postList.get(position).getId());
    }

    @Override
    public void bottomSheet(View param1, String postID, boolean isUser, int position) {
        showDialog(postID, position);

    }
    public void showDialog(String postId, int position) {

        final Dialog dialog = new Dialog(getActivity());
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
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        if (userId.equalsIgnoreCase(postList.get(position).getUserId())) {
            tvDelete.setVisibility(View.VISIBLE);
        } else {
            tvDelete.setVisibility(View.GONE);
        }

        tvShare.setOnClickListener(v1 ->
                {
                    dialog.dismiss();
                    String shareBody = "User :" + postList.get(position).getFirstName() + " " + postList.get(position).getLastName() + "\n\nPosted :" + postList.get(position).getImage();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    getActivity().startActivity(Intent.createChooser(sharingIntent, getActivity().getResources().getString(R.string.share_using)));
                }
        );

        tvDelete.setOnClickListener(v1 ->
                {
                    dialog.dismiss();
                    new AlertDialog.Builder(getActivity())
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

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("post_id", postId);
        Call<SuccessResAddLike> call = apiInterface.deletePost(map);
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
                        getPosts();
                    } else if (data.status == 0) {
                        showToast(getActivity(), data.message);
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

    @Override
    public void savePost(View param1, String postID, boolean isUser, int position) {

    }

    private void addLike(String postId) {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("post_id",postId);
        Call<SuccessResAddLike> call = apiInterface.addLike(map);
        call.enqueue(new Callback<SuccessResAddLike>() {
            @Override
            public void onResponse(Call<SuccessResAddLike> call, Response<SuccessResAddLike> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddLike data = response.body();
                    Log.e("data",data.status+"");
                    if (data.status==1) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
//                        setProfileDetails();

                        getPosts();

                    } else if (data.status==0) {
                        showToast(getActivity(), data.message);
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