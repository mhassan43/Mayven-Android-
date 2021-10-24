package app.mayven.mayven;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class adapterLiveChatDialog extends ArrayAdapter<Group> {

    Context mContext;
    String person;
    private ArrayList<Group> groups;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");

    private CollectionReference chatboxRef = db.collection("ChatGroups");
    private DatabaseReference RTDNotifications = FirebaseDatabase.getInstance().getReference("Notifications");


    public adapterLiveChatDialog(@NonNull Context context, ArrayList<Group> groups, String person) {
        super(context,R.layout.add_livechat_dialog,groups);
        this.person = person;
        mContext = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            row = layoutInflater.inflate(R.layout.list_add_group_dialog, null);
        }

        ChatGroupArray chatGroupArray = new ChatGroupArray();

        Boolean clicked = false;
        Boolean permission = false;


        RegisterUsername reg = new RegisterUsername();
        List<userDB> qwe = reg.readData();
        final String school = qwe.get(0).school;
        final String signedInUsername = qwe.get(0).username;

        final Group group = groups.get(position);

        String imgurl = null;

        TextView groupName = row.findViewById(R.id.groupName);
        ImageView profilePic = row.findViewById(R.id.profilePic);
        final ImageView add = row.findViewById(R.id.add);


        if (group.getType().equals("dm")){

            for (String people : group.getMembers()){
                if(!signedInUsername.equals(people)){
                    groupName.setText(people);
                    imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Thumbnail%2F" + people + ".jpeg?alt=media";
                }
            }

            //groups.remove(position);

        }else {
            groupName.setText(group.getName());
            imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/ChatGroups%2F" + group.getDocId() + ".jpeg?alt=media";
        }

        for (String admin : group.getAdmins()){
            if(admin.equals(signedInUsername)){
                permission = true;
            }else if (group.getOwnerId().equals(admin)){
                permission = true;
            }
        }



        String ts = String.valueOf(chatGroupArray.imageTime);

        Glide.with(profilePic.getContext()).load(imgurl)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                //.signature(new ObjectKey(ts))
                //  .error(R.drawable.ic_person_fill)
                .into(profilePic);


        final Boolean finalPermission = permission;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                final Long dateNow = Long.parseLong(timeStamp);



                //change to green check mark

                // add to group
                if(finalPermission == true) {
                    add.setImageResource(R.drawable.ic_check);
                    add.setColorFilter(Color.GREEN);

                    chatboxRef.document(group.getDocId()).update(
                            "members", FieldValue.arrayUnion(person)
                    );

                    DatabaseReference ref = RTDNotifications.push();
                    String postId = ref.getKey();
                    Map<String, Object> RTDN = new HashMap<>();

                    RTDN.put("gName", group.getDocId());
                    RTDN.put("lastMessage", person + " has joined the group");
                    RTDN.put("lastUser", person);
                    RTDN.put("parentUser", person);
                    RTDN.put("timestamp", dateNow);
                    RTDN.put("unseenMessage", 0);

                    RTDNotifications.child(postId).updateChildren(RTDN);
                }else {
                    Toast.makeText(mContext, "Only admins and owners can add",Toast.LENGTH_SHORT).show();
                }

            }
        });

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                groupName.setTextColor(mContext.getResources().getColor(R.color.gnt_white));
                add.setColorFilter(mContext.getResources().getColor(R.color.gnt_white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }


        return row;
    }
}
