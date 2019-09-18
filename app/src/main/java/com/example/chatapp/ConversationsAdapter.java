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


    private List<Pair<User , Message>> conversations;
    private Context context;
    private Clicklistener clicklistener;

    public ConversationsAdapter(Context context, List<Pair<User , Message>> conversations,Clicklistener clicklistener) {
        this.conversations = conversations;
        this.context = context;
        this.clicklistener = clicklistener;
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
        Message message = conversations.get(position).second;
        if (message.getSenderId().equals(App.getmFirebaseUser().getEmail()))
            holder.lastMessage.setText("You: "+message.getBody());
        else holder.lastMessage.setText(message.getBody());
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicklistener.onItemClicked(getAdapterPosition());
                }
            });

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
