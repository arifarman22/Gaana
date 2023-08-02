package com.example.gaana;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private static final int PERMISSION_COUNT = 1;

    private boolean isMusicPlayerInit;
    private ArrayList<String> musicFilesList;
    private TextAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listview);
        adapter = new TextAdapter();
        listView.setAdapter(adapter);

        musicFilesList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        } else {
            fillMusicList();
            adapter.setData(musicFilesList);
            isMusicPlayerInit = true;
        }
    }

    private boolean arePermissionsDenied() {
        for (String permission : PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (arePermissionsDenied()) {
            ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            recreate();
        } else {
            fillMusicList();
            adapter.setData(musicFilesList);
            isMusicPlayerInit = true;
        }
    }

    private void addMusicFilesFrom(String dirPath) {
        final File musicDir = new File(dirPath);
        if (!musicDir.exists()) {
            return;
        }
        final File[] files = musicDir.listFiles();
        if (files != null) {
            for (File file : files) {
                final String path = file.getAbsolutePath();
                if (path.endsWith(".mp3")) {
                    musicFilesList.add(path);
                }
            }
        }
    }

    private void fillMusicList() {
        musicFilesList.clear();
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
    }

    class TextAdapter extends BaseAdapter {

        private ArrayList<String> data = new ArrayList<>();

        void setData(ArrayList<String> nData) {
            data.clear();
            data.addAll(nData);
            notifyDataSetChanged();
        }

        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.my_item)));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String item = data.get(position);
            holder.info.setText(item.substring(item.lastIndexOf('/') + 1));
            return convertView;
        }

        class ViewHolder {
            TextView info;

            ViewHolder(TextView minfo) {
                info = minfo;
            }
        }
    }
}
