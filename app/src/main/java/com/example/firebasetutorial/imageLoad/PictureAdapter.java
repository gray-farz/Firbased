package com.example.firebasetutorial.imageLoad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetutorial.R;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PicViewHolder> {

    private Context context;
    private List<UploadModel> listPics;
    public static final String TAG="aaa";
    public PictureAdapter(Context context)
    {
        Log.d(TAG, "PictureAdapter: ");
        this.context=context;
    }

    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        return new PicViewHolder(
                LayoutInflater.from(context).inflate
                        (R.layout.picture_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder: ");
        holder.setWidgets(listPics.get(position));
    }

    @Override
    public int getItemCount() {
        return listPics.size();
    }

    public void setListPics(List<UploadModel> listPics)
    {
        this.listPics=listPics;
        notifyDataSetChanged();
    }

    public class PicViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img;
        public PicViewHolder(@NonNull View itemView) {
            super(itemView);

            img= itemView.findViewById(R.id.imageProfile);
        }
        public void setWidgets(UploadModel uploadModel)
        {
            Glide.with(context).load(uploadModel.getLink()).into(img);
        }
    }
}
