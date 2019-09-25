package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int INCOMING = 0;
    private static final int OUTGOING = 1;
    private List<Message> messagesList;
    private String myId;

    private Clicklistener clicklistener;

    public MessageAdapter(String myId, List<Message> messagesList, Clicklistener clicklistener) {
        this.myId = myId;
        this.messagesList = messagesList;
        this.clicklistener = clicklistener;
    }

    public void setData(List<Message> messages) {
        this.messagesList = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public void addEnd(Message message) {
        messagesList.add(message);
        notifyItemInserted(messagesList.size()-1);
    }

    public void removeItemAt(int index) {
//        messagesList.remove(index);
        notifyItemRemoved(index);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble_view, parent, false);
        switch (viewType) {
            case INCOMING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble_view, parent, false);
                break;
            case OUTGOING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.out_message_bubble_view, parent, false);
                break;
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        if (messagesList == null) return 0;
        else return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String senderId = messagesList.get(position).getSenderId();
        if (senderId.equals(myId)) return OUTGOING;
        else return INCOMING;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTextView;
        private TextView timestampTextView;
        private ImageView messageImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            messageImageView = itemView.findViewById(R.id.photo);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clicklistener.onItemClicked(getAdapterPosition());
                    return false;
                }
            });
        }

        public void bind(final Message message) {
            if(message.getBody() == null){
                messageImageView.setVisibility(View.VISIBLE);
                messageTextView.setVisibility(View.GONE);
                Glide.with(messageImageView.getContext())
                        .load(message.getPhoto_url())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                messageImageView.setImageDrawable(resource);
                                return false;
                            }
                        })
                        .into(messageImageView);

            }else if(message.getPhoto_url() == null){
                messageImageView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(message.getBody());
            }
            timestampTextView.setText(message.getTimestamp().toDate().toLocaleString());
        }
    }

}
