package app.mayven.mayven;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class adapterListLiveChat extends ArrayAdapter<Chat> {

    Context context;
    Boolean isDark = false;
    View previousRow;
    private Dialog dialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");
    private CollectionReference chatGroupRef = db.collection("ChatGroups");
    private DatabaseReference RTDNotifications = FirebaseDatabase.getInstance().getReference("Notifications");


    public adapterListLiveChat(Context context, ArrayList<Chat> chat) {
        super(context, R.layout.livechatlayout, chat);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer previousIndex = 0;
        View row = convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.livechatlayout, null);
        }

        Chat previousChat = new Chat();
        Chat chat = getItem(position);
        TextView previousUser;
        TextView userName = (TextView) row.findViewById(R.id.userName);
        TextView message = (TextView) row.findViewById(R.id.textView);
        TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
        TextView firstTimestamp = (TextView) row.findViewById(R.id.firstTimestamp);
        TextView dateTextView = (TextView) row.findViewById(R.id.date);
        Long theTime = chat.getTimestamp();

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:

                userName.setTextColor(Color.WHITE);
                message.setTextColor(Color.WHITE);
                timestamp.setTextColor(Color.WHITE);
                firstTimestamp.setTextColor(Color.WHITE);
                dateTextView.setTextColor(Color.WHITE);
                isDark = true;

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        Date date = new java.util.Date(theTime * 1000L);
        String formattedTime = new SimpleDateFormat("hh:mm aa").format(date);
        String currDate = new SimpleDateFormat("dd MMMM yyyy").format(date);

        final ImageView profilePic = (ImageView) row.findViewById(R.id.profilePic);

        if (position - 1 > -1) {
            previousIndex = position - 1;
            previousChat = getItem(previousIndex);

            Date date2 = new java.util.Date(previousChat.getTimestamp() * 1000L);
            String previousDate = new SimpleDateFormat("dd MMMM yyyy").format(date2);

            if (!previousDate.equals(currDate)) {
                dateTextView.setText(currDate);

                dateTextView.setVisibility(View.VISIBLE);
                firstTimestamp.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
                profilePic.setVisibility(View.VISIBLE);
                timestamp.setVisibility(View.GONE);
                userName.setText(chat.getOwnerName());
                message.setText(chat.getLiveMessage());
                firstTimestamp.setText(formattedTime);

            } else {
                dateTextView.setVisibility(View.GONE);
                if (previousChat.getOwnerName().equals(chat.getOwnerName())) {
                    firstTimestamp.setVisibility(View.GONE);
                    userName.setVisibility(View.GONE);
                    profilePic.setVisibility(View.GONE);
                    timestamp.setVisibility(View.VISIBLE);
                    message.setText(chat.getLiveMessage());
                    timestamp.setText(formattedTime);
                    if (chat.getTimestamp() - previousChat.getTimestamp() < 60) {
                        timestamp.setVisibility(View.INVISIBLE);
                    }
                } else {
                    firstTimestamp.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.VISIBLE);
                    profilePic.setVisibility(View.VISIBLE);
                    timestamp.setVisibility(View.GONE);
                    userName.setText(chat.getOwnerName());
                    message.setText(chat.getLiveMessage());
                    firstTimestamp.setText(formattedTime);
                }
            }
        } else {
            userName.setVisibility(View.VISIBLE);
            profilePic.setVisibility(View.VISIBLE);
            timestamp.setVisibility(View.GONE);
            firstTimestamp.setVisibility(View.INVISIBLE);
            userName.setText(chat.getOwnerName());
            message.setText(chat.getLiveMessage());
            firstTimestamp.setText(formattedTime);
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imageRef = db.collection("Users");
        final DocumentReference imageUrl = imageRef.document(chat.getOwnerName());

        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Thumbnail%2F" + chat.getOwnerId() + ".jpeg?alt=media";

        ChatGroupArray chatGroupArray = new ChatGroupArray();
        String ts = String.valueOf(chatGroupArray.imageTime);

        Glide.with(profilePic.getContext()).load(imgUrl)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                //.apply(RequestOptions.circleCropTransform())
                //.signature(new ObjectKey(ts))
                .error(R.drawable.initial_pic)
                .into(profilePic);

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

                db.collection("Users").document(chat.getOwnerId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot ds = task.getResult();
                            RegisterUsername reg = new RegisterUsername();
                            List<userDB> qwe = reg.readData();

                            final String signedInUser = qwe.get(0).username;


                            //  Block of code to try

                            //  Block of code to try
                            TextView dialogName = dialog.findViewById(R.id.name);
                            TextView dialogDesc = dialog.findViewById(R.id.desc);

                            TextView achive = dialog.findViewById(R.id.achive);

                            ImageView dialogPic = dialog.findViewById(R.id.profilePic);
                            //final ImageView dialogTripledot = dialog.findViewById(R.id.tripleDot);
                            ImageView achiveDot = dialog.findViewById(R.id.dot);
                            TextView programName = dialog.findViewById(R.id.programName);
                            //TextView classOf = dialog.findViewById(R.id.classOf);
                            RelativeLayout box = dialog.findViewById(R.id.box);
                            TextView classOfDesc = dialog.findViewById(R.id.classOfDesc);

                            TextView program = dialog.findViewById(R.id.program);
                            TextView classOf = dialog.findViewById(R.id.classOf);

                            ImageView personalmessage = dialog.findViewById(R.id.personalmessage);
                            ImageView groupadd = dialog.findViewById(R.id.groupadd);
                            ImageView block = dialog.findViewById(R.id.block);

                            TextView msg_subtext = dialog.findViewById(R.id.msg_subtext);
                            TextView groupadd_subtext = dialog.findViewById(R.id.groupadd_subtext);
                            TextView block_subtext = dialog.findViewById(R.id.block_subtext);

                            dialogName.setText(chat.getOwnerId());
                            dialogDesc.setText(chat.getOwnerName());
                            programName.setText((String) ds.getString("programName"));

                            int classOfInt = Integer.valueOf(ds.getString("classOf")) + 4;
                            String classOfStr = String.valueOf(classOfInt);

                            classOfDesc.setText("Class of " + classOfStr);


                            Long points = ds.getLong("points");
                            Log.d("1234", "points " + points);
                            String sPoints = String.valueOf(points);

                            achive.setText(sPoints);

                            if (chat.getOwnerId().equals(signedInUser)) {
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

                            //achive.setText(Math.toIntExact(doc.getLong("points")));




                                        /*
                                        if (signedInUser.equals(menuItem.getId())) {
                                            dialogTripledot.setVisibility(View.GONE);
                                        } else {
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


                            block.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    blockUser(signedInUser, chat.getDocId(), dialog);
                                }
                            });

                            groupadd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

                                    add_livechat_dialog adding_post = new add_livechat_dialog();
                                    assert ((AppCompatActivity) context).getFragmentManager() != null;

                                    Bundle arg = new Bundle();
                                    arg.putString("uid", chat.getOwnerId());
                                    adding_post.setArguments(arg);

                                    adding_post.show(manager, adding_post.getTag());
                                }
                            });

                            personalmessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {

                                    //String[] sortName = new String [] {menuItem.getId(), signedInUser};
                                    final ArrayList<String> sortName = new ArrayList<String>();
                                    sortName.add(chat.getOwnerId());
                                    sortName.add(signedInUser);

                                    Log.d("1234", "clicked user " + chat.getOwnerId());

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
                                                RTDN2.put("lastMessage", chat.getOwnerId() + " has joined the group");
                                                RTDN2.put("lastUser", chat.getOwnerId());
                                                RTDN2.put("parentUser", chat.getOwnerId());
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

                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(finalCombinedName, chat.getOwnerId(), signedInUser,"dm")).addToBackStack("chatFragment").commit();

                                            }
                                        }
                                    });


                                }
                            });



                                        /*
                                        dialogTripledot.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
                                                final PopupMenu popup = new PopupMenu(wrapper, dialogTripledot);

                                                Menu menu = popup.getMenu();
                                                popup.inflate(R.menu.menu_block);

                                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                    @Override
                                                    public boolean onMenuItemClick(MenuItem item) {
                                                        switch (item.getItemId()) {
                                                            case R.id.blockUser:
                                                                blockUser(signedInUser, menuItem.getId(), dialog);
                                                                break;
                                                        }
                                                        return false;
                                                    }
                                                });
                                                popup.show();
                                            }
                                        });
                                         */


                            Glide.with(dialogPic.getContext()).load(imgUrl)
                                    .dontAnimate()
                                    //.apply(RequestOptions.circleCropTransform())
                                    //.signature(new ObjectKey(ts))
                                    .into(dialogPic);

                            dialog.show();


                        } else {
                            Toast.makeText(context, "This user has been deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        previousRow = row;
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private void blockUser(String signedInUser, String id, Dialog dialog) {
        userRef.document(signedInUser).update(
                "blockedUsers", FieldValue.arrayUnion(id)
        );
        ChatGroupArray.BlockedUsers.add(id);
        //mContext.startActivity(new Intent(mContext, MainActivity.class));
        dialog.dismiss();
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        assert context instanceof AppCompatActivity;
        return (AppCompatActivity) context;
    }

}