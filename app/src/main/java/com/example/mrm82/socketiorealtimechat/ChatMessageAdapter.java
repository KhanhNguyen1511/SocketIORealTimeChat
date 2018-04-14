package com.example.mrm82.socketiorealtimechat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;
    Activity context;
    int resource;
    List<ChatMessage> objects;
    public ChatMessageAdapter(@NonNull Activity context, int resource, @NonNull List<ChatMessage> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine()) return MY_MESSAGE;
        else return OTHER_MESSAGE;

    }

    TextView txtView;
    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater layoutInflater=this.context.getLayoutInflater();
//        int viewType = getItemViewType(position);
//        View row=layoutInflater.inflate(this.resource,null);
//
//        if (viewType == MY_MESSAGE) {
//            row=layoutInflater.inflate(R.layout.item_message_sent,null);
//            txtView = row.findViewById(R.id.text_message_body1);
//            ChatMessage chatMessage=this.objects.get(position);
//            txtView.setText(chatMessage.getContent());
//        }
//        else if(viewType == OTHER_MESSAGE){
//            row=layoutInflater.inflate(R.layout.item_message_received,null);
//            txtView = row.findViewById(R.id.text_message_body2);
//            ChatMessage chatMessage=this.objects.get(position);
//            txtView.setText(chatMessage.getContent());
//        }
//        return row ;
//    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater layoutInflater=this.context.getLayoutInflater();
        if (view==null){
            if (objects.get(position).isMine()){
                view = layoutInflater.inflate(R.layout.item_message_sent,null);
                TextView textView = view.findViewById(R.id.text_message_sent);
                textView.setText(objects.get(position).getContent());
            }
            else {
                view = layoutInflater.inflate(R.layout.item_message_received,null);
                TextView textView = view.findViewById(R.id.text_message_receive);
                textView.setText(objects.get(position).getContent());
            }

        }
        return  view;
    }
}
