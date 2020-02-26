package com.fly.getmorepicturedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    List<String> imgUri = new ArrayList<>();
    private Set<String> uris = new HashSet<>();
    private SparseBooleanArray mSelectedPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mSelectedPositions = new SparseBooleanArray();
        recyclerView.setAdapter(new MyAdapter());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 50);


        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imgUri.add(uri);
        }
    }

    public void look(View view) {
        for (String s : uris) {
            Log.d("123", "look: " + s);
        }
        Log.d("123", "look: " + uris.size());
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_image, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
            final String uri = imgUri.get(position);
            Glide.with(getApplicationContext())
                    .load(uri)
                    .override(300, 300)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isItemChecked(position)) {
                        setItemChecked(position, false);
                        holder.imageView2.setVisibility(View.GONE);
                        uris.remove(uri);
                    } else {
                        setItemChecked(position, true);
                        uris.add(uri);
                        holder.imageView2.setImageDrawable(getDrawable(R.drawable.check));
                        holder.imageView2.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return imgUri.size();
        }

        private boolean isItemChecked(int position) {
            return mSelectedPositions.get(position);
        }

        private void setItemChecked(int position, boolean isChecked) {
            mSelectedPositions.put(position, isChecked);
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView imageView2;

            MyHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                imageView2 = itemView.findViewById(R.id.imageView2);
            }
        }
    }
}

