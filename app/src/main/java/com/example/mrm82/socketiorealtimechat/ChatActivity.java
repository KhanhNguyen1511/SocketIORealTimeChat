package com.example.mrm82.socketiorealtimechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listMessage;
    Button btnSend;
    EditText edtMessage;
    ArrayAdapter messageAdapter;
    ArrayList<String> chatArr ;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.43.167:3000");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSocket.connect();
        initControls();
        mSocket.on("server_send_chat_message",onGetMessageFromServer);

    }

    private void initControls(){
        chatArr = new ArrayList<>();
        listMessage=findViewById(R.id.listview_message_list);
        btnSend=findViewById(R.id.button_chatbox_send);
        edtMessage=findViewById(R.id.edittext_chatbox);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_chatbox_send:
                String message = edtMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    edtMessage.setError("Input message!");
                }
                sendMessage(message);
                break;
        }
    }

    private void sendMessage(String message) {
//        ChatMessage chatMessage = new ChatMessage(message,true);
//        chatArr.add(chatMessage);
//        listMessage.setAdapter(messageAdapter);
        edtMessage.setText("");
        mSocket.emit("client_send_message",message);


    }


    private Emitter.Listener onGetMessageFromServer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content;
                    try {
                        content = data.getString("chatMessages");
                        chatArr.add(content);
                        messageAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,chatArr);
                        listMessage.setAdapter(messageAdapter);
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                exitApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exitApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out")
                .setMessage("Log out now?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent =new Intent(ChatActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

}
