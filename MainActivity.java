package com.example.mehrbod.gifdisplayingsecond;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_REQUEST_CODE = 10231;
    private TextView addressTextView = null;
    private Uri fileUri = null;
    private FFmpegMediaMetadataRetriever mediaMetadataRetriever = null;
    private Long maxDur;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        addressTextView = (TextView) findViewById(R.id.textViewFileAddressMainActivity);
    }

    public void onOpenFileButtonClickListener(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fileUri = data.getData();
                File file = new File(fileUri.getPath());
                String path = file.getPath();
                path = path.replace("/document/primary:", "/storage/emulated/0/");
                Log.d("Data", path);

                if (hasSound(path)) {
                    addressTextView.setText(path + "\nHas sound.");
                } else {
                    addressTextView.setText(path + "\nDoesn't have sound.");
                }
            }
        }
    }

    public void onConvertToGifButtonClickListener(View view) {
        GifMaker gifMaker = new GifMaker(1);
        gifMaker.makeGifFromVideo(videoPath, 0, maxDur, 250, Environment.getExternalStorageDirectory() + "/test.gif");

    }

    private boolean hasSound(String path) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(path);
        videoPath = path;
        String dur = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        maxDur = Long.parseLong(dur);

        String sound =
                retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC);

        if (sound == null) {
            return false;
        }
        return true;
    }

    public void onDisplayGifButtonClickListener(View view) {
        SimpleDraweeView myView = (SimpleDraweeView) findViewById(R.id.simpleDrawee);

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/test.gif"));

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        myView.setController(controller);
    }
}
