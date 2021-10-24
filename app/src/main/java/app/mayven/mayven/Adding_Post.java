package app.mayven.mayven;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import app.mayven.mayven.R;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

import com.bumptech.glide.Glide;

import com.freesoulapps.preview.android.Preview;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.OnlineState;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Adding_Post extends BottomSheetDialogFragment {

    public static Adding_Post newInstance() {
        return new Adding_Post();
    }

    private static final int SELECT_IMAGE = 1;
    private int STORAGE_PERMISSION_CODE = 1;
    private int LINK_IMAGE = 2;
    private static final int CAMERA_PIC_REQUEST = 1337;


    Button send, exit;

    private ImageView imageadd, phoneCamera, link, select_image, delete_image,previewImage;

    private String name;
    private String schoolId;
    private String whichProgram;

    private TextView programType, exitPicker, done, heading,
            imageadd_subtext,camera_subtext,link_subtext,previewTitle,previewDescription,previewURL;

    private List<String> repliesArray = new ArrayList<String>();

    private EditText post;

    private Uri uri = null;
    private Bitmap image = null;
    private Bitmap bitmap = null;

    private Boolean isDark = false;

    private RelativeLayout preview_wrapper,previewError;
    private ProgressBar progressBar;

    private MetaData mData;

    private static int selectedIndex = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postRef = db.collection("Posts");

    RegisterUsername reg = new RegisterUsername();
    List<userDB> qwe = reg.readData();
    final String signedInUser = qwe.get(0).name;
    final String signedInUsername = qwe.get(0).username;
    final String userProgram = qwe.get(0).programCode;

    List<String> programs = new ArrayList<String>();
    String currentProgram = null;
    String urlLink;

    Boolean onlyOne = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_adding_post, container, false);

        post = view.findViewById(R.id.input);
        send = view.findViewById(R.id.send);
        heading = view.findViewById(R.id.heading);
        programType = view.findViewById(R.id.programType);

        imageadd = view.findViewById(R.id.imageadd);
        phoneCamera = view.findViewById(R.id.phoneCamera);
        link = view.findViewById(R.id.link);

        delete_image = view.findViewById(R.id.delete_image);

        imageadd_subtext = view.findViewById(R.id.imageadd_subtext);
        camera_subtext = view.findViewById(R.id.camera_subtext);
        link_subtext = view.findViewById(R.id.link_subtext);

        select_image = view.findViewById(R.id.select_image);

        preview_wrapper = view.findViewById(R.id.preview_wrapper);
        progressBar = view.findViewById(R.id.progressBar);
        previewImage = view.findViewById(R.id.previewImage);
        previewTitle = view.findViewById(R.id.previewTitle);
        previewDescription = view.findViewById(R.id.previewDescription);
        previewURL = view.findViewById(R.id.previewURL);
        previewError = view.findViewById(R.id.previewError);


        ChatGroupArray chatGroupArray = new ChatGroupArray();

        if (currentProgram == null) {
            currentProgram = chatGroupArray.currentProgram;
            programType.setText(currentProgram);
        }

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        final int time = Integer.parseInt(timeStamp);


        exitPicker = view.findViewById(R.id.exitPicker);
        done = view.findViewById(R.id.done);

        exit = view.findViewById(R.id.exit);

        programs.add("All");
        programs.add(userProgram);

        programType.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                disableAll();

                RelativeLayout s = view.findViewById(R.id.blackScreen);
                s.setVisibility(View.VISIBLE);
                RelativeLayout pickerScreen = view.findViewById(R.id.pickerView);
                s.setBackgroundColor(R.color.GRAY);
                com.shawnlin.numberpicker.NumberPicker numberPicker = (com.shawnlin.numberpicker.NumberPicker) view.findViewById(R.id.number_picker);
                pickerScreen.setVisibility(View.VISIBLE);
                numberPicker.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                String[] stringArray = programs.toArray(new String[0]);
                numberPicker.setMaxValue(programs.size());
                numberPicker.setDisplayedValues(stringArray);


                currentProgram = (String) programs.get(0);


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
                        int count = newVal - 1;
                        selectedIndex = count;
                        currentProgram = programs.get(selectedIndex);
                    }
                });

                numberPicker.setOnScrollListener(new com.shawnlin.numberpicker.NumberPicker.OnScrollListener() {
                    @Override
                    public void onScrollStateChange(com.shawnlin.numberpicker.NumberPicker picker, int scrollState) {

                    }
                });
            }
        });

        int nightModeFlags =
                getActivity().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                post.setTextColor(Color.WHITE);
                heading.setTextColor(Color.WHITE);
                programType.setTextColor(Color.WHITE);

                imageadd.setColorFilter(getResources().getColor(R.color.gnt_white));
                phoneCamera.setColorFilter(getResources().getColor(R.color.gnt_white));
                link.setColorFilter(getResources().getColor(R.color.gnt_white));

                imageadd_subtext.setTextColor(Color.WHITE);
                camera_subtext.setTextColor(Color.WHITE);
                link_subtext.setTextColor(Color.WHITE);

                isDark = true;

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        exitPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (programType.getText().toString().equals("")) {
                    currentProgram = null;
                }
                RelativeLayout s = view.findViewById(R.id.blackScreen);
                RelativeLayout pickerScreen = view.findViewById(R.id.pickerView);
                s.setVisibility(View.INVISIBLE);

                pickerScreen.setVisibility(View.GONE);

                enableAll();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programType.setText(currentProgram);

                RelativeLayout s = view.findViewById(R.id.blackScreen);
                RelativeLayout pickerScreen = view.findViewById(R.id.pickerView);
                s.setVisibility(View.INVISIBLE);

                pickerScreen.setVisibility(View.GONE);

                enableAll();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String text = post.getText().toString();
                final String id = signedInUser;

                if (currentProgram != null) {
                    final DocumentReference docRef = db.collection("Users").document(signedInUsername);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (!text.matches("")) {
                                    name = document.getString("name");
                                    schoolId = document.getString("school");
                                    repliesArray.add(signedInUsername);
                                    Map<String, Object> toAdd = new HashMap<>();
                                    toAdd.put("lastAction", "post");
                                    toAdd.put("lastActionTime", time);
                                    toAdd.put("likes", 0);
                                    toAdd.put("ownerId", signedInUsername);
                                    toAdd.put("ownerName", name);
                                    toAdd.put("programCode", programType.getText().toString());
                                    toAdd.put("replies", repliesArray);
                                    toAdd.put("schoolId", schoolId);
                                    toAdd.put("text", text);
                                    toAdd.put("timestamp", time);
                                    toAdd.put("usersLiked", Collections.emptyList());
                                    toAdd.put("reports", Collections.emptyList());
                                    toAdd.put("replyCount", 0);

                                    //temp values
                                    //toAdd.put("type", "text");
                                    toAdd.put("color", "common");

                                    if (uri != null) {
                                        toAdd.put("type", "image");
                                    } else if (urlLink != null) {
                                        toAdd.put("link", urlLink);
                                        toAdd.put("type", "link");
                                    } else if (image != null) {
                                        toAdd.put("type", "image");
                                    } else {
                                        toAdd.put("type", "text");

                                    }


                                    if(signedInUsername.equals("kevinchan") || signedInUsername.equals("mahadhassan1")){
                                        toAdd.put("color","mayven");
                                    }else {
                                        int points = chatGroupArray.getPoints();
                                        Log.d("1234","points = " + points);
                                        if (points < 1000) {
                                            //common
                                            toAdd.put("color","common");
                                        } else if (points > 1000 && points < 10000) {
                                            //bronze
                                            toAdd.put("color","bronze");
                                        } else if (points > 10000 && points < 50000) {
                                            //gold
                                            toAdd.put("color","gold");
                                        } else if (points > 50000 && points < 100000) {
                                            //emerald
                                            toAdd.put("color","emerald");
                                        } else if (points > 100000) {
                                            //ruby
                                            toAdd.put("color","ruby");
                                        }
                                    }

                                    docRef.update(
                                            "points", FieldValue.increment(50)
                                    );

                                    postRef.add(toAdd).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            DocumentReference doc = task.getResult();
                                            if (task.isSuccessful()) {
                                                if (uri != null) {
                                                    //image
                                                    //String furi = uri.toString();


                                                    Log.d("1234", "uri " + uri);

                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
                                                    byte[] compressed = baos.toByteArray();


                                                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                                    //Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 125, 125, true);

                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 55, baos2);
                                                    byte[] thumb = baos2.toByteArray();


                                                    final StorageReference img = FirebaseStorage.getInstance().getReference().child("Post/").child(doc.getId() + ".jpeg");
                                                    final StorageReference thumbNail = FirebaseStorage.getInstance().getReference().child("Post/").child("Thumbnail/").child(doc.getId() + ".jpeg");

                                                    img.putBytes(compressed);
                                                    thumbNail.putBytes(thumb);

                                                    toAdd.put("type", "image");

                                                } else if (image != null) {
                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    image.compress(Bitmap.CompressFormat.JPEG, 95, baos);
                                                    byte[] compressed = baos.toByteArray();

                                                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

                                                    image.compress(Bitmap.CompressFormat.JPEG, 55, baos2);
                                                    byte[] thumb = baos2.toByteArray();


                                                    final StorageReference img = FirebaseStorage.getInstance().getReference().child("Post/").child(doc.getId() + ".jpeg");
                                                    final StorageReference thumbNail = FirebaseStorage.getInstance().getReference().child("Post/").child("Thumbnail/").child(doc.getId() + ".jpeg");

                                                    img.putBytes(compressed);
                                                    thumbNail.putBytes(thumb);

                                                    toAdd.put("type", "image");

                                                }
                                            }
                                        }
                                    });
                                    FirebaseMessaging.getInstance().subscribeToTopic(postRef.getId()) // PUT THIS INSIDE THE FOR LOOP OF THE RETURN FIRST DOCS && INIT NOTIFICATIONS
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.e("Error", "Cannot get notification");
                                                    } else {
                                                        ChatGroupArray chatGroupArray = new ChatGroupArray();
                                                        chatGroupArray.cloudNotifications.add(postRef.getId());
                                                    }
                                                }
                                            });

                                    ((MainActivity) getActivity()).setBlackScreen();
                                    ((MainActivity) getActivity()).clearItems();
                                    ChatGroupArray chatGroupArray = new ChatGroupArray();
                                    ((MainActivity) getActivity()).addItemsFromFirebase(chatGroupArray.currentType, chatGroupArray.currentProgram);
                                    dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Please input some text", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Please select a program", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
                } else {
                    requeststorangepermission();
                }
            }
        });

        phoneCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });

        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage();
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select_image.setVisibility(View.VISIBLE);
                //delete_image.setVisibility(View.VISIBLE);
                //rich_image.setVisibility(View.VISIBLE);

                if (onlyOne) {
                    removeImage();
                } else {
                    onlyOne = true;
                }

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Link");


                TextInputLayout textInputLayout = new TextInputLayout(getContext());

                final EditText input = new EditText(getContext());
                input.setSingleLine(true);
                input.setHint("Paste link here");

                if(isDark){
                    alert.setTitle(Html.fromHtml("<font color='#FFFFFF'>Link</font>"));
                    input.setTextColor(getContext().getResources().getColor(R.color.gnt_white));
                }

                FrameLayout container = new FrameLayout(getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                int left_margin = dpToPx(20, getContext().getResources());
                int top_margin = dpToPx(10, getContext().getResources());
                int right_margin = dpToPx(20, getContext().getResources());
                int bottom_margin = dpToPx(4, getContext().getResources());
                params.setMargins(left_margin, top_margin, right_margin, bottom_margin);

                //textInputLayout.setLayoutParams(params);
                input.setLayoutParams(params);

                //textInputLayout.addView(input);
                container.addView(input);


                alert.setView(container);


                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //select_image.setVisibility(View.VISIBLE);

                        //preview.setVisibility(View.VISIBLE);
                        preview_wrapper.setVisibility(View.VISIBLE);
                        delete_image.setVisibility(View.VISIBLE);

                        urlLink = input.getText().toString();

                        String temp = "";
                        for (int i = 0; i < urlLink.length(); i++) {
                            char c = urlLink.charAt(i);
                            if (i >= 3) {
                                if (!temp.equals("htt")) {
                                    urlLink = "https://" + urlLink;
                                }
                                break;
                            } else {
                                temp += c;
                            }
                        }

                        LoadURL(urlLink);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

            }
        });

        return view;
    }

    private void LoadURL(String urlLink) {
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                mData = metaData;

                Glide.with(previewImage.getContext()).load(mData.getImageurl())
                        .dontAnimate()
                        .placeholder(R.drawable.notfound)
                        //.signature(new ObjectKey(ts))
                        //  .error(R.drawable.ic_person_fill)
                        .into(previewImage);


                previewTitle.setText(mData.getTitle());
                previewDescription.setText(mData.getDescription());
                previewURL.setText(mData.getUrl());

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        preview_wrapper.setVisibility(View.GONE);

                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.link_error_layout, null);

                        TextView url = view.findViewById(R.id.message);
                        url.setText(urlLink);

                        if (isDark) {
                            url.setBackgroundColor(getContext().getResources().getColor(R.color.bottomnavgrey));
                        }

                        url.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(urlLink));
                                getContext().startActivity(i);
                            }
                        });

                        previewError.addView(view);

                    }
                });
            }
        });

        richPreview.getPreview(urlLink);

    }

    private int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    private static void print(String msg, Object... args) {
        Log.d("1234", String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    private void requeststorangepermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Confirm");
            builder.setMessage("Enable image permissions to change your image");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (onlyOne) {
                        removeImage();
                    } else {
                        onlyOne = true;
                    }
                    select_image.setVisibility(View.VISIBLE);
                    delete_image.setVisibility(View.VISIBLE);

                    uri = data.getData();


                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Glide.with(select_image.getContext()).load(uri)
                            .dontAnimate()
                            .error(R.drawable.placeholder)
                            .into(select_image);
                }
            }
        }
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                if (onlyOne) {
                    removeImage();
                } else {
                    onlyOne = true;
                }
                Log.d("1234","only one " + onlyOne);

                select_image.setVisibility(View.VISIBLE);
                delete_image.setVisibility(View.VISIBLE);

                image = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);

                Glide.with(select_image.getContext())
                        .load(stream.toByteArray())
                        .asBitmap()
                        .error(R.drawable.placeholder)
                        .into(select_image);

            }
        }
    }

    private void removeImage() {
        //preview.setVisibility(View.GONE);
        preview_wrapper.setVisibility(View.GONE);
        previewError.setVisibility(View.GONE);
        select_image.setVisibility(View.GONE);
        delete_image.setVisibility(View.GONE);
        uri = null;
        onlyOne = false;
        image = null;
        urlLink = "";
    }


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");

            return d;
        } catch (Exception e) {

            return null;
        }
    }


    public void disableAll() {
        exit.setClickable(false);
        programType.setClickable(false);
        send.setClickable(false);
        post.setClickable(false);
    }

    public void enableAll() {
        exit.setClickable(true);
        programType.setClickable(true);
        send.setClickable(true);
        post.setClickable(true);
    }

}
