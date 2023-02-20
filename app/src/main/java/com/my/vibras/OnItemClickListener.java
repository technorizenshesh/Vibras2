package com.my.vibras;

import android.view.View;

import com.my.vibras.model.SuccessResGetInterest;

public interface OnItemClickListener {

        void onItemClick(View view, int position, SuccessResGetInterest.Result model, String status );
    }