package com.example.transportdisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class Adapter  extends RecyclerView.Adapter<Adapter.ViewHolder>{

    ArrayList<String> urls;
    Context context;
    //constructor
    public Adapter(ArrayList<String> ImgUrl, Context context_)
    {
        this.urls = ImgUrl;
        this.context = context_;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView image;
        private TextView name;

        public ViewHolder(View v)
        {
            super(v);
            image =v.findViewById(R.id.img);
            name = v.findViewById(R.id.name);
        }

        public ImageView getImage(){ return this.image;}
        public TextView getName(){ return this.name;}
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(1080,800));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Glide.with(this.context)
                .load(urls.get(position))
                .into(holder.getImage());

        String str = urls.get(position);
        //Log.d("dataSnapshot2", str);
        holder.getName().setText("Data: " + str.substring(80,82) + "/" + str.substring(78,80) + "/" + str.substring(74,78) + " Ora: " + str.substring(83,85) + ":" + str.substring(85,87) + ":" + str.substring(87,89) );

    }

    @Override
    public int getItemCount()
    {
        return urls.size();
    }

}
