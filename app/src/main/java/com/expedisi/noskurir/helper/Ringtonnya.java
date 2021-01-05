package com.expedisi.noskurir.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;

import com.expedisi.noskurir.R;

public class Ringtonnya {

    public static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    public static boolean isplayingAudio=false;

    public static void playAudio(Context c, int id){
        mediaPlayer = MediaPlayer.create(c,id);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        if(!mediaPlayer.isPlaying())
        {
            isplayingAudio=true;
            if(id == R.raw.tingtong){
                mediaPlayer.setLooping(false);
            }else{
                mediaPlayer.setLooping(false);
            }
            mediaPlayer.start();
        }
    }
    public static void playAudio(Context c, Uri id){
        mediaPlayer = MediaPlayer.create(c,id);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        if(!mediaPlayer.isPlaying())
        {
            isplayingAudio=true;
            mediaPlayer.start();
        }
    }
    public static void stopAudio(){
        isplayingAudio=false;
        mediaPlayer.stop();
    }
}
