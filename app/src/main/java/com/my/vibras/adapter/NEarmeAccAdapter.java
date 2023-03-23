package com.my.vibras.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.my.vibras.AccomdDetailAct;
import com.my.vibras.R;
import com.my.vibras.model.AccDetailsResSuccess;
import com.my.vibras.model.SuccessAccList;
import com.my.vibras.model.SuccessResGetRestaurants;


import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class NEarmeAccAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<SuccessAccList.Result> modelList;
    private OnItemClickListener mItemClickListener;


    public NEarmeAccAdapter(Context context, ArrayList<SuccessAccList.Result> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<SuccessAccList.Result> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itme_near_me_rest, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final SuccessAccList.Result model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;

           ImageView ivRestaurants = holder.itemView.findViewById(R.id.ivRestaurant);
            TextView tvName = holder.itemView.findViewById(R.id.tvName);
            TextView tvDistance = holder.itemView.findViewById(R.id.tvDistance);


            holder.itemView.setOnClickListener(v ->
                    {
                        mContext.startActivity(new Intent(mContext, AccomdDetailAct.class)
                                .putExtra("data",new Gson().toJson(modelList.get(position)))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
            );

            Glide.with(mContext)
                    .load(model.getImage())
                    .into(ivRestaurants);
            tvName.setText(  StringEscapeUtils.unescapeJava(model.getAccommodationName()));
//            if (model.getEstimateTime()>60) {
//
//                SimpleDateFormat sdf = new SimpleDateFormat("mm");
//                try {
//                    Date dt = sdf.parse(model.getEstimateTime() + "");
//                    sdf = new SimpleDateFormat("HH:mm");
//                    System.out.println(sdf.format(dt));
//                    tvDistance.setText("" + sdf.format(dt) + " Hour's");
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }else {
                tvDistance.setText("" + model.getAddress());

           // }
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

    private SuccessAccList.Result getItem(int position) {
        return modelList.get(position);
    }


    public interface OnItemClickListener {

        void onItemClick(View view, int position, SuccessResGetRestaurants.Result model);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;

        public ViewHolder(final View itemView) {
            super(itemView);


        //    this.txtName=itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }
            });
        }
    }

}

