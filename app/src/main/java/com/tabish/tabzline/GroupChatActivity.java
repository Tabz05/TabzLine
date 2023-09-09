package com.tabish.tabzline;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.rgb;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ArrayList<String> userIds;

    private ArrayList<String> usernames;

    private EditText messageText;
    private LinearLayout chatLinearLayout;

    private String chat_id;

    private int image_selected=0;

    private String receivedMessageText;
    private String sentMessageText;
    private String from;

    private String currUsername;

    private String groupName;

    private String image_uri;

    private String messageType;

    private Timestamp timeStamp;

    private Timestamp messageSentTimeStamp;

    private Uri selectedImage;

    private ImageView ImageMessageToSend;

    private ScrollView scrollview;

    private ListenerRegistration registrationGroup;

    private DocumentReference chat_details;

    private String fromName;

    private void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    //requesting permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    public void sendImage (View view)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //checking if permission for gallery has been granted or not
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }


    public void sendMessage(View view)
    {
        try { //to hide keyboard once send button is pressed
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception var2)
        {
        }

        /*scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });*/

        if(messageText.getText().toString().length()!=0)
        {
            Map<String, Object> newMessageSent = new HashMap<>();
            newMessageSent.put("type", "text");
            newMessageSent.put("from",currentUser.getUid().toString());
            newMessageSent.put("fromName",currUsername);
            newMessageSent.put("text",messageText.getText().toString());

            messageText.setText("");

            messageSentTimeStamp = new Timestamp(new Date());
            newMessageSent.put("timestamp",messageSentTimeStamp);

            chat_details.update("chat_started",true);
            chat_details.update("lastMessage",messageSentTimeStamp);

            db.collection("chats").document(chat_id).collection("messages").document(currentUser.getUid()+messageSentTimeStamp)
                    .set(newMessageSent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
        if(image_selected==1)
        {
            chatLinearLayout.removeView(ImageMessageToSend);

            image_selected=0;
            Map<String, Object> newMessageSent = new HashMap<>();
            newMessageSent.put("type", "image");
            newMessageSent.put("from",currentUser.getUid().toString());
            newMessageSent.put("fromName",currUsername);

            messageSentTimeStamp = new Timestamp(new Date());
            newMessageSent.put("timestamp",messageSentTimeStamp);

            chat_details.update("chat_started",true);
            chat_details.update("lastMessage",messageSentTimeStamp);

            StorageReference ref = storageReference.child("chats").child(chat_id).child("messages").child(currentUser.getUid()+messageSentTimeStamp);

            // adding listeners on upload
            // or failure of image
            ref.putFile(selectedImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    newMessageSent.put("uri",uri.toString());

                                    db.collection("chats").document(chat_id).collection("messages").document(currentUser.getUid()+messageSentTimeStamp)
                                            .set(newMessageSent)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                }
                            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

    
            scrollview =  findViewById(R.id.chatScrollView);

            /*scrollview.post(new Runnable() {
                @Override
                public void run() {
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });*/

            messageText =findViewById(R.id.messageText);
            chatLinearLayout=findViewById(R.id.chatLinearLayout);

            frbAuth = FirebaseAuth.getInstance();
            currentUser = frbAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            Intent fromUserList = getIntent();

            usernames=  new ArrayList<String>();
            userIds =  new ArrayList<String>();

            usernames = (ArrayList<String>)getIntent().getSerializableExtra("usernames");
            userIds = (ArrayList<String>)getIntent().getSerializableExtra("userIds");
            chat_id = fromUserList.getStringExtra("chat_id");
            groupName = fromUserList.getStringExtra("groupName");
            currUsername= fromUserList.getStringExtra("currUsername");

            setTitle(groupName);

            chat_details = db.collection("chats").document(chat_id);

            registrationGroup=db.collection("chats").document(chat_id).collection("messages").orderBy("timestamp")
                    .addSnapshotListener(GroupChatActivity.this,new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:

                                        from = (String) dc.getDocument().get("from");
                                        fromName = (String) dc.getDocument().get("fromName");
                                        messageType = (String) dc.getDocument().get("type");
                                        timeStamp = (Timestamp) dc.getDocument().get("timestamp");

                                        if (from.equals(currentUser.getUid()))
                                        {
                                            if (messageType.equals("text"))
                                            {
                                                sentMessageText = (String) dc.getDocument().get("text");

                                                TextView sentTextMessage = new TextView(getApplicationContext());

                                                LinearLayout.LayoutParams sentTextMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                sentTextMessageLayoutParams.setMargins(0, 20, 5, 0);
                                                sentTextMessageLayoutParams.gravity = Gravity.RIGHT;
                                                sentTextMessage.setGravity(Gravity.LEFT);

                                                sentTextMessage.setPadding(5, 5, 5, 5);
                                                sentTextMessage.setText("-You\n\n"+sentMessageText);
                                                sentTextMessage.setTextSize(20);
                                                sentTextMessage.setTextColor(getResources().getColor(R.color.black));
                                                sentTextMessage.setBackgroundColor(rgb(51, 219, 245));

                                                sentTextMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));

                                                chatLinearLayout.addView(sentTextMessage, sentTextMessageLayoutParams);
                                            }

                                            if (messageType.equals("image"))
                                            {

                                                TextView sentTextMessage = new TextView(getApplicationContext());

                                                LinearLayout.LayoutParams sentTextMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                sentTextMessageLayoutParams.setMargins(0, 20, 5, 0);
                                                sentTextMessageLayoutParams.gravity = Gravity.RIGHT;
                                                sentTextMessage.setGravity(Gravity.LEFT);

                                                sentTextMessage.setPadding(5, 5, 5, 5);
                                                sentTextMessage.setText("-You");
                                                sentTextMessage.setTextSize(20);
                                                sentTextMessage.setTextColor(getResources().getColor(R.color.black));
                                                sentTextMessage.setBackgroundColor(rgb(51, 219, 245));

                                                sentTextMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));

                                                chatLinearLayout.addView(sentTextMessage, sentTextMessageLayoutParams);

                                                image_uri = (String) dc.getDocument().get("uri");

                                                ImageView sentImageMessage = new ImageView(getApplicationContext());

                                                LinearLayout.LayoutParams sentImageMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        2*chatLinearLayout.getWidth() / 3,
                                                        4*chatLinearLayout.getWidth() / 5);

                                               // sentImageMessage.setAdjustViewBounds(true);
                                                sentImageMessage.setScaleType(ImageView.ScaleType.FIT_XY);

                                                sentImageMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));
                                                sentImageMessage.setMaxHeight((4*chatLinearLayout.getWidth() / 5));

                                                sentImageMessageLayoutParams.setMargins(0, 5, 5, 0);

                                                sentImageMessageLayoutParams.gravity = Gravity.RIGHT;

                                                Glide.with(getApplicationContext()).load(image_uri.toString()).into(sentImageMessage);

                                                chatLinearLayout.addView(sentImageMessage, sentImageMessageLayoutParams);

                                            }

                                        } else {
                                            if (messageType.equals("text")) {
                                                receivedMessageText = (String) dc.getDocument().get("text");

                                                TextView receivedTextMessage = new TextView(getApplicationContext());

                                                LinearLayout.LayoutParams receivedTextMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                receivedTextMessageLayoutParams.setMargins(5, 20, 0, 0);
                                                receivedTextMessageLayoutParams.gravity = Gravity.LEFT;
                                                receivedTextMessage.setGravity(Gravity.LEFT);

                                                receivedTextMessage.setPadding(5, 5, 5, 5);
                                                receivedTextMessage.setText("-"+fromName+"\n\n"+receivedMessageText);
                                                receivedTextMessage.setTextSize(20);
                                                receivedTextMessage.setTextColor(getResources().getColor(R.color.black));
                                                receivedTextMessage.setBackgroundColor(rgb(255, 13, 214));

                                                receivedTextMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));

                                                chatLinearLayout.addView(receivedTextMessage, receivedTextMessageLayoutParams);
                                            }

                                            if (messageType.equals("image")) {

                                                TextView receivedTextMessage = new TextView(getApplicationContext());

                                                LinearLayout.LayoutParams receivedTextMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                                receivedTextMessageLayoutParams.setMargins(5, 20, 0, 0);
                                                receivedTextMessageLayoutParams.gravity = Gravity.LEFT;
                                                receivedTextMessage.setGravity(Gravity.LEFT);

                                                receivedTextMessage.setPadding(5, 5, 5, 5);
                                                receivedTextMessage.setText("-"+fromName);
                                                receivedTextMessage.setTextSize(20);
                                                receivedTextMessage.setTextColor(getResources().getColor(R.color.black));
                                                receivedTextMessage.setBackgroundColor(rgb(255, 13, 214));

                                                receivedTextMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));

                                                chatLinearLayout.addView(receivedTextMessage, receivedTextMessageLayoutParams);

                                                image_uri = (String) dc.getDocument().get("uri");

                                                ImageView receivedImageMessage = new ImageView(getApplicationContext());

                                                LinearLayout.LayoutParams receivedImageMessageLayoutParams = new LinearLayout.LayoutParams(
                                                        2*chatLinearLayout.getWidth() / 3,
                                                        4*chatLinearLayout.getWidth() / 5);

                                                //receivedImageMessage.setAdjustViewBounds(true);
                                                receivedImageMessage.setScaleType(ImageView.ScaleType.FIT_XY);

                                                receivedImageMessage.setMaxWidth((2*chatLinearLayout.getWidth() / 3));
                                                receivedImageMessage.setMaxHeight((4*chatLinearLayout.getWidth() / 5));

                                                receivedImageMessageLayoutParams.setMargins(5, 5, 0, 0);

                                                receivedImageMessageLayoutParams.gravity = Gravity.LEFT;

                                                Glide.with(getApplicationContext()).load(image_uri.toString()).into(receivedImageMessage);

                                                chatLinearLayout.addView(receivedImageMessage, receivedImageMessageLayoutParams);

                                            }

                                        }

                                        scrollview.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                                            }
                                        });

                                        break;
                                    case MODIFIED:

                                        break;
                                    case REMOVED:

                                        break;
                                }
                            }

                        }
                    });



    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes

                        Intent data = result.getData();

                        selectedImage = data.getData();

                        image_selected=1;

                        ImageMessageToSend= new ImageView(getApplicationContext());

                        LinearLayout.LayoutParams sentImageMessageLayoutParams = new LinearLayout.LayoutParams(
                                2*chatLinearLayout.getWidth() / 3,
                                4*chatLinearLayout.getWidth() / 5);

                        //ImageMessageToSend.setAdjustViewBounds(true);
                        ImageMessageToSend.setScaleType(ImageView.ScaleType.FIT_XY);

                        ImageMessageToSend.setMaxWidth((2*chatLinearLayout.getWidth() / 3));
                        ImageMessageToSend.setMaxHeight((4*chatLinearLayout.getWidth() / 5));

                        sentImageMessageLayoutParams.setMargins( 0 , 20, 0 , 0 ) ;

                        sentImageMessageLayoutParams.gravity = Gravity.CENTER;

                        Glide.with(getApplicationContext()).load(selectedImage.toString()).into(ImageMessageToSend);

                        chatLinearLayout.addView(ImageMessageToSend,sentImageMessageLayoutParams);

                        scrollview.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                    }
                }
            });
}