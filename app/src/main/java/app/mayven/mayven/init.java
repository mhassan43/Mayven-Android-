package app.mayven.mayven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.bigkoo.pickerview.MyOptionsPickerView;
import app.mayven.mayven.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class init extends AppCompatActivity {
    private Button register;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView select_school, done, exit,login;
    private String selectedSchool;
    private String tx;
    private ArrayList<String> school = new ArrayList<String>();
    private ArrayList<String> documentIds = new ArrayList<String>();
    //private ArrayList<String> extension = new ArrayList<String>();
    private static ArrayList<ArrayList<String>> extension = new ArrayList<ArrayList<String>>();
    private int index;
    private String did;
    private ArrayList<String> ext;

    private List<String> orderedList = new ArrayList<String>();

    private String currentSchool = null;
    private static int selectedIndex = 0;

    private int num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        register = findViewById(R.id.register);
        done = findViewById(R.id.done);
        exit = findViewById(R.id.exit);

        login = findViewById(R.id.login);
        //textView = (TextView) findViewById(R.id.textView);
        select_school = findViewById(R.id.select_school);
        final Map<String, String> idandname = new HashMap<>();

        int nightModeFlags =
                getApplication().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;


        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                login.setTextColor(Color.GRAY);
                select_school.setTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:

                break;
        }


        db.collection("Schools")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String did = document.getId();
                                school.add(name);
                                documentIds.add(did);
                                extension.add((ArrayList<String>)document.get("emailExtension"));
                                //extension.add((ArrayList<String>) document.get("emailExtension"));
                            }

                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });


        select_school.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                disableAll();

                RelativeLayout s = findViewById(R.id.blackScreen);
                s.setVisibility(View.VISIBLE);
                RelativeLayout pickerScreen = findViewById(R.id.pickerView);
                s.setBackgroundColor(R.color.GRAY);
                com.shawnlin.numberpicker.NumberPicker numberPicker = (com.shawnlin.numberpicker.NumberPicker) findViewById(R.id.number_picker);
                pickerScreen.setVisibility(View.VISIBLE);
                numberPicker.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                final String[] stringArray = school.toArray(new String[0]); // 1
                Arrays.sort(stringArray);

                numberPicker.setMaxValue(school.size()); // 2
                numberPicker.setDisplayedValues(stringArray);

                orderedList = Arrays.asList(stringArray);

                if(currentSchool == null) {
                    currentSchool = (String) orderedList.get(0); // 3
                }
                Log.d("1234","s " + school);

                numberPicker.setScrollContainer(true);
                //    numberPicker.setWrapSelectorWheel(true);
                numberPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                numberPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                        int count = newVal-1;
                        selectedIndex = count;
                        currentSchool = (String) orderedList.get(selectedIndex);
                    }

                });

                numberPicker.setOnScrollListener(new com.shawnlin.numberpicker.NumberPicker.OnScrollListener() {
                    @Override
                    public void onScrollStateChange(com.shawnlin.numberpicker.NumberPicker picker, int scrollState) {

                    }
                });
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAll();
                RelativeLayout s = findViewById(R.id.blackScreen);
                RelativeLayout pickerScreen = findViewById(R.id.pickerView);
                s.setVisibility(View.INVISIBLE);

                pickerScreen.setVisibility(View.GONE);
                select_school.setText(currentSchool);

                num = 0;

                for (String school : school){
                    if(currentSchool.equals(school)){
                        selectedIndex = num;
                        break;
                    }
                    num++;
                }

                did = documentIds.get(selectedIndex);
                ext = extension.get(selectedIndex);

                Log.d("1234", "ext " + ext );
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAll();

                if(select_school.getText().toString().equals("")) {
                    currentSchool = null;
                }


                RelativeLayout s = findViewById(R.id.blackScreen);
                RelativeLayout pickerScreen = findViewById(R.id.pickerView);
                s.setVisibility(View.INVISIBLE);

                pickerScreen.setVisibility(View.GONE);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSchool != null) {
                    Intent intent = new Intent(getApplicationContext(), Register_First_Part.class);
                    intent.putExtra("schoolName", currentSchool);
                    intent.putExtra("schoolId", did);
                    intent.putExtra("extension", ext);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "You must select a school first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSchool != null) {
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    intent.putExtra("schoolName", currentSchool);
                    intent.putExtra("extension", ext);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "You must select a school first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

    }


    public void disableAll(){
        select_school.setClickable(false);
        register.setClickable(false);
        login.setClickable(false);
        select_school.setEnabled(false);
        register.setEnabled(false);
        login.setEnabled(false);
    }

    public void enableAll() {
        select_school.setClickable(true);
        register.setClickable(true);
        login.setClickable(true);
        select_school.setEnabled(true);
        register.setEnabled(true);
        login.setEnabled(true);
    }
}