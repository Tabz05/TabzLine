package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MyChats extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ListView listViewChat;

    String chat_id;
    String chat_name;
    String chat_type;

    Boolean chat_started;

    String currUsername;

    ArrayList<String> participant_id;

    ArrayList<String> chatIdList;
    ArrayList<String> chatNameList;
    ArrayList<String> chatTypeList;

    String chat_user_1_id ;
    String chat_user_2_id;
    String chat_user_1_username;
    String chat_user_2_username;

    String selected_chat_id;
    String selected_chat_type;
    String selected_chat_name;

    ArrayList<ArrayList<String>> groupUsername;
    ArrayList<ArrayList<String>> groupUserId;

    ArrayAdapter<String> arrayAdapter;

    public void getChatList()
    {
        try {
            db.collection("chats").orderBy("lastMessage", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    chat_started=(Boolean) document.get("chat_started");

                                    participant_id = (ArrayList) document.get("participants_id");

                                    //Toast.makeText(MyChats.this,participant_id.toString(),Toast.LENGTH_LONG).show();

                                    if(participant_id.contains(currentUser.getUid()))
                                    {
                                        if(chat_started)
                                        {
                                            chat_id = (String) document.getId();

                                            chat_type = (String) document.get("chatType");

                                            if(chat_type.equals("group"))
                                            {
                                                chat_name=(String) document.get("groupName");
                                                groupUserId.add((ArrayList) document.get("userIds"));
                                                groupUsername.add((ArrayList) document.get("usernames"));
                                            }

                                            if(chat_type.equals("single"))
                                            {
                                                chat_user_1_id = (String) document.get("user1Id");
                                                chat_user_2_id = (String) document.get("user2Id");
                                                chat_user_1_username = (String) document.get("user1");
                                                chat_user_2_username = (String) document.get("user2");

                                                if(currentUser.getUid().equals(chat_user_1_id))
                                                {
                                        /*user_to_chat_id=chat_user_2_id;
                                        user_to_chat_username = chat_user_2_username;*/

                                                    chat_name= (String) document.get("user2");


                                                }

                                                if(currentUser.getUid().equals(chat_user_2_id))
                                                {
                                        /*user_to_chat_id=chat_user_1_id;
                                        user_to_chat_username = chat_user_1_username;*/
                                                    chat_name= (String) document.get("user1");

                                                }

                                                groupUserId.add(new ArrayList<>());
                                                groupUsername.add(new ArrayList<>());
                                            }

                                            chatIdList.add(chat_id);
                                            chatTypeList.add(chat_type);
                                            chatNameList.add(chat_name);
                                        }
                                    }

                                }
                                arrayAdapter.notifyDataSetChanged();
                            } else {
                                //Log.i("Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch(Exception e)
        {
           // Toast.makeText(MyChats.this,"err: "+e,Toast.LENGTH_LONG).show();
        }

    }

    public boolean isNetworkAvailable() { // to check if connected to internet
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
        {
            connected = false;
        }

        return connected;
    }

    public void homeButton(View view)
    {
        if (currentUser != null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
            else
            {
                if (isNetworkAvailable()) {
                    Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);

                    startActivity(goToHome);
                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(goToHome);
        }
    }

    public void myProfileButton(View view)
    {
        if(currentUser!=null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            } else {
                if (isNetworkAvailable()) {
                    Toast.makeText(this, "Your Profile", Toast.LENGTH_SHORT).show();
                    Intent goToMyProfile = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(goToMyProfile);

                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }

            }
        }
        else
        {
            Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
            Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(goToSignIn);
        }
    }

    public void findPeopleButton(View view)
    {
        if(currentUser!=null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            } else {
                if (isNetworkAvailable()) {
                    Toast.makeText(this, "User List", Toast.LENGTH_SHORT).show();
                    Intent goToUserList = new Intent(getApplicationContext(), UserList.class);

                    startActivity(goToUserList);
                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }

            }
        }
        else
        {
            Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
            Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(goToSignIn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

            frbAuth = FirebaseAuth.getInstance();
            currentUser = frbAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();

            listViewChat = findViewById(R.id.myChatListView);


            participant_id = new ArrayList<String>();

            chatIdList = new ArrayList<String>();
            chatNameList = new ArrayList<String>();
            chatTypeList = new ArrayList<String>();

            groupUsername =  new ArrayList<ArrayList<String>>();
            groupUserId = new ArrayList<ArrayList<String>>();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,chatNameList);

            listViewChat.setAdapter(arrayAdapter);

            db.collection("users").document(currentUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            currUsername = (String) document.get("username");
                            getChatList();

                        } else {
                            //   Log.d(TAG, "No such document");
                        }
                    } else {
                        //Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() { //when an item of listview is clicked
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    selected_chat_id = (String) chatIdList.get(i);
                    selected_chat_type = (String) chatTypeList.get(i);
                    selected_chat_name = (String) chatNameList.get(i);

                    if (selected_chat_type.equals("single"))
                    {
                        Intent goToChat = new Intent(getApplicationContext(), ChatActivity.class);

                        goToChat.putExtra("chat_id", selected_chat_id);
                        goToChat.putExtra("chat_user_username", selected_chat_name);

                        startActivity(goToChat);
                    }

                    if(selected_chat_type.equals("group"))
                    {

                            Intent goToGroupChat = new Intent(getApplicationContext(),GroupChatActivity.class);
                            goToGroupChat.putExtra("usernames",groupUsername.get(i));
                            goToGroupChat.putExtra("userIds",groupUserId.get(i));
                            goToGroupChat.putExtra("chat_id",selected_chat_id);
                            goToGroupChat.putExtra("groupName",selected_chat_name);
                            goToGroupChat.putExtra("currUsername",currUsername);

                            startActivity(goToGroupChat);

                    }
                }
            });

    }
}