package com.smartspatial.sschat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    private String mUsername;
    private String mPhotoUrl;

    final String TAG = MainActivity.class.getName();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ListView chatview;
    private EditText editText;
    private Button button;

    ArrayList<ChatDTO> items;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("로그아웃하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mFirebaseAuth.signOut();
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        }).show();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            Log.d("check","회원가입으로 이동");
            finish();
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }

            Toast.makeText(this, mUsername + "님 환영합니다.", Toast.LENGTH_SHORT).show();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // TODO: Chat
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("ChatDB").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
                items.add(chatDTO);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


        chatview = (ListView) findViewById(R.id.chat_view);
        editText = (EditText) findViewById(R.id.chat_edit);
        button = (Button) findViewById(R.id.chat_send);

        items = new ArrayList<>();
        adapter = new ListAdapter(this, items);
        chatview.setAdapter(adapter);
        items.clear();
        Log.d("check","어댑터 연결");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals(""))
                    return;

                ChatDTO chat = new ChatDTO(mUsername, editText.getText().toString());
                databaseReference.child("ChatDB").push().setValue(chat);
                editText.setText("");
            }
        });

        getMessage();
    }


    //메세지 받아오기
    private void getMessage(){
        databaseReference.child("ChatDB").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("data", dataSnapshot.toString());
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        Map data = (Map) entry.getValue();
                        ChatDTO chatdto = new ChatDTO((String) data.get("userName"), (String) data.get("message"));
                        items.add(chatdto);

                    }

                adapter.notifyDataSetChanged();
                Log.d("check","어댑터추가");
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "채팅에러",Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        adapter.notifyDataSetChanged();
        items.clear();
        super.onDestroy();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
