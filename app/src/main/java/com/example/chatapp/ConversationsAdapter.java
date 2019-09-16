package com.example.chatapp;


import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {


    private List<Pair<User , String>> conversations;
    private Context context;

    public ConversationsAdapter(Context context, List<Pair<User , String>> conversations) {

        this.conversations = conversations;
        this.context = context;
    }


    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull final ViewHolder holder, int position) {


        holder.userName.setText(conversations.get(position).first.getName());

        Glide.with(holder.userImage.getContext())
                .load(conversations.get(position).first.getUrlPhoto())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.userImage.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(holder.userImage);

        holder.lastMessage.setText(conversations.get(position).second);
    }


    @Override
    public int getItemCount() {
        return conversations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        CircleImageView userImage;
        TextView userName;
        TextView lastMessage;

        public ViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
