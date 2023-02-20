package com.my.vibras.chat.GroupChat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.vibras.R;
import com.my.vibras.chat.ChatListAdapter;
import com.my.vibras.chat.ChatMessage;
import com.my.vibras.utility.Session;

   
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ravindra Birla on 24,November,2022
 */
class GroupChatAdaapter extends RecyclerView.Adapter<GroupChatAdaapter.ViewHolder> {
    Context mContext;
    List<ChatMessage> chatList;
    Resources mRes;
    LayoutInflater mInflater;
    String userid;
    Session session;


    public GroupChatAdaapter(Context context, List<ChatMessage> chatList) {
        this.chatList = chatList;
        this.mContext = context;
        //notifyDataSetChanged();

    }


    @NonNull
    @Override
    public GroupChatAdaapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                group_allmessagesholayout, parent, false);
        return new GroupChatAdaapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatAdaapter.ViewHolder holder, final int position) {

        ChatMessage chat = chatList.get(position);
        session = new Session(mContext);
        userid = session.getUserId();
        Log.e("adapterID", "onBindViewHolder: " + userid);
        Log.e("adapterID", "onBindViewHolder: " + chat.getReceiveerID());
        Log.e("adapterID", "onBindViewHolder: " + chat.getSenderID());
        Log.e("adapterID", "onBindViewHolder: " + chat.getImage());
        Log.e("adapterID", "onBindViewHolder: " + chat.getVideo());
        Log.e("adapterID", "onBindViewHolder: " + chat.getTime());
        Log.e("adapterID", "onBindViewHolder: " + chat.getLongitude());

/*        if (chat.getSenderID().equalsIgnoreCase(userid)) {
            if (chat.getMessage().equalsIgnoreCase("")) {
                holder.usercard.setVisibility(View.VISIBLE);
                holder.friendcard.setVisibility(View.GONE);
                holder.usersendimage.setVisibility(View.VISIBLE);
                holder.recievesendimage.setVisibility(View.GONE);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
            } else {

                if (chat.getImage().equalsIgnoreCase("")){
                    holder.mycard_locat.setVisibility(View.VISIBLE);
                    holder.recievesendimage.setVisibility(View.GONE);
                    holder.usermessagelayout.setVisibility(View.GONE);
                    holder.friendmessagelayout.setVisibility(View.GONE);
                    holder.usercard.setVisibility(View.GONE);
                    holder.friendcard.setVisibility(View.GONE);
                    holder.usersendimage.setVisibility(View.GONE);
                    holder.recievesendimage.setVisibility(View.GONE);
                    holder.friendmessagelayout.setVisibility(View.GONE);
                    holder.friendtime.setVisibility(View.GONE);
                    holder.usercard.setVisibility(View.GONE);
                    holder.friendcard.setVisibility(View.GONE);
                    holder.usersendimage.setVisibility(View.GONE);
                    holder.recievesendimage.setVisibility(View.GONE);
                    holder.friendmessagelayout.setVisibility(View.GONE);
                    holder.friendtime.setVisibility(View.GONE);
                    holder.usertime.setVisibility(View.GONE);
                    holder.usertime.setText(chat.getTime());
                }else {
                    holder.usercard.setVisibility(View.GONE);
                    holder.friendcard.setVisibility(View.GONE);
                    holder.usersendimage.setVisibility(View.GONE);
                    holder.recievesendimage.setVisibility(View.GONE);
                    holder.userTextSHow.setText(  (chat.getMessage()));
                    holder.friendmessagelayout.setVisibility(View.GONE);
                    System.out.println("userid" + chat.getSenderID());
                    Glide.with(mContext).load(chat.getUserImage()).into(holder.userimage);
                    holder.friendtime.setVisibility(View.GONE);
                    holder.usertime.setVisibility(View.VISIBLE);
                    holder.usertime.setText(chat.getTime());
                }
            }

        } else {
            if (chat.getMessage().equalsIgnoreCase("")) {
                holder.recievesendimage.setVisibility(View.VISIBLE);
                holder.usercard.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.VISIBLE);
                holder.usersendimage.setVisibility(View.GONE);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendTextShow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
            }
            else {

                  if (chat.getImage().equalsIgnoreCase("")){
                      holder.mycard_locat.setVisibility(View.GONE);
                      holder.friendcard_locat.setVisibility(View.VISIBLE);
                      holder.usercard.setVisibility(View.GONE);
                      holder.friendcard.setVisibility(View.GONE);
                      holder.usersendimage.setVisibility(View.GONE);
                      holder.recievesendimage.setVisibility(View.GONE);
                      holder.usermessagelayout.setVisibility(View.GONE);
                      holder.friendmessagelayout.setVisibility(View.GONE);

                  }else {
                      holder.usercard.setVisibility(View.GONE);
                      holder.friendcard.setVisibility(View.GONE);
                      holder.usersendimage.setVisibility(View.GONE);
                      holder.recievesendimage.setVisibility(View.GONE);
                      holder.friendTextShow.setText((chat.getMessage()));
                      holder.usermessagelayout.setVisibility(View.GONE);
                      holder.friendtime.setVisibility(View.VISIBLE);
                      holder.usertime.setVisibility(View.GONE);
                      holder.friendtime.setText(chat.getTime());
                  }}*/


        if(chat.getSenderID().equalsIgnoreCase(userid)) {


            if (!chat.getLattitude().equalsIgnoreCase("0.0")){
                holder.mycard_locat.setVisibility(View.VISIBLE);
                holder.friendsenderimage.setVisibility(View.GONE);
                holder.friendsenderimage_liner.setVisibility(View.GONE);
                holder.friendTextShow.setVisibility(View.GONE);
                holder.friendcard_locat.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.GONE);
                holder.recievesendimage.setVisibility(View.GONE);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);

            }else
            if (chat.getMessage().equalsIgnoreCase("")){
                holder.usercard.setVisibility(View.VISIBLE);
                holder.friendcard.setVisibility(View.GONE);
                holder.  usersendimage.setVisibility(View.VISIBLE);
                holder.recievesendimage.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(  chat.getImage())
                        .into(holder.usersendimage);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
                holder.friendsenderimage_liner.setVisibility(View.GONE);

            }else {
                holder.usercard.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.GONE);
                holder.  usersendimage.setVisibility(View.GONE);
                holder.recievesendimage.setVisibility(View.GONE);
                holder.userTextSHow.setText(  (chat.getMessage()));
                holder.friendmessagelayout.setVisibility(View.GONE);
                System.out.println("userid" + chat.getSenderID());
                holder.friendsenderimage_liner.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(  chat.getUserImage())
                        .into(holder.userimage);
                holder. friendtime.setVisibility(View.GONE);
                holder. usertime.setVisibility(View.VISIBLE);
                holder. usertime.setText(chat.getTime());
            }

        }
        else {

            if (!chat.getLattitude().equalsIgnoreCase("0.0")){
                holder.friendcard_locat.setVisibility(View.VISIBLE);
                holder.mycard_locat.setVisibility(View.GONE);
                holder.  usersendimage.setVisibility(View.GONE);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
                holder.usercard.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.GONE);
                holder.  usersendimage.setVisibility(View.GONE);
                holder.recievesendimage.setVisibility(View.GONE);
            }else
            if (chat.getMessage().equalsIgnoreCase("")){
                holder.  recievesendimage.setVisibility(View.VISIBLE);
                holder.usercard.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(  chat.getImage())
                        .into(holder.recievesendimage);
                holder.  usersendimage.setVisibility(View.GONE);
                holder.userTextSHow.setVisibility(View.GONE);
                holder.friendTextShow.setVisibility(View.GONE);
                holder.friendmessagelayout.setVisibility(View.GONE);
                holder.usermessagelayout.setVisibility(View.GONE);
               /* if (chat.getImage().equalsIgnoreCase("")){
                    holder.friendcard_locat.setVisibility(View.VISIBLE);
                }*/
            }else {
                holder.usercard.setVisibility(View.GONE);
                holder.friendcard.setVisibility(View.GONE);
                holder.  usersendimage.setVisibility(View.GONE);
                holder.recievesendimage.setVisibility(View.GONE);
                holder.friendTextShow.setText(  (chat.getMessage()));
                holder.usermessagelayout.setVisibility(View.GONE);
                holder. friendtime.setVisibility(View.VISIBLE);
                holder. usertime.setVisibility(View.GONE);
                holder. friendtime.setText(chat.getTime());
                Log.e("TAG", "GlideGlideGlideGlideGlide: " + chat.getFriendImage());
                Glide.with(mContext)
                        .load(   chat.getFriendImage())
                        .into(holder.friendsenderimage);
                System.out.println("elseuserid" + chat.getReceiveerID());

            }
            // ChatListAdapter222.notifyDataSetChanged();
            try {
              //  ChatListAdapter.this.notify();

            } catch (Exception e) {
                System.out.println("====" + e.fillInStackTrace());

            }

        }







            Log.e("TAG", "GlideGlideGlideGlideGlide: " + chat.getFriendImage());
            Glide.with(mContext)
                    .load(chat.getFriendImage())
                    .into(holder.friendsenderimage);
            holder.userss_name.setText(chat.getUsername());

            System.out.println("elseuserid" + chat.getReceiveerID());


            // GroupChatAdaapter222.notifyDataSetChanged();
            try {
                GroupChatAdaapter.this.notify();

            } catch (Exception e) {
                System.out.println("====" + e.fillInStackTrace());

            }


        holder.mycard_locat.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:"+ chat.getLattitude()+","+ chat.getLongitude()+"?q="+ chat.getLattitude()+","+ chat.getLongitude()));
            mContext. startActivity(intent);
        }); holder.friendcard_locat.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:"+ chat.getLattitude()+","+ chat.getLongitude()+"?q="+ chat.getLattitude()+","+ chat.getLongitude()));
            mContext. startActivity(intent);
        });
        holder.usercard.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Holo_NoActionBar);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                dialog.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue));
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customview);
                dialog.show();
                ImageView imagewshow = (ImageView) dialog.findViewById(R.id.imagewshow);
                RelativeLayout closeimage = (RelativeLayout) dialog.findViewById(R.id.closeimage);
                closeimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                Glide.with(mContext)
                        .load(  chat.getImage())
                        .into(imagewshow);
            }
        });
        holder.friendcard.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Holo_NoActionBar);
                // dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                dialog.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue));
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customview);
                //     dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFF;
                dialog.show();


                ImageView imagewshow = (ImageView) dialog.findViewById(R.id.imagewshow);
                RelativeLayout closeimage = (RelativeLayout) dialog.findViewById(R.id.closeimage);
                closeimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                Glide.with(mContext)
                        .load(  chat.getImage())
                        .into(imagewshow);

            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void newDialog(final Context c) {

        final AlertDialog alertDialog;


    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userss_name, userTextSHow, friendTextShow, usertime, friendtime;
        CardView    friendcard_locat, mycard_locat, usercard, friendcard;
        LinearLayout     usermessagelayout, friendmessagelayout;
        ImageView usersendimage, recievesendimage;
        CircleImageView friendsenderimage, userimage;
RelativeLayout friendsenderimage_liner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendcard_locat = itemView.findViewById(R.id.friendcard_locat);
            usercard = itemView.findViewById(R.id.usercard);
            mycard_locat = itemView.findViewById(R.id.mycard_locat);
            friendcard = itemView.findViewById(R.id.friendcard);
            userTextSHow = itemView.findViewById(R.id.userTextSHow);
            friendTextShow = itemView.findViewById(R.id.friendTextShow);
            usermessagelayout = itemView.findViewById(R.id.usermessagelayout);
            friendmessagelayout = itemView.findViewById(R.id.friendmessagelayout);
            usersendimage = itemView.findViewById(R.id.usersendimage);
            recievesendimage = itemView.findViewById(R.id.recievesendimage);
            usertime = itemView.findViewById(R.id.usertime);
            friendtime = itemView.findViewById(R.id.friendtime);
            friendsenderimage = itemView.findViewById(R.id.friendsenderimage);
            userimage = itemView.findViewById(R.id.userimage);
            userss_name = itemView.findViewById(R.id.userss_name);
            friendsenderimage_liner = itemView.findViewById(R.id.friendsenderimage_liner);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}