package com.kankanla.m0417b;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.CoderResult;


/**
 * Created by kankanla on 2017/04/28.
 */

public class Item_list extends Fragment {
    private View view;
    private ListView listView;
    private FileSQL fileSQL;
    private String album_list_id;
    private Cursor cursor;
    private PlayerService2 playerService2;
    private boolean flag;
    private PlayerService2.L_Binder l_binder;
    private final int requestCode_ADDURL = 99;
    private final int requestCode_SETCOVER = 98;
    private final String set_icon_menu = "setAlbumicon";
    private final String set_album_noactive = "AlbumnoActive";


    public void setAlbum_list_id(String album_list_id) {
        this.album_list_id = album_list_id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("Item_list--------------onAttach----------------");
        Intent intent = new Intent(getContext(), PlayerService2.class);
        getActivity().bindService(intent, new con(), Context.BIND_AUTO_CREATE);
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Item_list--------------onCreate----------------");
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        System.out.println("Item_list--------------onCreateView----------------");
        view = inflater.inflate(R.layout.album_item_list, null);
        listView = (ListView) view.findViewById(R.id.album_item_listview);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        System.out.println("Item_list--------------onActivityCreated----------------");

        listshow();
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Item_list--------------onStart----------------");
        setButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Item_list--------------onResume----------------");
        listshow();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Item_list--------------onPause----------------");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Item_list--------------onStop----------------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Item_list--------------onDestroyView----------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Item_list--------------onDestroy----------------");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Item_list--------------onDetach----------------");
    }

    public void listshow() {
        fileSQL = new FileSQL(getActivity());
        cursor = fileSQL.get_album_id_item(album_list_id);
        Myapa myapa = new Myapa(cursor);
        listView.setAdapter(myapa);
    }

    public void setButton() {
        Button button_play = (Button) view.findViewById(R.id.button_item_play);
        Button button_previous = (Button) view.findViewById(R.id.button_item_previous);
        Button button_next = (Button) view.findViewById(R.id.button_item_next);
        Button button_add = (Button) view.findViewById(R.id.button_add_item_uri);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, requestCode_ADDURL);
            }
        });

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case requestCode_ADDURL:
                    long id = fileSQL.get_info_sound_id(data.getData());
                    fileSQL.setAlbum_link_list(album_list_id, String.valueOf(id));
                    break;
                case requestCode_SETCOVER:
                    fileSQL.set_album_icon(data.getData(),album_list_id);
                    Toast.makeText(playerService2, data.getData().toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 21, 0, set_icon_menu);
        menu.add(0, 22, 0, set_album_noactive);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 21:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, requestCode_SETCOVER);
                break;
            case 22:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //    Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("audio/*");
//        intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);
//        intent.setType("image/*");
//    startActivityForResult(intent, 99);
    private class Myapa extends BaseAdapter {
        private Cursor cursor;

        public Myapa(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public int getCount() {
            System.out.println(cursor.getCount());
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.moveToPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (cursor != null) {
                if (convertView == null) {
                    view = getActivity().getLayoutInflater().inflate(R.layout.item_list, null);
                    cursor.moveToPosition(position);
                    TextView textView = (TextView) view.findViewById(R.id.ittess);
                    textView.setText(cursor.getString(cursor.getColumnIndex("_display_name")));
                    textView.setTextSize(22);
                } else {
                    view = convertView;
                    cursor.moveToPosition(position);
                    TextView textView = (TextView) view.findViewById(R.id.ittess);
                    textView.setText(cursor.getString(cursor.getColumnIndex("_display_name")));
                    textView.setTextSize(22);
                }
                return view;
            } else {
                return null;
            }
        }
    }
}
