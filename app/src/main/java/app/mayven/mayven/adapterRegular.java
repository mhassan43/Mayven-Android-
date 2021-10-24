package app.mayven.mayven;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.BoringLayout;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import com.freesoulapps.preview.android.Preview;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class adapterRegular extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Preview.PreviewListener {

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    private static final int TYPE_LOADING = 2;
    private Boolean isLoader = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference postRef = db.collection("Posts");
    private CollectionReference userRef = db.collection("Users");
    private CollectionReference chatGroupRef = db.collection("ChatGroups");

    private DatabaseReference RTDNotifications = FirebaseDatabase.getInstance().getReference("Notifications");

    private String regexURL = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";


    private Dialog dialog;
    private RelativeLayout mRelative;

    // An Activity's Context.
    private final Context mContext;

    private Context context;
    public static List<Object> noteList;

    private Boolean isDark = false;

    private MetaData mData;

    private String igu;


    public void setData(List<Note> note) {
        this.noteList.addAll(note);
        notifyDataSetChanged();
    }

    public void addItem(List<Object> note) {
        noteList.addAll(note);
        notifyDataSetChanged();
    }

    public void setAds(List<UnifiedNativeAd> ads) {
        this.noteList.addAll(ads);
    }

    public adapterRegular(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.noteList = recyclerViewItems;
    }

    @Override
    public void onDataReady(Preview preview) {
        preview.setMessage(preview.getLink());
    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView post;
        private TextView REPLY;
        private TextView likes;
        private TextView time;
        private ImageView heart;
        private de.hdodenhof.circleimageview.CircleImageView profilePic;
        private ImageView tripleDot;

        private ImageView imagePost;
        private RelativeLayout linkorimage;
        private Preview preview;

        private Boolean test = false;


        //private RichLinkView richView;

        private RelativeLayout preview_wrapper;
        private RelativeLayout previewError;
        private ImageView previewImage;
        private TextView previewTitle;

        private TextView previewDescription;
        private TextView previewURL;


        //private ImageView img_preview;
        //private TextView link_title;
        //private TextView link_body;
        //private LinearLayout link_layout_preview;

        private ProgressBar progressBar;


        MenuItemViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            post = (TextView) view.findViewById(R.id.post);
            REPLY = (TextView) view.findViewById(R.id.REPLY);
            likes = (TextView) view.findViewById(R.id.likes);
            time = (TextView) view.findViewById(R.id.time);
            profilePic = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.profilePic);
            tripleDot = (ImageView) view.findViewById(R.id.tripleDot);
            heart = (ImageView) view.findViewById(R.id.heart);
            imagePost = (ImageView) view.findViewById(R.id.imagePost);

            linkorimage = (RelativeLayout) view.findViewById(R.id.linkorimage);

            //img_preview = (ImageView) view.findViewById(R.id.img_preview);
            //link_title = (TextView) view.findViewById(R.id.link_title);
            //link_body = (TextView) view.findViewById(R.id.link_body);
            //link_layout_preview = (LinearLayout) view.findViewById(R.id.link_layout_preview);

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            preview_wrapper = (RelativeLayout) view.findViewById(R.id.preview_wrapper);

            previewError = (RelativeLayout) view.findViewById(R.id.previewError);

            previewImage = (ImageView) view.findViewById(R.id.previewImage);
            previewTitle = (TextView) view.findViewById(R.id.previewTitle);

            previewDescription = (TextView) view.findViewById(R.id.previewDescription);
            previewURL = (TextView) view.findViewById(R.id.previewURL);


        }

    }
    /*
    public class LoadingBar  extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        public LoadingBar(@NonNull View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

     */

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RegisterUsername reg = new RegisterUsername();
        List<userDB> qwe = reg.readData();

        final String signedInUser = qwe.get(0).username;
        final String classOf = qwe.get(0).classOf;

        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) noteList.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                final MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                final Note menuItem = (Note) noteList.get(position);
                Boolean isCommon = false;


                int nightModeFlags =
                        mContext.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;

                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:

                        String color = menuItem.getColor();

                        menuItemHolder.post.setTextColor(Color.WHITE);
                        isDark = true;
                        //menuItemHolder.preview.setBackgroundColor(mContext.getResources().getColor(R.color.bottomnavgrey));
                        menuItemHolder.preview_wrapper.setBackgroundColor(mContext.getResources().getColor(R.color.bottomnavgrey));

                        if (color.equals("common")) {
                            isCommon = true;
                        }

                        menuItemHolder.previewDescription.setTextColor(mContext.getResources().getColor(R.color.gnt_white));

                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        break;
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        break;
                }


                // Get the menu item image resource ID.
                //  String imageName = menuItem.getImageName();
                // int imageResID = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
                // Add the menu item details to the menu item view.
                //     menuItemHolder.menuItemImage.setImageResource(imageResID);

                //TODO: achivements
                /*
                userRef.document(signedInUser).collection("Achievements").document(signedInUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot dc = task.getResult();
                    }
                });

                 */


                final String numberOfLikes = String.valueOf(menuItem.getLikes());

                boolean flag = false;
                List<String> test = menuItem.getUsersLiked();
                for (int y = 0; y < test.size(); y++) {
                    if (test.get(y).equals(signedInUser)) {
                        flag = true;
                        break;
                    }
                }
                if (flag == true) {
                    menuItemHolder.heart.setImageResource(R.drawable.ic_heart_fill);
                    menuItemHolder.heart.setTag(2);
                } else {
                    menuItemHolder.heart.setImageResource(R.drawable.ic_heart);
                    menuItemHolder.heart.setTag(1);
                }

                final String documentId = menuItem.getDocumentId();

                menuItemHolder.heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                        final int dateNow = Integer.parseInt(timeStamp);
                        Map<String, Object> toAdd = new HashMap<>();
                        int num = (int) menuItemHolder.heart.getTag();
                        List<String> test = menuItem.getUsersLiked();
                        if (num == 1) {
                            menuItemHolder.heart.setImageResource(R.drawable.ic_heart_fill);
                            test.add(signedInUser);
                            toAdd.put("usersLiked", test);
                            db.collection("Posts").document(documentId)
                                    .update(
                                            "usersLiked", test,
                                            "likes", FieldValue.increment(1),
                                            "lastAction", "likes",
                                            "lastActionTime", dateNow
                                    );
                            //points
                            db.collection("Users").document(signedInUser).update(
                                    "points", FieldValue.increment(1)
                            );

                            db.collection("Users").document(menuItem.getId()).update(
                                    "points", FieldValue.increment(5)
                            );

                            String likes = String.valueOf(menuItem.getLikes() + 1);
                            menuItemHolder.likes.setText(likes);
                            menuItem.likes += 1;
                            menuItemHolder.heart.setTag(2);
                        } else {
                            menuItemHolder.heart.setImageResource(R.drawable.ic_heart);
                            menuItemHolder.heart.setTag(1);
                            test.remove(signedInUser);
                            toAdd.put("usersLiked", test);
                            db.collection("Posts").document(documentId)
                                    .update(
                                            "likes", FieldValue.increment(-1)
                                            , "usersLiked", test
                                    );

                            db.collection("Users").document(signedInUser).update(
                                    "points", FieldValue.increment(-1)
                            );

                            db.collection("Users").document(menuItem.getId()).update(
                                    "points", FieldValue.increment(-5)
                            );

                            String likes = String.valueOf(menuItem.getLikes() - 1);
                            menuItemHolder.likes.setText(likes);
                            menuItem.likes -= 1;
                        }
                    }
                });


                menuItemHolder.userName.setText(menuItem.getOwnerName());

                String color = menuItem.getColor();

                switch (color) {
                    case "common":
                        //leave default black
                        if (isCommon) {
                            menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.gnt_white));
                        } else {
                            menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.black));
                        }
                        break;
                    case "bronze":
                        menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.brown));
                        break;
                    case "emerald":
                        menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.emerald));
                        break;
                    case "ruby":
                        menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.ruby));
                        break;
                    case "mayven":
                        menuItemHolder.userName.setTextColor(mContext.getResources().getColor(R.color.mayvenBlue));
                        break;

                }

                //menuItemHolder.REPLY.setText(menuItem.getReplyCount() + " replies");
                if (menuItem.getReplyCount() == 1) {
                    menuItemHolder.REPLY.setText(menuItem.getReplyCount() + " reply");
                } else {
                    menuItemHolder.REPLY.setText(menuItem.getReplyCount() + " replies");
                }

                menuItemHolder.time.setText(menuItem.getTime());
                menuItemHolder.post.setText(menuItem.getPost());
                menuItemHolder.likes.setText(numberOfLikes);

                String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Thumbnail%2F" + menuItem.getId() + ".jpeg?alt=media";

                final ChatGroupArray chatGroupArray = new ChatGroupArray();
                final String ts = String.valueOf(chatGroupArray.imageTime);


                Glide.with(menuItemHolder.profilePic.getContext()).load(imgurl)
                        .dontAnimate()
                        .placeholder(R.drawable.placeholder)
                        //.signature(new ObjectKey(ts))
                        //  .error(R.drawable.ic_person_fill)
                        .into(menuItemHolder.profilePic);


                if (menuItem.getType().equals("image")) {
                    //menuItemHolder.link_layout_preview.setVisibility(View.GONE);


                    menuItemHolder.linkorimage.setVisibility(View.VISIBLE);

                    menuItemHolder.imagePost.setVisibility(View.VISIBLE);
                    //menuItemHolder.preview.setVisibility(View.GONE);

                    menuItemHolder.preview_wrapper.setVisibility(View.GONE);


                    igu = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Post%2F" + menuItem.getDocumentId() + ".jpeg?alt=media";

                    Glide.with(menuItemHolder.imagePost.getContext()).load(igu)
                            .dontAnimate()
                            .placeholder(R.drawable.placeholder)
                            //.signature(new ObjectKey(ts))
                            //  .error(R.drawable.ic_person_fill)
                            .into(menuItemHolder.imagePost);


                    //menuItemHolder.progressBar.setVisibility(View.GONE);
                } else if (menuItem.getType().equals("link")) {
                    //link
                    menuItemHolder.linkorimage.setVisibility(View.VISIBLE);
                    menuItemHolder.preview_wrapper.setVisibility(View.VISIBLE);
                    menuItemHolder.progressBar.setVisibility(View.VISIBLE);

                    menuItemHolder.imagePost.setVisibility(View.GONE);

                    //menuItemHolder.preview.setVisibility(View.VISIBLE);
                    //menuItemHolder.link_layout_preview.setVisibility(View.VISIBLE);

/*
                    if (chatGroupArray.getProgressShown()) {
                        menuItemHolder.progressBar.setVisibility(View.GONE);
                    } else {
                        menuItemHolder.progressBar.setVisibility(View.VISIBLE);

                    }
*/

                    String link = menuItem.getLink();
                    String temp = "";

                    for (int i = 0; i < menuItem.getLink().length(); i++) {
                        char c = menuItem.getLink().charAt(i);
                        if (i >= 3) {
                            if (!temp.equals("htt")) {
                                link = "https://" + menuItem.getLink();
                            }
                            break;
                        } else {
                            temp += c;
                        }
                    }


                    // menuItemHolder.preview.setListener(this);

                    if (link != null && !link.equals("")) {
                        //menuItemHolder.preview.setData(link);

                        //menuItemHolder.richView.setVisibility(View.VISIBLE);

                        String finalLink = link;
                        RichPreview richPreview = new RichPreview(new ResponseListener() {
                            @Override
                            public void onData(MetaData metaData) {
                                mData = metaData;

                                Glide.with(menuItemHolder.previewImage.getContext()).load(mData.getImageurl())
                                        .dontAnimate()
                                        .placeholder(R.drawable.notfound)
                                        //.signature(new ObjectKey(ts))
                                        //  .error(R.drawable.ic_person_fill)
                                        .into(menuItemHolder.previewImage);

                                menuItemHolder.test = true;
                                Log.d("1234", "working  " + menuItemHolder.test);


                                menuItemHolder.previewTitle.setText(mData.getTitle());
                                menuItemHolder.previewDescription.setText(mData.getDescription());
                                menuItemHolder.previewURL.setText(mData.getUrl());

                                menuItemHolder.preview_wrapper.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(finalLink));
                                        mContext.startActivity(i);
                                    }
                                });

                                menuItemHolder.progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError(Exception e) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        menuItemHolder.preview_wrapper.setVisibility(View.GONE);

                                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.link_error_layout, null);

                                        TextView url = view.findViewById(R.id.message);
                                        url.setText(finalLink);

                                        if (isDark) {
                                            url.setBackgroundColor(mContext.getResources().getColor(R.color.bottomnavgrey));
                                        }

                                        url.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(finalLink));
                                                mContext.startActivity(i);
                                            }
                                        });

                                        menuItemHolder.previewError.addView(view);

                                        menuItemHolder.previewError.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(finalLink));
                                                mContext.startActivity(i);
                                            }
                                        });

                                    }
                                });
                            }
                        });

                        richPreview.getPreview(link);


                        /*
                        menuItemHolder.richView.setLink(link, new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                         */
                    }


                    /*
                    menuItemHolder.preview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(finalLink));
                            mContext.startActivity(i);
                        }
                    });

                     */

                    /*
                    String finalLink = link;
                    Utils.getJsoupContent(link)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                        if (result != null) {
                                            Elements metaTags = (Elements) result.getElementsByTag("meta");
                                            for (Element element : metaTags) {

                                                if (element.attr("property").equals("og:image")) {
                                                    /*
                                                    Picasso.with(mContext)
                                                            .load(element.attr("content"))
                                                            .into(menuItemHolder.img_preview);


                                                    Glide.with(menuItemHolder.img_preview.getContext()).load(element.attr("content"))
                                                            .dontAnimate()
                                                            .placeholder(R.drawable.placeholder)
                                                            .signature(new ObjectKey(ts))
                                                            //  .error(R.drawable.ic_person_fill)
                                                            /*
                                                            .listener(new RequestListener<Drawable>() {
                                                                          @Override
                                                                          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                              return false;
                                                                          }

                                                                          @Override
                                                                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                              return false;
                                                                          }
                                                                      }
                                                            )

                                                            .into(menuItemHolder.img_preview);

                                                } else if (element.attr("name").equals("title")) {
                                                    menuItemHolder.link_title.setText(element.attr("content"));
                                                } else if (element.attr("property").equals("og:url")) {
                                                    menuItemHolder.link_body.setText(element.attr("content"));
                                                    menuItemHolder.link_layout_preview.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String internet = element.attr("content");
                                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                                            i.setData(Uri.parse(internet));
                                                            mContext.startActivity(i);
                                                        }
                                                    });
                                                }

                                            }


                                            menuItemHolder.progressBar.setVisibility(View.GONE);
                                            menuItemHolder.link_layout_preview.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.d("1234", "link result null");
                                            Toast.makeText(mContext, "Error loading link preview", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    error -> {
                                        Log.d("1234", "error getting link preview");

                                        menuItemHolder.link_title.setText("Error getting title");
                                        menuItemHolder.link_body.setText(finalLink);


                                        menuItemHolder.progressBar.setVisibility(View.GONE);
                                        menuItemHolder.link_layout_preview.setVisibility(View.VISIBLE);
                                    });
*/


                } else {
                    //text
                    menuItemHolder.linkorimage.setVisibility(View.GONE);
                    menuItemHolder.imagePost.setVisibility(View.GONE);
                    // menuItemHolder.preview.setVisibility(View.GONE);

                    menuItemHolder.preview_wrapper.setVisibility(View.GONE);


                }


                menuItemHolder.imagePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new FullScreenImage(menuItem.getDocumentId())).addToBackStack(null).commit();
                    }
                });


                menuItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //whatever you want here
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeReplies(menuItem.getReplyCount()
                                , menuItem.getPost(), menuItem.getOwnerName(), menuItem.getImageURL(), menuItem.getDocumentId(), menuItem.getId(), menuItem.getTime2(), menuItem.getUsersLiked(), menuItem.getLikes(), position, menuItem.getreplies(), menuItem.getReports(), menuItem.getLink(), menuItem.getColor(), menuItem.getType())).addToBackStack(null).commit();
                    }
                });

                menuItemHolder.tripleDot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);

                        final PopupMenu popup = new PopupMenu(wrapper, menuItemHolder.tripleDot);
                        Menu menu = popup.getMenu();
                        popup.inflate(R.menu.menu_remove_report);

                        if (signedInUser.equals(menuItem.getId())) {
                            menu.findItem(R.id.report).setVisible(false);

                        } else {
                            menu.findItem(R.id.remove).setVisible(false);
                        }


                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.remove:
                                        removePost(mContext, menuItem.getDocumentId(), position, menuItem.getId(), menuItem.getType());
                                        break;
                                    case R.id.report:
                                        /*
                                        if (menuItem.getReports().size() + 1 >= 5) { // report and delete
                                            reportAndDeletePost(menuItem.getId(), mContext, menuItem.getDocumentId(), menuItem.getPost());

                                            //noteList.remove(position);
                                            //HomeFragment.adapter.notifyDataSetChanged();
                                        } else {
                                            reportPost(menuItem.getId(), mContext, menuItem.getDocumentId());
                                        }
                                        */
                                        reportPost(menuItem.getId(), mContext, menuItem.getDocumentId(), menuItem.getPost(), menuItem.getId(), signedInUser);


                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });

                dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_image);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = ActionBar.LayoutParams.MATCH_PARENT;
                params.gravity = Gravity.BOTTOM;

                dialog.getWindow().setAttributes(params);

                menuItemHolder.profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        db.collection("Users").document(menuItem.ownerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot ds = task.getResult();

                                    try {
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

                                        dialogName.setText(menuItem.getOwnerName());
                                        dialogDesc.setText(menuItem.ownerId);
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
                                            achive.setTextColor(mContext.getResources().getColor(R.color.black));
                                            achiveDot.setBackgroundColor(mContext.getResources().getColor(R.color.black));
                                        } else if (points > 1000 && points < 10000) {
                                            //bronze
                                            achive.setTextColor(mContext.getResources().getColor(R.color.brown));
                                            achiveDot.setBackgroundColor(mContext.getResources().getColor(R.color.brown));

                                        } else if (points > 10000 && points < 50000) {
                                            //gold
                                            achive.setTextColor(mContext.getResources().getColor(R.color.gold));
                                            achiveDot.setBackgroundColor(mContext.getResources().getColor(R.color.gold));

                                        } else if (points > 50000 && points < 100000) {
                                            //emerald
                                            achive.setTextColor(mContext.getResources().getColor(R.color.emerald));
                                            achiveDot.setBackgroundColor(mContext.getResources().getColor(R.color.emerald));

                                        } else if (points > 100000) {
                                            //ruby
                                            achive.setTextColor(mContext.getResources().getColor(R.color.ruby));
                                            achiveDot.setBackgroundColor(mContext.getResources().getColor(R.color.ruby));
                                        }

                                        if (menuItem.getId().equals(signedInUser)) {
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


                                        /*
                                        if (signedInUser.equals(menuItem.getId())) {
                                            dialogTripledot.setVisibility(View.GONE);
                                        } else {
                                            dialogTripledot.setVisibility(View.VISIBLE);
                                        }
                                         */

                                        if (isDark) {
                                            box.setBackgroundColor(mContext.getResources().getColor(R.color.addingpostgrey));
                                            dialogName.setTextColor(Color.WHITE);
                                            dialogDesc.setTextColor(Color.WHITE);
                                            programName.setTextColor(Color.WHITE);
                                            classOfDesc.setTextColor(Color.WHITE);
                                            //program.setTextColor(Color.WHITE);
                                            //classOf.setTextColor(Color.WHITE);

                                            msg_subtext.setTextColor(Color.WHITE);
                                            groupadd_subtext.setTextColor(Color.WHITE);
                                            block_subtext.setTextColor(Color.WHITE);

                                            personalmessage.setColorFilter(mContext.getResources().getColor(R.color.gnt_white));
                                            groupadd.setColorFilter(mContext.getResources().getColor(R.color.gnt_white));

                                        } else {
                                            box.setBackgroundColor(mContext.getResources().getColor(R.color.gnt_white));
                                        }


                                        block.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                blockUser(signedInUser, menuItem.getId(), dialog);
                                            }
                                        });

                                        groupadd.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();

                                                add_livechat_dialog adding_post = new add_livechat_dialog();
                                                assert ((AppCompatActivity) mContext).getFragmentManager() != null;

                                                Bundle arg = new Bundle();
                                                arg.putString("uid", menuItem.getId());
                                                adding_post.setArguments(arg);

                                                adding_post.show(manager, adding_post.getTag());
                                            }
                                        });

                                        personalmessage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(final View v) {

                                                //String[] sortName = new String [] {menuItem.getId(), signedInUser};
                                                final ArrayList<String> sortName = new ArrayList<String>();
                                                sortName.add(menuItem.getId());
                                                sortName.add(signedInUser);

                                                Log.d("1234", "clicked user " + menuItem.getId());

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
                                                            RTDN2.put("lastMessage", menuItem.getId() + " has joined the group");
                                                            RTDN2.put("lastUser", menuItem.getId());
                                                            RTDN2.put("parentUser", menuItem.getId());
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

                                                            ArrayAdapter<Group> newAdapter = new groupAdapter(mContext, chatGroupArray.getGroupSave());
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

                                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new LiveChatFragment(finalCombinedName, menuItem.getId(), signedInUser,"dm")).addToBackStack("chatFragment").commit();

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

                                        String imgurl = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/" + menuItem.getId() + ".jpeg?alt=media";

                                        Glide.with(dialogPic.getContext()).load(imgurl)
                                                .dontAnimate()
                                                //.apply(RequestOptions.circleCropTransform())
                                                //.signature(new ObjectKey(ts))
                                                .into(dialogPic);

                                        dialog.show();

                                    } catch (Exception e) {
                                        //  Block of code to handle errors
                                        Toast.makeText(mContext, "Error fetching profile", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Toast.makeText(mContext, "This user has been deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


                //     MainActivity activity = (MainActivity) context;
                //    activity.mProgressDialog.dismiss();
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.native_ads,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
                /*
            case TYPE_LOADING:
               View loader = LayoutInflater.from(
                       parent.getContext()).inflate(R.layout.native_ads,
                       parent, false);
               return new LoadingBar(loader);

                 */
            default:
                View menuItemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_row, parent, false);
                return new MenuItemViewHolder(menuItemLayoutView);
        }
    }

    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public static int returnCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public void addLoading() {
        isLoader = true;
        noteList.add(new Note());
        notifyItemInserted(noteList.size() - 1);
    }

    public void removeLoading() {
        isLoader = false;
        int position = noteList.size() - 1;
        Note note = (Note) getItems(position);
        if (note != null) {
            noteList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        noteList.clear();
        notifyDataSetChanged();
    }


    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());


        int nightModeFlags =
                mContext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:

                ((TextView) adView.getHeadlineView()).setTextColor(Color.WHITE);
                ((TextView) adView.getBodyView()).setTextColor(Color.WHITE);
                if (nativeAd.getAdvertiser() != null) {
                    ((TextView) adView.getAdvertiserView()).setTextColor(Color.WHITE);
                }


                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((de.hdodenhof.circleimageview.CircleImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = noteList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        if (isLoader) {
            return position == noteList.size() - 1 ? TYPE_LOADING : MENU_ITEM_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    Note getItems(int position) {
        return (Note) noteList.get(position);
    }

    public void reportAndDeletePost(final String ownerId, Context context, final String did, final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to report?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {


                CollectionReference reportRef = db.collection("Reports");
                Map<String, Object> data = new HashMap<>();
                data.put("ownerId", ownerId);
                data.put("text", text);
                data.put("type", "original");

                reportRef.add(data);
                //postRef.document(did).delete();
                dialog.dismiss();
                Toast.makeText(mContext, "Report has been filled", Toast.LENGTH_LONG);
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

    public void reportPost(final String user, Context context, final String did, String post, String ownerId, String SignedInUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to report?");

        if(isDark){
            builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm</font>"));
        }

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                postRef.document(did)
                        .update(
                                "reports", FieldValue.arrayUnion(SignedInUser)
                        );

                CollectionReference reportRef = db.collection("Reports");
                Map<String, Object> data = new HashMap<>();

                data.put("ownerId", ownerId);
                data.put("text", post);
                data.put("type", "original");
                data.put("originalPostId", did);

                reportRef.document(did).set(data);

                reportRef.document(did).update(
                        "reports", FieldValue.arrayUnion(SignedInUser)
                );

                dialog.dismiss();
                Toast.makeText(mContext, "Report has been filled", Toast.LENGTH_LONG);


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


    private void removePost(Context context, final String did, final int position, String ownerid, String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to remove this post?");

        if(isDark){
            builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm</font>"));
        }

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                postRef.document(did)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                FirebaseMessaging.getInstance().unsubscribeFromTopic(did);
                                noteList.remove(position);
                                if (noteList.size() == 0) {
                                    HomeFragment.isEmpty.setVisibility(View.VISIBLE);
                                }
                                HomeFragment.adapter.notifyDataSetChanged();
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(did);

                                if (type.equals("image")) {
                                    StorageReference postImage = FirebaseStorage.getInstance().getReference().child("Post/" + did);
                                    StorageReference postImageThumb = FirebaseStorage.getInstance().getReference().child("Post/Thumbnail" + did);

                                    postImage.delete();
                                    postImageThumb.delete();
                                }

                            }
                        });
                userRef.document(ownerid).update(
                        "points", FieldValue.increment(-50)
                );

                dialog.dismiss();
                Toast.makeText(mContext, "Post removed", Toast.LENGTH_LONG);


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

}
