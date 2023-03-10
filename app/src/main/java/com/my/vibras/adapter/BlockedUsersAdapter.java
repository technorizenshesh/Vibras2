package com.my.vibras.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.my.vibras.R;
import com.my.vibras.model.SuccessResBlockedUser;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<SuccessResBlockedUser.Result> modelList;
    private OnItemClickListener mItemClickListener;

    public BlockedUsersAdapter(Context context, ArrayList<SuccessResBlockedUser.Result> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<SuccessResBlockedUser.Result> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.block_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final SuccessResBlockedUser.Result model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;

            CircleImageView circleImageView = holder.itemView.findViewById(R.id.ivProfile);
            TextView tvUserName,tvunblock;
            tvunblock = holder.itemView.findViewById(R.id.tvunblock);
            tvUserName = holder.itemView.findViewById(R.id.tvUserName);
            tvUserName.setText(StringEscapeUtils.unescapeJava(model.getUserName()));
            Glide.with(mContext)
                    .load(model.getUserImage())
                    .centerCrop()
                    .into(circleImageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private SuccessResBlockedUser.Result getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, SuccessResBlockedUser.Result model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;

        public ViewHolder(final View itemView) {
            super(itemView);

        //    this.txtName=itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));

                }
            });
        }
    }

}

