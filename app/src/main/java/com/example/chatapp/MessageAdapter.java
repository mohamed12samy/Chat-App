package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INCOMING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble_view, parent, false);
                break;
            case OUTGOING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.out_message_bubble_view, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble_view, parent, false);
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

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clicklistener.onItemClicked(getAdapterPosition());
                    return false;
                }
            });
        }

        public void bind(final Message message) {
            messageTextView.setText(message.getBody());
            timestampTextView.setText(message.getTimestamp().toDate().toLocaleString());
        }
    }

}
