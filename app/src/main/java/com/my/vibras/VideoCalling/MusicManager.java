package com.my.vibras.VideoCalling;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Ravindra Birla on 09,August,2022
 */

public class MusicManager {

    private static MusicManager refrence = null;
    private MediaPlayer mPlayer;
    Uri doogeUri;
    Context mContext;

    public static MusicManager getInstance() {
        if(refrence == null) {
            refrence = new MusicManager();
        }
        return refrence;
    }

    public void initalizeMediaPlayer(Context context, Uri musicId) {
        mContext = context;
        doogeUri = musicId;
    }

    public void startPlaying() {
        try {
            mPlayer = MediaPlayer.create(mContext,doogeUri);
            mPlayer.setLooping(true);
            Log.e("MediaPlayerMediaPlayer","MediaPlayer123");
            mPlayer.start();
        } catch (Exception e) {}
    }

    public void stopPlaying() {
        try {
            if (mPlayer != null)
                mPlayer.stop();
        } catch (Exception e){}
    }

}