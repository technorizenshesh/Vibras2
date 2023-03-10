package com.my.vibras.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.vibras.R;
import com.my.vibras.model.SuccessResGetUsers;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;

    private ArrayList<SuccessResGetUsers.Result> modelList;

    private OnItemClickListener mItemClickListener;

    public AddFriendAdapter(Context context, ArrayList<SuccessResGetUsers.Result> modelList, OnItemClickListener mItemClickListener) {
        this.mContext = context;
        this.modelList = modelList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itme_add_group, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final SuccessResGetUsers.Result model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;

            TextView tvUserName = holder.itemView.findViewById(R.id.tvUserName);
            RelativeLayout rlParent = holder.itemView.findViewById(R.id.rlParent);
            TextView tvLastMessage = holder.itemView.findViewById(R.id.tvLastMessage);
            ImageView ivDelete = holder.itemView.findViewById(R.id.ivDelete);
            CircleImageView ivProfile = holder.itemView.findViewById(R.id.ivProfile);

            tvUserName.setText(StringEscapeUtils.unescapeJava(model.getFirstName()+" "+model.getLastName()));

            Glide.with(mContext)
                    .load(model.getImage())
                    .into(ivProfile);

            rlParent.setOnClickListener(v ->
                    {
                        mItemClickListener.onItemClick(v,position,model);

                    }
                    );
        }
    }

    @Override
    public int getItemCount()
    {
        return modelList.size();
    }

    private SuccessResGetUsers.Result getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position, SuccessResGetUsers.Result model);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;

        public ViewHolder(final View itemView) {
            super(itemView);

        }
    }

}

