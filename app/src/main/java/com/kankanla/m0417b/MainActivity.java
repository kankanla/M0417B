package com.kankanla.m0417b;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Uri u;
    private Context context;
    private Intent intent;
    private PlayerService2 playerService2;
    private boolean flag;
    private PlayerService2.L_Binder l_binder;
    protected FileSQL fileSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        intent = new Intent(this, PlayerService2.class);
        startService(intent);
        bindService(intent, new con(), Context.BIND_AUTO_CREATE);
        fileSQL = new FileSQL(context);
        if (savedInstanceState == null) {
//            add_Frag();
            add_album_Frag();
        }
    }

    private void add_album_Frag(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Album_list album_list = new Album_list();
        fragmentTransaction.add(R.id.musiclist, album_list ,"album_list");
        fragmentTransaction.commit();
    }

//
//
//    private void add_Frag() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Item_list item_list = new Item_list();
//        fragmentTransaction.add(R.id.musiclist, item_list, "item_list");
//        fragmentTransaction.commit();
//    }

    public void t1(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
//        intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);
//        intent.setType("image/*");
        startActivityForResult(intent, 99);
    }

    public void plal(View view) {
        FileSQL fileSQL = new FileSQL(getApplicationContext());
        Cursor cursor = fileSQL.info_sound_all_quiry();

        if (cursor.getCount() > 0) {
            for (int l = 0; l < cursor.getCount(); l++) {
                cursor.moveToPosition(l);
                Uri u = Uri.parse(cursor.getString(cursor.getColumnIndex("document_uri")));
                System.out.println(u.toString());
                playerService2.addUri(u);
            }
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        playerService2.player();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            u = data.getData();
            if (flag) {
                fileSQL.getUriinfo(u);
                fileSQL.add_info_sound(u);
                playerService2.addUri(u);
                playerService2.player();
            }
        }
    }

    public class con implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            l_binder = (PlayerService2.L_Binder) service;
            playerService2 = l_binder.getServer();
            flag = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
