package app.mayven.mayven;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FullScreenImage extends Fragment {
    private String did;
    private ImageView back, download;
    private PhotoView photoView;
    private Boolean clickedBefore = false;
    private RelativeLayout relativeLayout;
    //private Bitmap headView;

    private FragmentManager fm = getFragmentManager();


    public FullScreenImage(String did) {
        this.did = did;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fullscreen_image, container, false);

        photoView = view.findViewById(R.id.image);

        back = view.findViewById(R.id.back);
        download = view.findViewById(R.id.download);
        relativeLayout = view.findViewById(R.id.wrapper);

        String igu = "https://firebasestorage.googleapis.com/v0/b/chatapp-6d978.appspot.com/o/Post%2F" + did + ".jpeg?alt=media";


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm = getFragmentManager();
                fm.popBackStack();
            }
        });


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide bottom nav
                URL url = null;
                try {
                    url = new URL(igu);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap image = null;
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String fileName = String.format("%d.jpg", System.currentTimeMillis());


                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), image,fileName, "description");

                Toast.makeText(getActivity(), "", Toast.LENGTH_LONG );
                /*

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();

                File dir = new File(sdCard.getAbsolutePath() + "/Mayven");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                 */



                //Log.e(TAG, e.getMessage());

            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedBefore) {
                    final Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            back.setVisibility(View.VISIBLE);
                            download.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.VISIBLE);
                            clickedBefore = false;

                        }
                    }, 200);

                } else {
                    back.setVisibility(View.INVISIBLE);
                    download.setVisibility(View.INVISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    clickedBefore = true;
                }
            }
        });

        Glide.with(photoView.getContext()).load(igu)
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                //  .error(R.drawable.ic_person_fill)
                .into(photoView);


        return view;
    }
}
