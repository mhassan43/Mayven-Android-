package app.mayven.mayven;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class add_livechat_dialog extends BottomSheetDialogFragment {

    ImageView backButton;
    ListView List;
    TextView emptyText;
    private String person;
    public static adapterLiveChatDialog adapter;

    private int Position = 0;


    private ArrayList<Group> filterGroup = new ArrayList<>();


    public add_livechat_dialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            person = bundle.getString("uid");
        }
    }
    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }
    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_livechat_dialog, container, false);


        emptyText = v.findViewById(R.id.emptyText);
        List = v.findViewById(R.id.List);
        backButton = v.findViewById(R.id.backButton);

        RegisterUsername reg = new RegisterUsername();
        java.util.List<userDB> qwe = reg.readData();
        final String signedInUsername = qwe.get(0).username;

        ChatGroupArray chatGroupArray = new ChatGroupArray();


        for (Group people: chatGroupArray.getGroupSave()){
            Boolean isThere = false;


            for (String members : people.getMembers()){

                if(members.equals(person)){
                    isThere = true;
                }
            }



            if(!isThere){
                Boolean isAdmin = false;
                if(people.getType().equals("group")) {
                    if(people.getOwnerId().equals(signedInUsername)) {
                        for (String admins : people.getAdmins()){
                            if (admins.equals(signedInUsername)){
                                isAdmin = true;
                            }
                        }
                        if(isAdmin){
                            filterGroup.add(chatGroupArray.getGroupSave().get(Position));
                        }
                    }
                }
            }

            Position++;
        }


        adapter = new adapterLiveChatDialog(getContext(),filterGroup ,person);

        List.setAdapter(adapter);

        return  v;

    }
}
