package app.mayven.mayven;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class groupMembersAdapter extends ArrayAdapter<groupMembers> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatGroups = db.collection("ChatGroups");
    private CollectionReference userRef = db.collection("Users");
    private DatabaseReference RTDNotifications = FirebaseDatabase.getInstance().getReference("Notifications");



    private Dialog dialog;


    ArrayList<groupMembers> list;

    Boolean isTrue;

    String groupId;
    Context context;
    Boolean isDark = false;

    public groupMembersAdapter(@NonNull Context context, ArrayList<groupMembers> group, String groupid, Boolean isTrue) {
        super(context, R.layout.list_groupmembers, group);
        this.context = context;
        groupId = groupid;
        list = group;
        this.isTrue = isTrue;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.list_groupmembers, null);
        }

        RegisterUsername reg = new RegisterUsername();
        List<userDB> qwe = reg.readData();

        final String signedInUsername = qwe.get(0).username;

        final groupMembers group = getItem(position);
        final TextView userid = (TextView) row.findViewById(R.id.userName);
        final TextView username = (TextView) row.findViewById(R.id.name);
        TextView permission = (TextView) row.findViewById(R.id.permissions);
        ImageView profilePic = (ImageView) row.findViewById(R.id.profilePic);
        final ImageView tripleDot = (ImageView) row.findViewById(R.id.tripleDot);

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                username.setTextColor(Color.WHITE);
                userid.setTextColor(Color.WHITE);
                isDark = true;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Thumbnail%2F" + group.getName() + ".jpeg?alt=media";

        ChatGroupArray chatGroupArray = new ChatGroupArray();
        final String ts = String.valueOf(chatGroupArray.imageTime);

        Glide.with(profilePic.getContext())
                .load(imgurl)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.initial_pic)
                //.signature(new ObjectKey(ts))
                //.apply(RequestOptions.circleCropTransform())
                .into(profilePic);

        userid.setText(group.getName());
        username.setText(group.getUserName());


        if (position == 0 && group.isAdmin() == true) {
            permission.setText("Owner");
            permission.setVisibility(View.VISIBLE);
            tripleDot.setVisibility(View.INVISIBLE);

        } else if (group.isAdmin() == true) {
            permission.setText("Admin");
            permission.setVisibility(View.VISIBLE);
        } else {
            permission.setVisibility(View.INVISIBLE);
        }
        if (isTrue == false) {
            tripleDot.setVisibility(View.INVISIBLE);
        }

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(params);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(group.getName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            try {
                                //  Block of code to try
                                DocumentSnapshot ds = task.getResult();
                                TextView dialogName = dialog.findViewById(R.id.name);
                                TextView dialogDesc = dialog.findViewById(R.id.desc);
                                ImageView dialogPic = dialog.findViewById(R.id.profilePic);
                                final ImageView dialogTripledot = dialog.findViewById(R.id.tripleDot);
                                TextView programName = dialog.findViewById(R.id.programName);
                                //TextView classOf = dialog.findViewById(R.id.classOf);
                                TextView classOfDesc = dialog.findViewById(R.id.classOfDesc);
                                RelativeLayout box = dialog.findViewById(R.id.box);

                                TextView program = dialog.findViewById(R.id.program);
                                TextView classOf = dialog.findViewById(R.id.classOf);

                                ImageView personalmessage = dialog.findViewById(R.id.personalmessage);
                                ImageView groupadd = dialog.findViewById(R.id.groupadd);
                                ImageView block = dialog.findViewById(R.id.block);

                                TextView msg_subtext = dialog.findViewById(R.id.msg_subtext);
                                TextView groupadd_subtext = dialog.findViewById(R.id.groupadd_subtext);
                                TextView block_subtext = dialog.findViewById(R.id.block_subtext);

                                TextView achive = dialog.findViewById(R.id.achive);
                                ImageView achiveDot = dialog.findViewById(R.id.dot);

                                dialogName.setText((group.getUserName()));
                                dialogDesc.setText((group.getName()));
                                programName.setText((String) ds.getString("programName"));

                                int classOfInt = Integer.valueOf(ds.getString("classOf")) + 4;
                                String classOfStr = String.valueOf(classOfInt);

                                classOfDesc.setText("Class of " + classOfStr);

                                Long points = ds.getLong("points");
                                Log.d("1234", "points " + points);
                                String sPoints = String.valueOf(points);

                                achive.setText(sPoints);

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
                                if (signedInUsername.equals(group.getName())) {
                                    dialogTripledot.setVisibility(View.GONE);
                                }
                                else {
                                    dialogTripledot.setVisibility(View.VISIBLE);
                                }
                                 */
                                if (isDark) {
                                    box.setBackgroundColor(context.getResources().getColor(R.color.addingpostgrey));
                                    dialogName.setTextColor(Color.WHITE);
                                    dialogDesc.setTextColor(Color.WHITE);
                                    programName.setTextColor(Color.WHITE);
                                    classOfDesc.setTextColor(Color.WHITE);
                                    //program.setTextColor(Color.WHITE);
                                    //classOf.setTextColor(Color.WHITE);

                                    msg_subtext.setTextColor(Color.WHITE);
                                    groupadd_subtext.setTextColor(Color.WHITE);
                                    block_subtext.setTextColor(Color.WHITE);

                                    personalmessage.setColorFilter(context.getResources().getColor(R.color.gnt_white));
                                    groupadd.setColorFilter(context.getResources().getColor(R.color.gnt_white));

                                } else {
                                    box.setBackgroundColor(context.getResources().getColor(R.color.gnt_white));
                                }



                                if(signedInUsername.equals(group.getName())){
                                    groupadd.setVisibility(View.INVISIBLE);
                                    block.setVisibility(View.INVISIBLE);
                                    personalmessage.setVisibility(View.INVISIBLE);

                                    msg_subtext.setVisibility(View.INVISIBLE);
                                    groupadd_subtext.setVisibility(View.INVISIBLE);
                                    block_subtext.setVisibility(View.INVISIBLE);
                                }else {
                                    groupadd.setVisibility(View.VISIBLE);
                                    block.setVisibility(View.VISIBLE);
                                    personalmessage.setVisibility(View.VISIBLE);

                                    msg_subtext.setVisibility(View.VISIBLE);
                                    groupadd_subtext.setVisibility(View.VISIBLE);
                                    block_subtext.setVisibility(View.VISIBLE);
                                }


                                groupadd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

                                        add_livechat_dialog adding_post = new add_livechat_dialog();
                                        assert ((AppCompatActivity) context).getFragmentManager() != null;

                                        Bundle arg = new Bundle();
                                        arg.putString("uid", group.getName());
                                        adding_post.setArguments(arg);

                                        adding_post.show(manager, adding_post.getTag());

                                    }
                                });

                                block.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        blockUser(signedInUsername, group.getName(), dialog);
                                    }
                                });

                                personalmessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {

                                        //String[] sortName = new String [] {menuItem.getId(), signedInUser};
                                        final ArrayList<String> sortName = new ArrayList<String>();
                                        sortName.add(group.getName());
                                        sortName.add(signedInUsername);

                                        Log.d("1234", "clicked user " + group.getName());

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
                                        chatGroups.document(combinedName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot dc = task.getResult();

                                                Log.d("1234", "document exist = " + dc.exists());

                                                if (!dc.exists()) {
                                                    DatabaseReference ref = RTDNotifications.push();
                                                    String postId = ref.getKey();
                                                    Map<String, Object> RTDN = new HashMap<>();

                                                    RTDN.put("gName", finalCombinedName);
                                                    RTDN.put("lastMessage", signedInUsername + " has joined the group");
                                                    RTDN.put("lastUser", signedInUsername);
                                                    RTDN.put("parentUser", signedInUsername);
                                                    RTDN.put("timestamp", dateNow);
                                                    RTDN.put("unseenMessage", 0);

                                                    RTDNotifications.child(postId).updateChildren(RTDN);

                                                    //add target user
                                                    DatabaseReference ref2 = RTDNotifications.push();
                                                    String postId2 = ref2.getKey();
                                                    Map<String, Object> RTDN2 = new HashMap<>();

                                                    RTDN2.put("gName", finalCombinedName);
                                                    RTDN2.put("lastMessage", group.getName() + " has joined the group");
                                                    RTDN2.put("lastUser", group.getName());
                                                    RTDN2.put("parentUser",group.getName());
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

                                                    Group newGroup = new Group(admins, members, name, ownerId, 0, docId, signedInUsername + " has created a dm", signedInUsername, dateNow, type);
                                                    chatGroupArray.setGroupSave(newGroup);

                                                    ArrayAdapter<Group> newAdapter = new groupAdapter(context, chatGroupArray.getGroupSave());
                                                    chatFragment.listView.setAdapter(newAdapter);

                                                    chatGroups.document(finalCombinedName).set(toAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            chatGroupArray.setBeginDM(true);

                                                            AppCompatActivity activity = (AppCompatActivity) unwrap(v.getContext());

                                                            int fPosition = 0;

                                                            dialog.dismiss();

                                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(chatGroupArray.getGroupSave().get(fPosition).getDocId(), chatGroupArray.getGroupSave().get(fPosition).getName(), signedInUsername,chatGroupArray.getGroupSave().get(fPosition).getType())).addToBackStack("chatFragment").commit();

                                                        }
                                                    });

                                                } else {

                                                    AppCompatActivity activity = (AppCompatActivity) unwrap(v.getContext());

                                                    dialog.dismiss();

                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(finalCombinedName, group.getName(), signedInUsername,"dm")).addToBackStack("chatFragment").commit();

                                                }
                                            }
                                        });


                                    }
                                });





                                String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/" + group.getName() + ".jpeg?alt=media";

                                Glide.with(dialogPic.getContext()).load(imgurl)
                                        .dontAnimate()
                                        //.apply(RequestOptions.circleCropTransform())
                                        //.signature(new ObjectKey(ts))
                                        .into(dialogPic);

                                dialog.show();
                            }
                            catch(Exception e) {
                                //  Block of code to handle errors
                                Toast.makeText(context,"This user has been deleted",Toast.LENGTH_SHORT).show();
                            }


                        }else {
                            Toast.makeText(context,"This user has been deleted",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        tripleDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);

                final PopupMenu popup = new PopupMenu(wrapper, tripleDot);
                Menu menu = popup.getMenu();
                popup.inflate(R.menu.menu_members_3dots);

                if (group.isAdmin == true) {
                    menu.getItem(0).setTitle("Demote");
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.promoteMember:
                                if (group.isAdmin == true) {
                                    DemoteUser(group.getName(), position, 1);
                                    LiveChatMembersFragment cmf = new LiveChatMembersFragment();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference chatGroups = db.collection("ChatGroups");
                                    final CollectionReference userRef = db.collection("Users");

                                    chatGroups
                                            .document(groupId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Boolean isTrue2 = isTrue;
                                            DocumentSnapshot dc = task.getResult();
                                            List<String> finale = new ArrayList<>();
                                            String owner = dc.getString("ownerId");
                                            List<String> members = (List<String>) dc.get("members");
                                            final List<String> admins = (List<String>) dc.get("admins");
                                            Collections.sort(members);
                                            Collections.sort(admins);

                                            final ArrayList<groupMembers> obj = new ArrayList<groupMembers>();

                                            if (owner != "") {
                                                obj.add(new groupMembers(true, owner, ""));
                                            }

                                            if (admins.size() != 0) {
                                                for (int t = 0; t < admins.size(); t++) {
                                                    if (signedInUsername.equals(admins.get(t))) {
                                                        isTrue = true;
                                                    }
                                                    if (!owner.equals(admins.get(t))) {
                                                        obj.add(new groupMembers(true, admins.get(t), ""));
                                                    }
                                                }
                                            }

                                            for (int t = 0; t < members.size(); t++) {
                                                if (!owner.equals(members.get(t))) {
                                                    if (!admins.contains(members.get(t))) {
                                                        //finale.add(members.get(t));
                                                        obj.add(new groupMembers(false, members.get(t), ""));
                                                    } else {
                                                    }
                                                }
                                            }

                                            final int[] count = {0};

                                            for(final groupMembers gr : obj) {
                                                userRef
                                                        .document(gr.getName())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            String name = document.getString("name");
                                                            gr.setUserName(name);

                                                            if (obj.size() - 1 == count[0]) {
                                                                LiveChatMembersFragment fragment = new LiveChatMembersFragment();
                                                                fragment.users.clear();
                                                                fragment.users = obj;
                                                                ArrayAdapter<groupMembers> saveAdapter = new groupMembersAdapter(getContext(), fragment.users, groupId, isTrue);
                                                                fragment.listView.setAdapter(saveAdapter);
                                                                fragment.listView.invalidateViews();
                                                            }

                                                            count[0]++;
                                                        } else {
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Promote(group.getName(), position, 0);
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference chatGroups = db.collection("ChatGroups");
                                    final CollectionReference userRef = db.collection("Users");

                                    chatGroups
                                            .document(groupId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot dc = task.getResult();
                                            List<String> finale = new ArrayList<>();
                                            String owner = dc.getString("ownerId");
                                            List<String> members = (List<String>) dc.get("members");
                                            final List<String> admins = (List<String>) dc.get("admins");

                                            Collections.sort(members);
                                            Collections.sort(admins);

                                            final ArrayList<groupMembers> obj = new ArrayList<groupMembers>();

                                            if (owner != "") {
                                                obj.add(new groupMembers(true, owner, ""));
                                            }


                                            if (admins.size() != 0) {
                                                for (int t = 0; t < admins.size(); t++) {
                                                    if (signedInUsername.equals(admins.get(t))) {
                                                        isTrue = true;
                                                    }
                                                    if (!owner.equals(admins.get(t))) {

                                                        obj.add(new groupMembers(true, admins.get(t), ""));
                                                    }
                                                }
                                            }

                                            for (int t = 0; t < members.size(); t++) {
                                                if (!owner.equals(members.get(t))) {
                                                    if (!admins.contains(members.get(t))) {
                                                        //finale.add(members.get(t));
                                                        obj.add(new groupMembers(false, members.get(t), ""));
                                                    } else {
                                                    }
                                                }
                                            }

                                            final int[] count = {0};

                                            for(final groupMembers gr : obj) {
                                                userRef
                                                        .document(gr.getName())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            String name = document.getString("name");
                                                            gr.setUserName(name);

                                                            if (obj.size() - 1 == count[0]) {
                                                                LiveChatMembersFragment fragment = new LiveChatMembersFragment();
                                                                fragment.users.clear();
                                                                fragment.users = obj;
                                                                ArrayAdapter<groupMembers> saveAdapter = new groupMembersAdapter(getContext(), fragment.users, groupId, isTrue);
                                                                fragment.listView.setAdapter(saveAdapter);
                                                                fragment.listView.invalidateViews();
                                                            }

                                                            count[0]++;
                                                        } else {
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                                break;
                            case R.id.removeMember:
                                RemoveUser(group.getName(), position, signedInUsername);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                CollectionReference chatGroups = db.collection("ChatGroups");
                                final CollectionReference userRef = db.collection("Users");

                                chatGroups
                                        .document(groupId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot dc = task.getResult();
                                        List<String> finale = new ArrayList<>();
                                        String owner = dc.getString("ownerId");
                                        List<String> members = (List<String>) dc.get("members");
                                        final List<String> admins = (List<String>) dc.get("admins");

                                        Collections.sort(members);
                                        Collections.sort(admins);

                                        final ArrayList<groupMembers> obj = new ArrayList<groupMembers>();

                                        if (owner != "") {
                                            obj.add(new groupMembers(true, owner, ""));
                                        }


                                        if (admins.size() != 0) {
                                            for (int t = 0; t < admins.size(); t++) {
                                                if (signedInUsername.equals(admins.get(t))) {
                                                    isTrue = true;
                                                }
                                                if (!owner.equals(admins.get(t))) {

                                                    obj.add(new groupMembers(true, admins.get(t), ""));
                                                }
                                            }
                                        }

                                        for (int t = 0; t < members.size(); t++) {
                                            if (!owner.equals(members.get(t))) {
                                                if (!admins.contains(members.get(t))) {
                                                    //finale.add(members.get(t));
                                                    obj.add(new groupMembers(false, members.get(t), ""));
                                                } else {
                                                }
                                            }
                                        }


                                        final int[] count = {0};

                                        for(final groupMembers gr : obj) {
                                            userRef
                                                    .document(gr.getName())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String name = document.getString("name");
                                                        gr.setUserName(name);

                                                        if (obj.size() - 1 == count[0]) {
                                                            LiveChatMembersFragment fragment = new LiveChatMembersFragment();
                                                            fragment.users.clear();
                                                            fragment.users = obj;
                                                            ArrayAdapter<groupMembers> saveAdapter = new groupMembersAdapter(getContext(), fragment.users, groupId, isTrue);
                                                            fragment.listView.setAdapter(saveAdapter);
                                                            fragment.listView.invalidateViews();
                                                        }

                                                        count[0]++;
                                                    } else {
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });


                        }
                        return false;
                    }
                });
                popup.show();

            }
        });
        return row;
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


    public void RemoveUser(final String username, int position, final String signedInUsername) {
        chatGroups.document(groupId).update(
                "admins", FieldValue.arrayRemove(username),
                "members", FieldValue.arrayRemove(username)
        );

        final ChatGroupArray chatGroupArray = new ChatGroupArray();
        chatGroupArray.isTransaction = true;

        LiveChatMembersFragment fragment = new LiveChatMembersFragment();
        fragment.users.remove(position);

        FirebaseDatabase.getInstance().getReference().child("ChatLogs").child(groupId).removeValue();

        Query q = FirebaseDatabase.getInstance().getReference().child("Notifications").orderByChild("parentUser").equalTo(username);

        q.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    for (final DataSnapshot i : task.getResult().getChildren()) {
                        if (groupId.equals((String) i.child("gName").getValue())){
                            i.getRef().removeValue();
                        }
                    }
                }else {
                    chatGroupArray.isTransaction = false;
                }
            }
        });

        this.notifyDataSetChanged();
    }

    public void Promote(String username, int position, int num) {

        chatGroups.document(groupId).update(
                "admins", FieldValue.arrayUnion(username)
        );

        LiveChatMembersFragment fragment = new LiveChatMembersFragment();

        this.notifyDataSetChanged();
    }

    public void DemoteUser(String username, int position, int num) {
        chatGroups.document(groupId).update(
                "admins", FieldValue.arrayRemove(username)
        );
        // list.remove(position);
        //list.add(1, new groupMembers(false, username, ""));

        LiveChatMembersFragment fragment = new LiveChatMembersFragment();
        //fragment.users.clear();

        //    final ChatGroupArray chatGroupArray = new ChatGroupArray();
        //  chatGroupArray.members = list;
        this.notifyDataSetChanged();
    }

    public void updateReceiptsList(ArrayList<groupMembers> newGroup) {
        list = newGroup;
        synchronized(this) {
            this.notifyAll();
        }
    }


}