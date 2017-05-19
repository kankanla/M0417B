package com.kankanla.m0417b;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kankanla on 2017/04/19.
 */

public class PlayerService2 extends Service {

    private ArrayList<Uri> Filelist;
    private L_Binder l_binder = new L_Binder();
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return l_binder;
    }

    public class L_Binder extends Binder {
        PlayerService2 getServer() {
            return PlayerService2.this;
        }
    }

    public void addUri(Uri uri) {
        Filelist.add(uri);
        System.out.println("-----------------addUri--------------------");
        for (Uri file : Filelist) {
            System.out.println(file.getPath());
        }
        System.out.println("-----------------addUri--------------------");
    }

    public void player() {
        if (Filelist.size() != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            System.out.println("-----------------------player-----mediaPlayer memory-------");
                            System.out.println(mediaPlayer.toString());
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(getApplication(), Filelist.remove(0));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            try {
                                if (Filelist.size() != 0) {
                                    System.out.println("--------------setOnCompletionListener----mp memory-------");
                                    System.out.println(mp.toString());
                                    mp.reset();
                                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mp.setDataSource(getApplicationContext(), Filelist.remove(0));
                                    mp.prepare();
                                    mp.start();
                                } else {
                                    mp.stop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("-----------------------onStartCommand----------------------------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        System.out.println("-----------------------onCreate----------------------------");
        Filelist = new ArrayList<Uri>();
        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        System.out.println("-----------------------onDestroy----------------------------");
        super.onDestroy();
    }
}

