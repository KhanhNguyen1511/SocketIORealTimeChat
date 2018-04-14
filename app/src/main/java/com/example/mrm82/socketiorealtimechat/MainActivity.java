package com.example.mrm82.socketiorealtimechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtName;
    Button btnStart;

    CheckBox chkRemember;
    String prefname="UserData";
    
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.43.167:3000");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        getSupportActionBar().hide();
        edtName=findViewById(R.id.edtUserName);
        btnStart=findViewById(R.id.btnStart);
        chkRemember = findViewById(R.id.ckRemember);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStart:
                createNewUser();
                break;
            }
        }

    private void createNewUser() {
        String userName = edtName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)){
            edtName.setError("User name cannot be empty");
        }
        else {
            mSocket.emit("client_send_username",userName);
            mSocket.on("user_create_result",onNewMessage_UserCreate);
        }
    }

    private Emitter.Listener onNewMessage_UserCreate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content;
                    try {
                        content = data.getString("content");
                        if (content=="true"){
                            Log.i("CreateStatus","User create success");
                            String user = edtName.getText().toString().trim();
                            Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                            i.putExtra("UserName",user);
                            startActivity(i);

                        }else {
                            Toast.makeText(MainActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    private void saveUser() {
        //tạo đối tượng getSharedPreferences
        SharedPreferences pre=getSharedPreferences(prefname, MODE_PRIVATE);
        //tạo đối tượng Editor để lưu thay đổi
        SharedPreferences.Editor editor=pre.edit();
        String user=edtName.getText().toString();
        boolean bchk=chkRemember.isChecked();
        if(!bchk)
        {
            //xóa mọi lưu trữ trước đó
            editor.clear();
        }
        else
        {
            //lưu vào editor
            editor.putString("user", user);
            editor.putBoolean("checked", bchk);
        }
        //chấp nhận lưu xuống file
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadUser();
    }

    private void LoadUser() {
        SharedPreferences pre=getSharedPreferences(prefname,MODE_PRIVATE);
        //lấy giá trị checked ra, nếu không thấy thì giá trị mặc định là false
        boolean bchk=pre.getBoolean("checked", false);
        if(bchk)
        {
            String user=pre.getString("user","");
            edtName.setText(user);
            //startActivity(new Intent(MainActivity.this,ChatActivity.class));
        }
        chkRemember.setChecked(bchk);
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}

