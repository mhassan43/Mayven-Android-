package app.mayven.mayven;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import com.freesoulapps.preview.android.Preview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;


public class adapterReplies extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Preview.PreviewListener {
    public static final int FIRST_ITEM = 0;
    public static final int REST = 1;
    public static int REPLY_COUNT_STRING;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Dialog dialog;
    private Boolean isDark = false;

    private Context context;
    private List<Object> noteList;
    private String docuID;
    private int glikes;
    private CollectionReference userRef = db.collection("Users");
    private CollectionReference chatGroupRef = db.collection("ChatGroups");

    private DatabaseReference RTDNotifications = FirebaseDatabase.getInstance().getReference("Notifications");

    private MetaData mData;


    public void setData(List<Note> note) {
        this.noteList.addAll(note);
    }


    public adapterReplies(Context context, List<Object> recyclerViewItems, String docuID, int glikes) {
        this.context = context;
        this.noteList = recyclerViewItems;
        this.docuID = docuID;
        this.glikes = glikes;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case FIRST_ITEM:
                View black = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_replies2, parent, false);
                return new adapterReplies.ItemViewHolder(black);
            case REST:
                //empty
            default:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_replies, parent, false);
                return new adapterReplies.ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RegisterUsername reg = new RegisterUsername();
        List<userDB> qwe = reg.readData();

        Boolean isCommon = false;

        final String signedInUser = qwe.get(0).username;

        final adapterReplies.ItemViewHolder ItemViewHolder = (adapterReplies.ItemViewHolder) holder;
        final Note note = (Note) noteList.get(position);

        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //ItemViewHolder.userName.setTextColor(Color.WHITE);
                ItemViewHolder.post.setTextColor(Color.WHITE);
                isCommon = true;
                isDark = true;


                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }


        final CollectionReference repliesRef = db.collection("Posts").document(docuID).collection("Replies");


        String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Thumbnail%2F" + note.getId() + ".jpeg?alt=media";

        ChatGroupArray chatGroupArray = new ChatGroupArray();
        final String ts = String.valueOf(chatGroupArray.imageTime);

        Glide.with(ItemViewHolder.profilePic.getContext()).load(imgurl)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.initial_pic)
                //.apply(RequestOptions.circleCropTransform())
                //.signature(new ObjectKey(ts))
                //  .error(R.drawable.ic_person_fill)
                .into(ItemViewHolder.profilePic);


        ItemViewHolder.userName.setText(note.getOwnerName());
        ItemViewHolder.time.setText(note.getTime());
        ItemViewHolder.post.setText(note.getPost());


        switch (note.getColor()) {
            case "common":
                if (isDark) {
                    ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.gnt_white));

                } else {
                    ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.black));

                }
                break;
            case "bronze":
                ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.brown));
                break;

            case "emerald":
                ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.emerald));
                break;

            case "ruby":
                ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.ruby));
                break;

            case "mayven":
                ItemViewHolder.userName.setTextColor(context.getResources().getColor(R.color.mayvenBlue));
                break;

        }


        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(params);


        ItemViewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db.collection("Users").document(note.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();

                            try {
                                //  Block of code to try
                                TextView dialogName = dialog.findViewById(R.id.name);
                                TextView dialogDesc = dialog.findViewById(R.id.desc);
                                ImageView dialogPic = dialog.findViewById(R.id.profilePic);
                                TextView programName = dialog.findViewById(R.id.programName);
                                TextView classOf = dialog.findViewById(R.id.classOfDesc);
                                TextView classOfhead = dialog.findViewById(R.id.classOf);
                                RelativeLayout box = dialog.findViewById(R.id.box);

                                TextView classOfDesc = dialog.findViewById(R.id.classOfDesc);

                                TextView program = dialog.findViewById(R.id.program);

                                TextView achive = dialog.findViewById(R.id.achive);
                                ImageView achiveDot = dialog.findViewById(R.id.dot);

                                ImageView personalmessage = dialog.findViewById(R.id.personalmessage);
                                ImageView groupadd = dialog.findViewById(R.id.groupadd);
                                ImageView block = dialog.findViewById(R.id.block);

                                TextView msg_subtext = dialog.findViewById(R.id.msg_subtext);
                                TextView groupadd_subtext = dialog.findViewById(R.id.groupadd_subtext);
                                TextView block_subtext = dialog.findViewById(R.id.block_subtext);


                                dialogName.setText(note.getOwnerName());
                                dialogDesc.setText(note.ownerId);
                                programName.setText((String) ds.getString("programName"));

                                if (isDark) {
                                    box.setBackgroundColor(context.getResources().getColor(R.color.addingpostgrey));
                                    dialogName.setTextColor(Color.WHITE);
                                    dialogDesc.setTextColor(Color.WHITE);
                                    programName.setTextColor(Color.WHITE);
                                    //classOfDesc.setTextColor(Color.WHITE);
                                    //program.setTextColor(Color.WHITE);
                                    classOf.setTextColor(Color.WHITE);
                                    //classOfhead.setTextColor(Color.WHITE);

                                    personalmessage.setColorFilter(context.getResources().getColor(R.color.gnt_white));
                                    groupadd.setColorFilter(context.getResources().getColor(R.color.gnt_white));
                                    block.setColorFilter(context.getResources().getColor(R.color.gnt_white));

                                    msg_subtext.setTextColor(Color.WHITE);
                                    groupadd_subtext.setTextColor(Color.WHITE);
                                    block_subtext.setTextColor(Color.WHITE);


                                    //ItemViewHolder.profilePic.setColorFilter(Color.WHITE);
                                } else {
                                    box.setBackgroundColor(context.getResources().getColor(R.color.gnt_white));
                                }

                                Long points = ds.getLong("points");
                                String sPoints = String.valueOf(points);

                                achive.setText(sPoints);

                                if (note.getId().equals(signedInUser)) {
                                    //HIDE PERSONALMSG UI
                                    groupadd.setVisibility(View.INVISIBLE);
                                    block.setVisibility(View.INVISIBLE);
                                    personalmessage.setVisibility(View.INVISIBLE);

                                    msg_subtext.setVisibility(View.INVISIBLE);
                                    groupadd_subtext.setVisibility(View.INVISIBLE);
                                    block_subtext.setVisibility(View.INVISIBLE);
                                } else {
                                    groupadd.setVisibility(View.VISIBLE);
                                    block.setVisibility(View.VISIBLE);
                                    personalmessage.setVisibility(View.VISIBLE);

                                    msg_subtext.setVisibility(View.VISIBLE);
                                    groupadd_subtext.setVisibility(View.VISIBLE);
                                    block_subtext.setVisibility(View.VISIBLE);
                                }

                                if (points < 1000) {
                                    //common
                                    achive.setTextColor(context.getResources().getColor(R.color.black));
                                    achiveDot.setBackgroundColor(context.getResources().getColor(R.color.black));
                                } else if (points > 1000 && points < 10000) {
                                    //bronze
                                    achive.setTextColor(context.getResources().getColor(R.color.brown));
                                    achiveDot.setBackgroundColor(context.getResources().getColor(R.color.brown));

                                } else if (points > 10000 && points < 50000) {
                                    //gold
                                    achive.setTextColor(context.getResources().getColor(R.color.gold));
                                    achiveDot.setBackgroundColor(context.getResources().getColor(R.color.gold));

                                } else if (points > 50000 && points < 100000) {
                                    //emerald
                                    achive.setTextColor(context.getResources().getColor(R.color.emerald));
                                    achiveDot.setBackgroundColor(context.getResources().getColor(R.color.emerald));

                                } else if (points > 100000) {
                                    //ruby
                                    achive.setTextColor(context.getResources().getColor(R.color.ruby));
                                    achiveDot.setBackgroundColor(context.getResources().getColor(R.color.ruby));
                                }


                                /*
                                final ImageView dialogTripledot = dialog.findViewById(R.id.tripleDot);
                                if (signedInUser.equals(note.getId())) {
                                    dialogTripledot.setVisibility(View.GONE);
                                }
                                else {
                                    dialogTripledot.setVisibility(View.VISIBLE);
                                }
                                dialogTripledot.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);

                                        final PopupMenu popup = new PopupMenu(wrapper, dialogTripledot);

                                        Menu menu = popup.getMenu();
                                        popup.inflate(R.menu.menu_block);

                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()) {
                                                    case R.id.blockUser:
                                                        blockUser(signedInUser, note.getId(), dialog);
                                                        break;
                                                }
                                                return false;
                                            }
                                        });
                                        popup.show();
                                    }
                                });

                                 */

                                int classOfInt = Integer.valueOf(ds.getString("classOf")) + 4;
                                String classOfStr = String.valueOf(classOfInt);

                                classOf.setText("Class of" + classOfStr);

                                String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/" + note.getId() + ".jpeg?alt=media";

                                Glide.with(dialogPic.getContext()).load(imgurl)
                                        .dontAnimate()
                                        //.apply(RequestOptions.circleCropTransform())
                                        //.signature(new ObjectKey(ts))
                                        .error(R.drawable.initial_pic)
                                        .into(dialogPic);

                                block.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        blockUser(signedInUser, note.getId(), dialog);
                                    }
                                });

                                groupadd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

                                        add_livechat_dialog adding_post = new add_livechat_dialog();
                                        assert ((AppCompatActivity) context).getFragmentManager() != null;

                                        Bundle arg = new Bundle();
                                        arg.putString("uid", note.getId());
                                        adding_post.setArguments(arg);

                                        adding_post.show(manager, adding_post.getTag());
                                    }
                                });

                                personalmessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //String[] sortName = new String [] {menuItem.getId(), signedInUser};
                                        final ArrayList<String> sortName = new ArrayList<String>();
                                        sortName.add(note.getId());
                                        sortName.add(signedInUser);

                                        Log.d("1234", "clicked user " + note.getId());

                                        Collections.sort(sortName);


                                        String combinedName = "";
                                        for (String name : sortName) {
                                            combinedName += name;
                                        }

                                        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                                        final int dateNow = Integer.parseInt(timeStamp);

                                        final Map<String, Object> toAdd = new HashMap<>();
                                        toAdd.put("admins", Collections.emptyList());
                                        toAdd.put("members", sortName);
                                        toAdd.put("name", "");
                                        toAdd.put("ownerId", "");
                                        toAdd.put("type", "dm");


                                        final String finalCombinedName = combinedName.trim();
                                        chatGroupRef.document(combinedName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot dc = task.getResult();

                                                Log.d("1234", "document exist = " + dc.exists());

                                                if (!dc.exists()) {
                                                    DatabaseReference ref = RTDNotifications.push();
                                                    String postId = ref.getKey();
                                                    Map<String, Object> RTDN = new HashMap<>();

                                                    RTDN.put("gName", finalCombinedName);
                                                    RTDN.put("lastMessage", signedInUser + " has joined the group");
                                                    RTDN.put("lastUser", signedInUser);
                                                    RTDN.put("parentUser", signedInUser);
                                                    RTDN.put("timestamp", dateNow);
                                                    RTDN.put("unseenMessage", 0);

                                                    RTDNotifications.child(postId).updateChildren(RTDN);

                                                    //add target user
                                                    DatabaseReference ref2 = RTDNotifications.push();
                                                    String postId2 = ref2.getKey();
                                                    Map<String, Object> RTDN2 = new HashMap<>();

                                                    RTDN2.put("gName", finalCombinedName);
                                                    RTDN2.put("lastMessage", note.getId() + " has joined the group");
                                                    RTDN2.put("lastUser", note.getId());
                                                    RTDN2.put("parentUser", note.getId());
                                                    RTDN2.put("timestamp", dateNow);
                                                    RTDN2.put("unseenMessage", 0);

                                                    RTDNotifications.child(postId2).updateChildren(RTDN2);

                                                    ArrayList<String> empty = new ArrayList<String>();

                                                    ArrayList<String> admins = empty;
                                                    ArrayList<String> members = sortName;
                                                    String name = "";
                                                    String ownerId = "";
                                                    final String docId = finalCombinedName;
                                                    String type = "dm";

                                                    Group newGroup = new Group(admins, members, name, ownerId, 0, docId, signedInUser + " has created a dm", signedInUser, dateNow, type);
                                                    chatGroupArray.setGroupSave(newGroup);

                                                    ArrayAdapter<Group> newAdapter = new groupAdapter(context, chatGroupArray.getGroupSave());
                                                    chatFragment.listView.setAdapter(newAdapter);

                                                    chatGroupRef.document(finalCombinedName).set(toAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            chatGroupArray.setBeginDM(true);

                                                            AppCompatActivity activity = (AppCompatActivity) unwrap(v.getContext());

                                                            int fPosition = 0;

                                                            dialog.dismiss();

                                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(chatGroupArray.getGroupSave().get(fPosition).getDocId(), chatGroupArray.getGroupSave().get(fPosition).getName(), signedInUser,chatGroupArray.getGroupSave().get(fPosition).getType())).addToBackStack("chatFragment").commit();

                                                        }
                                                    });

                                                } else {

                                                    AppCompatActivity activity = (AppCompatActivity) unwrap(v.getContext());

                                                    dialog.dismiss();

                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(finalCombinedName, note.getId(), signedInUser,"dm")).addToBackStack("chatFragment").commit();

                                                }
                                            }
                                        });

                                    }
                                });


                                dialog.show();
                            } catch (Exception e) {
                                //  Block of code to handle errors
                                Toast.makeText(context, "Error fetching profile", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "This user has been deleted", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        ItemViewHolder.tripleDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);

                final PopupMenu popup = new PopupMenu(wrapper, ItemViewHolder.tripleDots);
                Menu menu = popup.getMenu();
                popup.inflate(R.menu.menu_remove_report);

                if (signedInUser.equals(note.getId())) {
                    menu.findItem(R.id.report).setVisible(false);
                } else {
                    menu.findItem(R.id.remove).setVisible(false);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                removePost(context, docuID, position, repliesRef, note.getDocumentId(), v, signedInUser, note.getId());
                                break;
                            case R.id.report:
                                /*
                                if(note.getReports().size() +1 >= 5) {
                                    reportAndDeleteReplies(signedInUser, note.getDocumentId(), context, docuID, repliesRef, position, note.getId(), note.getPost());
                                    noteList.remove(position);
                                    HomeReplies.adapter.notifyDataSetChanged();
                                }
                                else {
                                    reportPost(signedInUser, note.getDocumentId(), context, docuID, repliesRef, position);
                                }

                                 */
                                reportPost(signedInUser, note.getDocumentId(), context, docuID, repliesRef, position, note.getPost(), note.getId());


                        }
                        return false;
                    }
                });
                popup.show();
            }

        });
        Boolean flag = false;

        if (position == 0) {

            if (note.getType().equals("image")) {
                ItemViewHolder.linkorimage.setVisibility(View.VISIBLE);
                ItemViewHolder.imagePost.setVisibility(View.VISIBLE);

                Log.d("1234", "doc id = " + docuID);
                String cImage = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Post%2F" + docuID + ".jpeg?alt=media";

                Glide.with(ItemViewHolder.imagePost.getContext()).load(cImage)
                        .dontAnimate()
                        .placeholder(R.drawable.placeholder)
                        //.signature(new ObjectKey(ts))
                        //  .error(R.drawable.ic_person_fill)
                        .into(ItemViewHolder.imagePost);

            } else if (note.getType().equals("link")) {
                ItemViewHolder.linkorimage.setVisibility(View.VISIBLE);

                ItemViewHolder.imagePost.setVisibility(View.GONE);
                ItemViewHolder.preview_wrapper.setVisibility(View.VISIBLE);
                ItemViewHolder.progressBar.setVisibility(View.VISIBLE);


                String link = note.getLink();
                String temp = "";

                for (int i = 0; i < note.getLink().length(); i++) {
                    char c = note.getLink().charAt(i);
                    if (i >= 3) {
                        if (!temp.equals("htt")) {
                            link = "https://" + note.getLink();
                        }
                        break;
                    } else {
                        temp += c;
                    }
                }

                if (link != null && !link.equals("")) {
                    //menuItemHolder.preview.setData(link);

                    //menuItemHolder.richView.setVisibility(View.VISIBLE);

                    String finalLink = link;
                    RichPreview richPreview = new RichPreview(new ResponseListener() {
                        @Override
                        public void onData(MetaData metaData) {
                            mData = metaData;

                            Glide.with(ItemViewHolder.previewImage.getContext()).load(mData.getImageurl())
                                    .dontAnimate()
                                    .placeholder(R.drawable.notfound)
                                    //.signature(new ObjectKey(ts))
                                    //  .error(R.drawable.ic_person_fill)
                                    .into(ItemViewHolder.previewImage);

                            ItemViewHolder.test = true;

                            ItemViewHolder.previewTitle.setText(mData.getTitle());
                            ItemViewHolder.previewDescription.setText(mData.getDescription());
                            if(isDark){
                                ItemViewHolder.previewDescription.setTextColor(context.getResources().getColor(R.color.gnt_white));
                            }
                            ItemViewHolder.previewURL.setText(mData.getUrl());

                            ItemViewHolder.progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ItemViewHolder.preview_wrapper.setVisibility(View.GONE);

                                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.link_error_layout, null);

                                    TextView url = view.findViewById(R.id.message);
                                    url.setText(finalLink);

                                    if (isDark) {
                                        url.setBackgroundColor(context.getResources().getColor(R.color.bottomnavgrey));
                                    }

                                    url.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(finalLink));
                                            context.startActivity(i);
                                        }
                                    });

                                    ItemViewHolder.previewError.addView(view);

                                }
                            });
                        }
                    });
                    richPreview.getPreview(link);
                }

            } else {
                //text
            }


            if (isDark) {
                ItemViewHolder.wrap.setBackgroundColor(context.getResources().getColor(R.color.bottomnavgrey));
            }

            List<String> test = note.getUsersLiked();

            for (int y = 0; y < test.size(); y++) {
                if (test.get(y).equals(signedInUser)) {
                    flag = true;
                    break;
                }
            }

            if (flag == true) {
                ItemViewHolder.heart.setImageResource(R.drawable.ic_heart_fill);
                ItemViewHolder.heart.setTag(2);
            } else {
                ItemViewHolder.heart.setImageResource(R.drawable.ic_heart);
                ItemViewHolder.heart.setTag(1);
            }
            ItemViewHolder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatGroupArray chatGroupArray = new ChatGroupArray();

                    //    adapter.clear();
                    ((MainActivity) context).clearItems();
                    HomeFragment.adapter.notifyDataSetChanged();

                    ((MainActivity) context).addItemsFromFirebase(chatGroupArray.currentType, chatGroupArray.currentProgram);

                    String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    final int dateNow = Integer.parseInt(timeStamp);
                    Map<String, Object> toAdd = new HashMap<>();
                    int num = (int) ItemViewHolder.heart.getTag();
                    List<String> test = note.getUsersLiked();
                    if (num == 1) {
                        ItemViewHolder.heart.setImageResource(R.drawable.ic_heart_fill);
                        test.add(signedInUser);
                        toAdd.put("usersLiked", test);
                        db.collection("Posts").document(docuID)
                                .update(
                                        "usersLiked", test,
                                        "likes", FieldValue.increment(1),
                                        "lastAction", "likes",
                                        "lastActionTime", dateNow
                                );

                        //String likes = String.valueOf(note.getLikes() + 1);
                        //ItemViewHolder.likes.setText(likes);
                        note.likes += 1;
                        ItemViewHolder.heart.setTag(2);
                    } else {
                        ItemViewHolder.heart.setImageResource(R.drawable.ic_heart);
                        ItemViewHolder.heart.setTag(1);
                        test.remove(signedInUser);
                        toAdd.put("usersLiked", test);
                        db.collection("Posts").document(docuID)
                                .update(
                                        "likes", FieldValue.increment(-1)
                                        , "usersLiked", test
                                );

                        String likes = String.valueOf(glikes - 1);
                        //ItemViewHolder.likes.setText(likes);
                        note.likes -= 1;
                    }

                }
            });

            String replyCt;
            if (REPLY_COUNT_STRING == 1) {
                replyCt = " reply";
            } else {
                replyCt = " replies";
            }

            if (HomeReplies.isRefreshRun == false) {
                ItemViewHolder.reply.setText(note.getReplyCount() + replyCt);
            } else {
                ItemViewHolder.reply.setText(REPLY_COUNT_STRING + replyCt);
            }
            //HomeReplies.adapter.notifyDataSetChanged();
        }

    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        assert context instanceof AppCompatActivity;
        return (AppCompatActivity) context;
    }

    private void blockUser(String signedInUser, String id, Dialog dialog) {
        userRef.document(signedInUser).update(
                "blockedUsers", FieldValue.arrayUnion(id)
        );
        ChatGroupArray.BlockedUsers.add(id);
        //mContext.startActivity(new Intent(mContext, MainActivity.class));
        dialog.dismiss();
    }


    private void removePost(final Context context, final String documentId, final int position, final CollectionReference repliesRef,
                            final String id, final View view, final String signedInUser, String ownerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to remove this?");

        if(isDark){
            builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm</font>"));
        }
        Log.d("1234","isDark" + isDark);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (position == 0) {
                    db.collection("Posts").document(documentId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    noteList.remove(position);

                                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                    fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                }
                            });
                } else {
                    repliesRef.document(id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    noteList.remove(position);
                                    HomeReplies.adapter.notifyDataSetChanged();
                                    repliesRef
                                            .whereEqualTo("ownerId", signedInUser)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    int numberOfReplies = 0;
                                                    for (QueryDocumentSnapshot qc : queryDocumentSnapshots) {
                                                        numberOfReplies++;
                                                    }

                                                    if (numberOfReplies == 0) {
                                                        db.collection("Posts").document(documentId)
                                                                .update(
                                                                        "replyCount", FieldValue.increment(-1),
                                                                        "replies", FieldValue.arrayRemove(signedInUser)
                                                                );

                                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(documentId); // PUT THIS INSIDE THE FOR LOOP OF THE RETURN FIRST DOCS && INIT NOTIFICATIONS

                                                    } else {
                                                        db.collection("Posts").document(documentId)
                                                                .update(
                                                                        "replyCount", FieldValue.increment(-1)
                                                                );
                                                    }


                                                    if (signedInUser.equals(ownerId)) {
                                                        db.collection("Users").document(signedInUser)
                                                                .update(
                                                                        "points", FieldValue.increment(-20)
                                                                );
                                                    } else {
                                                        db.collection("Users").document(signedInUser)
                                                                .update(
                                                                        "points", FieldValue.increment(-100)
                                                                );
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });

                                }
                            });
                }

                dialog.dismiss();
                Toast.makeText(context, "Post removed", Toast.LENGTH_LONG);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void reportPost(final String signedInUser, final String id, final Context context, final String documentId, final CollectionReference repliesRef,
                            final int position, String post, String ownerId) {

        //final Boolean flag = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to report this post?");

        if(isDark){
            builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm</font>"));
        }



        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (position == 0) {
                    db.collection("Posts").document(documentId)
                            .update(
                                    "reports", FieldValue.arrayUnion(signedInUser)
                            );

                    CollectionReference reportRef = db.collection("Reports");
                    Map<String, Object> data = new HashMap<>();

                    data.put("ownerId", ownerId);
                    data.put("text", post);
                    data.put("type", "original");
                    data.put("originalPostId", documentId);

                    reportRef.document(documentId).set(data);

                    reportRef.document(documentId).update(
                            "reports", FieldValue.arrayUnion(signedInUser)
                    );


                } else {
                    repliesRef.document(id)
                            .update(
                                    "reports", FieldValue.arrayUnion(signedInUser)
                            );

                    CollectionReference reportRef = db.collection("Reports");
                    Map<String, Object> data = new HashMap<>();

                    data.put("ownerId", ownerId);
                    data.put("text", post);
                    data.put("type", "original");
                    data.put("originalPostId", documentId);

                    reportRef.document(documentId + "-" + id).set(data);

                    reportRef.document(documentId + "-" + id).update(
                            "reports", FieldValue.arrayUnion(signedInUser)
                    );

                }
                dialog.dismiss();
                Toast.makeText(context, "Report has been filled", Toast.LENGTH_LONG);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void reportAndDeleteReplies(final String signedInUser, final String id, final Context context, final String documentId, final CollectionReference repliesRef, final int position, final String ownerId, final String text) {
        //final Boolean flag = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to report this post?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (position == 0) {
                    db.collection("Posts").document(documentId)
                            .update(
                                    "reports", FieldValue.arrayUnion(signedInUser)
                            );
                } else {

                    CollectionReference reportRef = db.collection("Reports");
                    Map<String, Object> data = new HashMap<>();
                    data.put("ownerId", ownerId);
                    data.put("text", text);
                    data.put("type", "original");

                    reportRef.add(data);
                    repliesRef.document(id).delete();
                }
                dialog.dismiss();
                Toast.makeText(context, "Report has been filled", Toast.LENGTH_LONG);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public void onDataReady(Preview preview) {
        preview.setMessage(preview.getLink());
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName;
        TextView post;
        TextView time;
        TextView likes;
        TextView reply;
        ImageView tripleDots;
        ImageView heart;
        LinearLayout wrap;
        RelativeLayout linkorimage;
        Preview preview;
        ImageView imagePost;

        private RelativeLayout preview_wrapper;
        private RelativeLayout previewError;
        private ImageView previewImage;
        private TextView previewTitle;
        private TextView previewDescription;
        private TextView previewURL;

        private Boolean test = false;
        private ProgressBar progressBar;

        public ItemViewHolder(View v) {
            super(v);
            profilePic = v.findViewById(R.id.profilePic);
            userName = v.findViewById(R.id.userName);
            post = v.findViewById(R.id.post);
            time = v.findViewById(R.id.time);
            tripleDots = v.findViewById(R.id.tripleDot);
            heart = v.findViewById(R.id.heart);
            likes = v.findViewById(R.id.likes);
            reply = v.findViewById(R.id.REPLY);
            wrap = v.findViewById(R.id.wrap);

            linkorimage = v.findViewById(R.id.linkorimage);

            imagePost = v.findViewById(R.id.imagePost);

            preview_wrapper = (RelativeLayout) v.findViewById(R.id.preview_wrapper);

            previewError = (RelativeLayout) v.findViewById(R.id.previewError);

            previewImage = (ImageView) v.findViewById(R.id.previewImage);
            previewTitle = (TextView) v.findViewById(R.id.previewTitle);

            previewDescription = (TextView) v.findViewById(R.id.previewDescription);
            previewURL = (TextView) v.findViewById(R.id.previewURL);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FIRST_ITEM;
        }
        return REST;
    }

}