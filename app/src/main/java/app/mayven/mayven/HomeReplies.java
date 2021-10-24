package app.mayven.mayven;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class HomeReplies extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeReplies";

    private FragmentManager fm = getFragmentManager();

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postRef = db.collection("Posts");
    private CollectionReference imageRef = db.collection("Users");
    private CollectionReference globalRef;
    //FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseFunctions mFunctions;
    public static adapterReplies adapter;

    private RecyclerView recyclerView;

    private EditText replyText;
    private ImageView send;
    private ImageView backbutton;

    private TextView companyName;

    public static View view;
    private String schoolId;

    public static List<Object> mRecyclerViewItemsReplies = new ArrayList<>();
    private List<String> userLiked = new ArrayList<>();
    private List<String> replies = new ArrayList<>();
    private List<String> reports = new ArrayList<>();
    public static Boolean isRefreshRun = false;

    RegisterUsername reg = new RegisterUsername();
    List<userDB> qwe = reg.readData();
    final String signedInName = qwe.get(0).name;

    Activity mActivity;

    public static int position;
    DocumentSnapshot lastDocument = null;

    int getreplies, time,glikes;
    public static String post, userName, docuId, imageUrl, ownerId, freplies,link,color,type;


    public HomeReplies() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public HomeReplies(int getreplies, String post, String userName, String URL, String docuId, String ownerId, int time, List<String> userLiked,int glikes, int position, List<String> replies, List<String> reports,
                       String link,String color, String type) {

        this.type = type;
        this.getreplies = getreplies;
        this.post = post;
        this.userName = userName;
        this.imageUrl = URL;
        this.docuId = docuId;
        this.ownerId = ownerId;
        this.time = time;
        this.userLiked = userLiked;
        this.glikes = glikes;
        this.position = position;
        this.replies = replies;
        this.reports = reports;
        this.link = link;
        this.color = color;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final CollectionReference doc = postRef.document(docuId).collection("Replies");
        globalRef = doc;
        view = inflater.inflate(R.layout.fragment_home_replies, container, false);


        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        backbutton = view.findViewById(R.id.back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        adapterReplies.REPLY_COUNT_STRING = getreplies;

        doc.orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> empty = new ArrayList<String>();
                Note addToTop = new Note(time, ownerId, post, userName, "", empty, userLiked, glikes, getreplies, "", "", empty,type,link,color);

                mRecyclerViewItemsReplies.clear();
                mRecyclerViewItemsReplies.add(0, addToTop);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String originalPost = document.getString("originalPost");
                    int timestamp = Integer.parseInt(document.get("timestamp").toString());
                    String ownerId = document.getString("ownerId");
                    String text = document.getString("text");
                    String ownerName = document.getString("ownerName");
                    String imageURL = document.getString("imageURL");
                    List<String> replies = (List<String>) document.get("replies");
                    List<String> usersLiked = (List<String>) document.get("usersLiked");
                    //int likes = Integer.parseInt(document.get("likes").toString());
                    //int replyCount = Integer.parseInt(document.get("replyCount").toString());
                    String documentId = document.getId();
                    List<String> reports = (List<String>) document.get("reports");
                    String type = document.getString("type");
                    String link = document.getString("link");
                    String color = document.getString("color");

                    Note note = new Note(timestamp, ownerId, text, ownerName, imageURL, replies, usersLiked, 0, 0, documentId, originalPost, reports,type,link,color);
                    mRecyclerViewItemsReplies.add(note);


                    lastDocument = document;
                }


                RegisterUsername reg = new RegisterUsername();
                List<userDB> qwe = reg.readData();

                ((MainActivity) getActivity()).hideNav();

                mFunctions = FirebaseFunctions.getInstance();

                final String signedInUser = qwe.get(0).name;
                final String signedInUsername = qwe.get(0).username;

                replyText = view.findViewById(R.id.reply);

                final ImageView imageHolder = view.findViewById(R.id.profilePic);
                TextView nameHolder = view.findViewById(R.id.userName);
                companyName = view.findViewById(R.id.companyName);
                final TextView postHolder = view.findViewById(R.id.post);
                final CollectionReference repliesRef = postRef.document(docuId).collection("Replies");
                final CollectionReference notificationRef = imageRef.document(signedInUsername).collection("Replies");
                Query query = repliesRef.orderBy("timestamp", Query.Direction.ASCENDING);
                recyclerView = view.findViewById(R.id.recycle);
                recyclerView.setHasFixedSize(true);

                adapter = new adapterReplies(mActivity, mRecyclerViewItemsReplies,docuId,glikes);


                //  adapter.notifyDataSetChanged();

                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(mDividerItemDecoration);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
                recyclerView.setLayoutManager(layoutManager);

                // Specify an adapter.
                recyclerView.setAdapter(adapter);

                int nightModeFlags =
                        getActivity().getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;

                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        companyName.setTextColor(Color.WHITE);
                        replyText.setTextColor(Color.BLACK);
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        break;
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        break;
                }


                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            Query cRef;

                            if(lastDocument == null) {
                                cRef = doc.orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
                            }
                            else {
                                cRef = doc.orderBy("timestamp", Query.Direction.DESCENDING).limit(5).startAfter(lastDocument);
                            }

                            cRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<String> empty = new ArrayList<String>();
                                    Note addToTop = new Note(time, ownerId, post, userName, "", empty, empty, 0, 0, "", "", empty,type,link,color);

                                    int count = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        count++;
                                        String originalPost = document.getString("originalPost");
                                        int timestamp = Integer.parseInt(document.get("timestamp").toString());
                                        String ownerId = document.getString("ownerId");
                                        String text = document.getString("text");
                                        String ownerName = document.getString("ownerName");
                                        String imageURL = document.getString("imageURL");
                                        List<String> replies = (List<String>) document.get("replies");
                                        List<String> usersLiked = (List<String>) document.get("usersLiked");
                                        //int likes = Integer.parseInt(document.get("likes").toString());
                                        //int replyCount = Integer.parseInt(document.get("replyCount").toString());
                                        String documentId = document.getId();
                                        List<String> reports = (List<String>) document.get("reports");
                                        String type = document.getString("type");
                                        String link = document.getString("link");
                                        String color = document.getString("color");


                                        Note note = new Note(timestamp, ownerId, text, ownerName, imageURL, replies, usersLiked, 0, 0, documentId, originalPost, reports,type,link,color);
                                        mRecyclerViewItemsReplies.add(note);

                                        lastDocument = document;
                                    }

                                    if(count != 0 ){
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                    }
                });



                send = view.findViewById(R.id.b2);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                        final int time = Integer.parseInt(timeStamp);


                        DocumentReference doc = postRef.document(docuId);
                        doc.update("replies", FieldValue.arrayUnion(signedInUsername),
                                "lastAction", "reply",
                                "lastActionTime", time,
                                "replyCount", FieldValue.increment(1)
                        );


                        if(!signedInUsername.equals(ownerId)){
                            db.collection("Users").document(signedInUsername).update(
                                    "points", FieldValue.increment(100)
                            );

                            db.collection("Users").document(ownerId).update(
                                    "points", FieldValue.increment(20)
                            );
                        }else {
                            Log.d("1234","commented under your post owner = " + ownerId + " signed in = " + signedInUser);
                        }


                        String replyTextString = replyText.getText().toString();
                        replyText.getText().clear();
                        Map<String, Object> toAdd = new HashMap<>();
                        toAdd.put("originalPost", docuId);
                        toAdd.put("ownerId", signedInUsername);
                        toAdd.put("ownerName", signedInUser);
                        toAdd.put("text", replyTextString);
                        toAdd.put("timestamp", time);
                        toAdd.put("type", "reply");
                        toAdd.put("color", color);
                        toAdd.put("reports", Collections.emptyList());

                        AddReplies(toAdd,signedInUsername,replyTextString,signedInUser,repliesRef);


                        FirebaseMessaging.getInstance().subscribeToTopic(docuId) // PUT THIS INSIDE THE FOR LOOP OF THE RETURN FIRST DOCS && INIT NOTIFICATIONS
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                        } else {
                                            String topic = "highScores";
                                            ChatGroupArray chatGroupArr = new ChatGroupArray();
                                            chatGroupArr.cloudNotifications.add(docuId);
                                            // See documentation on defining a message payload.
                                            addMessage(docuId);
                                        }
                                    }
                                });
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return view;
    }

    private void AddReplies(Map<String, Object> toAdd, String signedInUsername, String replyTextString, String signedInUser, CollectionReference repliesRef) {
        repliesRef.add(toAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                final int timeNow = Integer.parseInt(timeStamp);

                String id = documentReference.getId();

                Log.d("1234","reply text string = " + replyTextString);

                Note note = new Note(timeNow,signedInUsername,replyTextString,signedInUser,"",Collections.EMPTY_LIST,Collections.EMPTY_LIST,0,0, id,docuId,Collections.EMPTY_LIST,type,"","");

                mRecyclerViewItemsReplies.add(1,note);

                adapter.notifyDataSetChanged();
            }
        });
    }


    private Task<String> addMessage(String docId) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("docId", docId);
        data.put("title", "Mayven");
        data.put("message", signedInName + " has replied to a post");

        return mFunctions
                .getHttpsCallable("webhookNew")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
    public static void GetNewReplies() {
        mRecyclerViewItemsReplies.clear();
        db.collection("Posts").document(docuId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                isRefreshRun = true;
                DocumentSnapshot dc = task.getResult();




                int timestamp = Integer.parseInt(dc.get("timestamp").toString());
                String ownerId = dc.getString("ownerId");
                String text = dc.getString("text");
                String ownerName = dc.getString("ownerName");
                String imageURL = dc.getString("imageURL");
                List<String> replies = (List<String>) dc.get("replies");
                List<String> usersLiked = (List<String>) dc.get("usersLiked");
                int likes = Integer.parseInt(dc.get("likes").toString());
                int replyCount = Integer.parseInt(dc.get("replyCount").toString());
                String documentId = dc.getId();
                String originalPost = dc.getString("originalPost");
                List<String> reports = (List<String>) dc.get("reports");
                String type = dc.getString("type");
                String link = dc.getString("link");
                String color = dc.getString("color");


                adapterReplies.REPLY_COUNT_STRING=replyCount;

                ChatGroupArray chatGroupArray = new ChatGroupArray();


                Note addBack = new Note(timestamp, ownerId, text, ownerName, imageURL, replies, usersLiked, likes, replyCount, documentId, originalPost, reports,type,link,color);
                mRecyclerViewItemsReplies.add(0,addBack);



                adapter.notifyDataSetChanged();

                HomeFragment.adapter.notifyDataSetChanged();

            }
        });
    }
    private void refreshReplies(){
        isRefreshRun = true;
        mRecyclerViewItemsReplies.clear();


        final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        final int time = Integer.parseInt(timeStamp);



        db.collection("Posts").document(docuId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();

                int replyCount = Integer.parseInt(dc.get("replyCount").toString());
                adapterReplies.REPLY_COUNT_STRING=replyCount;

            }
        });

        postRef.document(docuId).collection("Replies")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> empty = new ArrayList<String>();
                Note addToTop = new Note(time, ownerId, post, userName, "", empty, empty, 0, 0, "", "", empty,type,link,color);
                mRecyclerViewItemsReplies.add(0,addToTop);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String originalPost = document.getString("originalPost");
                    int timestamp = Integer.parseInt(document.get("timestamp").toString());
                    String ownerId = document.getString("ownerId");
                    String text = document.getString("text");
                    String ownerName = document.getString("ownerName");
                    String imageURL = document.getString("imageURL");
                    List<String> replies = (List<String>) document.get("replies");
                    List<String> usersLiked = (List<String>) document.get("usersLiked");
                    //int likes = Integer.parseInt(document.get("likes").toString());
                    //int replyCount = Integer.parseInt(document.get("replyCount").toString());
                    String documentId = document.getId();
                    List<String> reports = (List<String>) document.get("reports");
                    String type = document.getString("type");
                    String link = document.getString("link");
                    String color = document.getString("color");

                    Note note = new Note(timestamp, ownerId, text, ownerName, imageURL, replies, usersLiked, 0, 0, documentId, originalPost, reports,type,link,color);
                    mRecyclerViewItemsReplies.add(note);


                    lastDocument = document;
                }
                adapter.notifyDataSetChanged();

            }
        });




    }


    @Override
    public void onRefresh() {

        //mRecyclerViewItemsReplies.clear();
/*
        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
            mSwipeRefreshLayout.setRefreshing(false);
        }

         */
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();


        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        final int time = Integer.parseInt(timeStamp);
        ChatGroupArray chatGroupArray = new ChatGroupArray();
        chatGroupArray.imageTime = time;
        mSwipeRefreshLayout.setRefreshing(false);

        refreshReplies();
        adapter.notifyDataSetChanged();
          //GetNewReplies();
          //HomeFragment.adapter.notifyDataSetChanged();
    }
}
