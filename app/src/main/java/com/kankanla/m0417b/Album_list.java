package com.kankanla.m0417b;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kankanla on 2017/05/10.
 */

public class Album_list extends Fragment {
    private Context context;
    private View view;
    private FileSQL fileSQL;
    private int g;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        fileSQL = new FileSQL(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.album_list, null);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        list_album();
        Button button = (Button) view.findViewById(R.id.button_album_list_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileSQL.add_album();
                list_album();
            }
        });
    }


    private int uNumColumns;
    private int img_size;

    public void list_album() {
//        final Cursor cursor = fileSQL.get_album_all_item();
        final Cursor cursor = fileSQL.get_album_all_item_active();
        GridView gridView = (GridView) view.findViewById(R.id.album_Gridview);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        int y = point.y;
        if (y > x) {
            uNumColumns = 2;
            img_size = (x - 50) / uNumColumns;
        } else {
            uNumColumns = 3;
            img_size = (x - 50) / uNumColumns;
        }
        gridView.setColumnWidth(img_size);
        gridView.setNumColumns(uNumColumns);
        gridView.setPadding(18, 18, 18, 18);
        gridView.setVerticalSpacing(18);
        gridView.setHorizontalSpacing(18);
        Album_adapter album_adapter = new Album_adapter();
        album_adapter.setCursor(cursor);
        gridView.setAdapter(album_adapter);

        /*
        アルバムを無表示にする
         */
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                fileSQL.set_album_frag(FileSQL.ALBUM_FRAG_NOACTIVETE, cursor.getString(cursor.getColumnIndex("_id")));
                return false;
            }
        });

        /*
        アルバムを表示するためにFragementを開く
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                cursor.moveToPosition(position);
                Item_list item_list = new Item_list();
                item_list.setAlbum_list_id(cursor.getString(cursor.getColumnIndex("_id")));
                fragmentTransaction.addToBackStack("top");
                fragmentTransaction.replace(R.id.musiclist, item_list, "it");
                fragmentTransaction.commit();
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class Album_adapter extends BaseAdapter {
        private Cursor cursor;

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public int getCount() {
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
            ImageView imageView;
            TextView textView, textView1;
            cursor.moveToPosition(position);
            String uri = cursor.getString(cursor.getColumnIndex("album_icon"));

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.album_list_item, null);
                view.setBackgroundResource(R.color.colorPrimary);
                imageView = (ImageView) view.findViewById(R.id.album_imageView);
                imageView.setMinimumHeight(img_size);
                imageView.setMinimumWidth(img_size);
                textView = (TextView) view.findViewById(R.id.album_album_name);
                textView1 = (TextView) view.findViewById(R.id.album_album_count);
                textView.setText("xxxxxxxxxxxxx555xxxxxxx");
                textView1.setText("yyyyyyyyyyy555yyyyyyyy");
                if (uri != null) {
                    imageView.setImageURI(Uri.parse(uri));
                }
            } else {
                view = convertView;
                imageView = (ImageView) view.findViewById(R.id.album_imageView);
                imageView.setMinimumHeight(img_size);
                imageView.setMinimumWidth(img_size);
                textView = (TextView) view.findViewById(R.id.album_album_name);
                textView1 = (TextView) view.findViewById(R.id.album_album_count);
                textView.setText("xxxxxxxxxxxxxx555xxxxxxxxxxxx");
                textView1.setText("yyyyyyyyyyyyyyy555yyyyyyyyyy");
                if (uri != null) {
                    imageView.setImageURI(Uri.parse(uri));
                }
            }

            return view;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView = new TextView(getContext());
//            cursor.moveToPosition(position);
//            textView.setText(cursor.getString(cursor.getColumnIndex("album_name")));
////            textView.setText(cursor.getString(cursor.getColumnIndex("last_modified")));
////            textView.setText(cursor.getString(cursor.getColumnIndex("_id")));
//            textView.setTextSize(55);
//            textView.setHeight(500);
//            textView.setWidth(500);
//
//            textView.setBackgroundResource(R.color.colorAccent);
//            return textView;
//        }
    }


}
