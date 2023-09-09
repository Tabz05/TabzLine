package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserList extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ListView listViewUser;

    private String currUsername;

    private String all_user_user_id;
    private String all_user_username;

    private ArrayList<String> allUsernameList;
    private ArrayList<String> allUserIdList;

    private String single_chat_id;

    private int user_id_compare;

    private String new_group_chat_id;

    private ArrayList<String> addedUserUsername;
    private ArrayList<String> addedUserId;

    private ArrayList<String> groupUsername;
    private ArrayList<String> groupUserId;

    private String user_to_add_username;
    private String user_to_add_id;

    private ArrayAdapter<String> arrayAdapter;

    private boolean group_mode=false;

    private EditText  textGroupName;

    private AlertDialog.Builder alertGroupName;

    private String groupName;

    private Button createGroupChatButton;
    private Button cancelGroupChatButton;

    private TextView myChatsTextView;

    private boolean peopleSelected=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        if(currentUser!=null)
        {
            menuInflater.inflate(R.menu.menu_bar_create_group,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {


            case R.id.newGroup:
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {
                        myChatsTextView.setText("Select participants");
                        group_mode=true;
                        Toast.makeText(this, "Select participants", Toast.LENGTH_LONG).show();
                        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,allUsernameList);

                        listViewUser.setAdapter(arrayAdapter);

                        listViewUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                       // listViewUser.setItemChecked(2, true);

                        createGroupChatButton.setVisibility(View.VISIBLE);
                        cancelGroupChatButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }

                return true;

            default:
                return false;
        }
    }

    public void cancelGroupChat(View view)
    {
        myChatsTextView.setText("Find people to chat with");

        createGroupChatButton.setVisibility(View.INVISIBLE);
        cancelGroupChatButton.setVisibility(View.INVISIBLE);

        group_mode=false;

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,allUsernameList);

        listViewUser.setAdapter(arrayAdapter);

        listViewUser.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void getUserList()
    {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                all_user_user_id = (String) document.getId();
                                all_user_username=(String) document.get("username");

                                if(! all_user_user_id.equals(currentUser.getUid().toString()))
                                {
                                    allUserIdList.add(all_user_user_id);
                                    allUsernameList.add(all_user_username);
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();

                        } else {
                            //Log.i("Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

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

    public void createGroupChat(View view)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        } else {
            if (isNetworkAvailable()) {
        if (group_mode)
        {
            try {

                for (int i = 0; i < listViewUser.getCount(); i++)
                {
                    if (listViewUser.isItemChecked(i))
                    {
                        peopleSelected=true;
                        break;
                    }
                }

                if(peopleSelected)
                {
                    peopleSelected=false;

                    for (int i = 0; i < listViewUser.getCount(); i++)
                    {
                        if (listViewUser.isItemChecked(i))
                        {
                            int pos = i;

                            user_to_add_username = allUsernameList.get(pos);
                            user_to_add_id = allUserIdList.get(pos);
                            addedUserUsername.add(user_to_add_username);
                            addedUserId.add(user_to_add_id);
                        }
                    }

                    new_group_chat_id = currentUser.getUid().toString() + new Timestamp(new Date());

                    addedUserId.add(currentUser.getUid());
                    addedUserUsername.add(currUsername);

                    textGroupName = new EditText(UserList.this);

                    alertGroupName = new AlertDialog.Builder(this);

                    alertGroupName
                            .setIcon(android.R.drawable.sym_action_chat)
                            .setTitle("Create group")
                            .setMessage("Enter group name")
                            .setView(textGroupName)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) { //when Yes button is clicked

                                    groupName=textGroupName.getText().toString();

                                    if(groupName!=null)
                                    {
                                        Map<String, Object> newChat = new HashMap<>();
                                        newChat.put("chatType","group");
                                        newChat.put("type", "all");
                                        newChat.put("participants_id",addedUserId);
                                        newChat.put("participants_username",addedUserUsername);
                                        newChat.put("groupName",groupName);
                                        newChat.put("lastMessage",new Timestamp(new Date()));
                                        newChat.put("chat_started",false);

                                        db.collection("chats").document(new_group_chat_id)
                                                .set(newChat)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(UserList.this, "Group created", Toast.LENGTH_LONG).show();

                                                        Intent goToGroupChat = new Intent(getApplicationContext(),GroupChatActivity.class);
                                                        goToGroupChat.putExtra("usernames",addedUserUsername);
                                                        goToGroupChat.putExtra("userIds",addedUserId);
                                                        goToGroupChat.putExtra("chat_id",new_group_chat_id);
                                                        goToGroupChat.putExtra("groupName",groupName);

                                                        goToGroupChat.putExtra("currUsername",currUsername);
                                                        startActivity(goToGroupChat);

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });

                                    }
                                    else
                                    {
                                        Toast.makeText(UserList.this,"please enter group name",Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else
                {
                    Toast.makeText(UserList.this,"Select at least one person",Toast.LENGTH_LONG).show();
                }

            }
            catch(Exception e)
            {
                Toast.makeText(UserList.this,"err: "+e,Toast.LENGTH_LONG).show();
            }

        }
            } else {
                Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        listViewUser=findViewById(R.id.listViewUser);

        allUsernameList= new ArrayList<String>();
        allUserIdList = new ArrayList<String>();

        addedUserUsername= new ArrayList<String>();
        addedUserId= new ArrayList<String>();

        createGroupChatButton = findViewById(R.id.createGroupChat);
        cancelGroupChatButton = findViewById(R.id.cancelGroupChat);

        myChatsTextView =  findViewById(R.id.myChatsTextView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,allUsernameList);

        listViewUser.setAdapter(arrayAdapter);

        groupUserId=  new ArrayList<String>();
        groupUsername =  new ArrayList<String>();

        db.collection("users").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        currUsername = (String) document.get("username");
                        getUserList();

                    } else {
                        //   Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() { //when an item of listview is clicked
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(!group_mode)
                {
                    user_id_compare = currentUser.getUid().toString().compareTo(allUserIdList.get(i)); //comparing id of current user with id of user with whom chat is happening

                    if(user_id_compare<0)
                    {
                        single_chat_id = currentUser.getUid().toString()+allUserIdList.get(i);
                    }
                    else
                    {
                        single_chat_id = allUserIdList.get(i)+currentUser.getUid().toString();
                    }

                    db.collection("chats").document(single_chat_id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists())
                                {
                                    Intent goToChat = new Intent (getApplicationContext(),ChatActivity.class);

                                    goToChat.putExtra("chat_id",single_chat_id);
                                    goToChat .putExtra("chat_user_username",allUsernameList.get(i));

                                    startActivity(goToChat);

                                }
                                else
                                {
                                    Map<String, Object> newSingleChat = new HashMap<>();

                                    newSingleChat.put("chatType","single");

                                    if(user_id_compare<0)
                                    {
                                        newSingleChat.put("user1Id",currentUser.getUid());
                                        newSingleChat.put("user2Id",allUserIdList.get(i));

                                        newSingleChat.put("user1",currUsername);
                                        newSingleChat.put("user2",allUsernameList.get(i));

                                    }
                                    else
                                    {
                                        newSingleChat.put("user2Id",currentUser.getUid());
                                        newSingleChat.put("user1Id",allUserIdList.get(i));

                                        newSingleChat.put("user2",currUsername);
                                        newSingleChat.put("user1",allUsernameList.get(i));
                                    }

                                    newSingleChat.put("participants_id",Arrays.asList(currentUser.getUid(),allUserIdList.get(i)));
                                    newSingleChat.put("lastMessage",new Timestamp(new Date()));
                                    newSingleChat.put("chat_started",false);

                                    db.collection("chats").document(single_chat_id)
                                            .set(newSingleChat)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Intent goToChat = new Intent (getApplicationContext(),ChatActivity.class);

                                                    goToChat.putExtra("chat_id",single_chat_id);
                                                    goToChat .putExtra("chat_user_username",allUsernameList.get(i));

                                                    startActivity(goToChat);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                }
                            } else {
                                //Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

    }
}