package com.chatapp.enconnect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chatapp.enconnect.Adapters.ChatAdapter;
import com.chatapp.enconnect.Models.MessageModel;
import com.chatapp.enconnect.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatDetailActivity<chatAdapter> extends AppCompatActivity {

   // Button buttomsheet;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText shMessage, shTime;
    private Button shSend;

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();
        final String senderId = auth.getUid();
        String reciverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_baseline_account_circle_24);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId+reciverId;
        final  String reciverRoom = reciverId+senderId;

        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            messageModels.add(model); //update recycler view

                        }

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //send button
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.etMessage.getText().toString(); //converting user message to string
                final MessageModel model = new MessageModel(senderId, message); //save sender id and message to database
                model.setTimestamp(new Date().getTime());
                binding.etMessage.setText(""); //empty text box after the message is send

                database.getReference().child("chats").child(senderRoom)       //save data for sender in database by creating child
                        .push().setValue(model).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(reciverRoom)       //save data for reciver in database by creating child
                                .push().setValue(model).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                    }
                });
            }
        });

        //////////////////////////////////////////////////////////////////
//        buttomsheet = findViewById(R.id.schedule);
//        buttomsheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });
        binding.schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    private  void showDialog(){
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.activity_schedule);

        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.activity_schedule, null);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

       // database = FirebaseDatabase.getInstance();
        //auth = FirebaseAuth.getInstance();

        //getSupportActionBar().hide();
        final String senderId = auth.getUid();
        String reciverId = getIntent().getStringExtra("userId");
//        String userName = getIntent().getStringExtra("userName");
//        String profilePic = getIntent().getStringExtra("profilePic");

//        binding.userName.setText(userName);
//        Picasso.get().load(profilePic).placeholder(R.drawable.ic_baseline_account_circle_24);

//        binding.backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });




        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager1);

        final String senderRoom = senderId+reciverId;
        final  String reciverRoom = reciverId+senderId;

        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messageModels.clear();
                        for (DataSnapshot snapshot2 : snapshot.getChildren()){
                            MessageModel model = snapshot2.getValue(MessageModel.class);
                            messageModels.add(model); //update recycler view

                        }

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        shMessage = (EditText)contactPopupView.findViewById(R.id.shMessage);
        shTime = (EditText)contactPopupView.findViewById(R.id.shTime);
        shSend = (Button)contactPopupView.findViewById(R.id.shSend);

        //int final1 = Integer.parseInt(shTime.getText().toString());
        //Long final2 = Long.parseLong(shTime.getText().toString());

//        shSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
        //send button  //shMessage, shTime;
//        shSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String message1 = shMessage.getText().toString(); //converting user message to string
//                final MessageModel model = new MessageModel(senderId, message1); //save sender id and message to database
//                model.setTimestamp(new Date().getTime());
//                shMessage.setText(""); //empty text box after the message is send
//
//                database.getReference().child("chats").child(senderRoom)       //save data for sender in database by creating child
//                        .push().setValue(model).
//                        addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                database.getReference().child("chats").child(reciverRoom)       //save data for reciver in database by creating child
//                                        .push().setValue(model).
//                                        addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//
//                                            }
//                                        });
//                            }
//                        });
//            }
//        });
///////////////////////////////////////////////////////////////
        Timer timer = new Timer();


                shSend.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                        Runnable task = new Runnable() {
                            @Override
                            public void run()
                            {
                                String message1 = shMessage.getText().toString(); //converting user message to string
                                final MessageModel model = new MessageModel(senderId, message1); //save sender id and message to database
                                model.setTimestamp(new Date().getTime());
                                shMessage.setText(""); //empty text box after the message is send

                                database.getReference().child("chats").child(senderRoom)       //save data for sender in database by creating child
                                        .push().setValue(model).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats").child(reciverRoom)       //save data for reciver in database by creating child
                                                        .push().setValue(model).
                                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        });
                                            }
                                        });
                            }
                        };
//                        TimerTask task = new TimerTask()
//                        {
//                            @Override
//                            public void run() {
//                                String message1 = shMessage.getText().toString(); //converting user message to string
//                                final MessageModel model = new MessageModel(senderId, message1); //save sender id and message to database
//                                model.setTimestamp(new Date().getTime());
//                                shMessage.setText(""); //empty text box after the message is send
//
//                                database.getReference().child("chats").child(senderRoom)       //save data for sender in database by creating child
//                                        .push().setValue(model).
//                                        addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                database.getReference().child("chats").child(reciverRoom)       //save data for reciver in database by creating child
//                                                        .push().setValue(model).
//                                                        addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void unused) {
//
//                                                            }
//                                                        });
//                                            }
//                                        });
//
//                            }
//                        };

//                        Long time1 = Long.parseLong(String.valueOf(shTime));
//                        int time2 = Integer.parseInt(String.valueOf(shTime));
//                        Calendar date = Calendar.getInstance();
//                        date.set(Calendar.MILLISECOND, time2);
                        int final1 = Integer.parseInt(shTime.getText().toString());
                        //timer.scheduleAtFixedRate(task, final1, 0, TimeUnit.SECONDS);
                          scheduler.schedule(task, final1, TimeUnit.SECONDS);
                        //timer.schedule(task, 5000);

                    }
                });

    }
}