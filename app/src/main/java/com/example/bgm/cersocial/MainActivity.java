package com.example.bgm.cersocial;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import util.SCService;
import util.U;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    static ImageView img;
    static Bitmap bitmap = null;
    static boolean image_flag = false;
    static boolean video_flag = false;
    ProgressDialog pDialog;
    MainActivity selfActivity = null;
    YouTubePlayer selfPlayer = null;
    static String outTxt = "";

    String APPNAME = "BG";
    public static final String API_KEY = "AIzaSyDXYqdcfm3v8PdR5N_Yy6oIrVxmi18zJ8w";
    public static final String VIDEO_ID = "ytqs8BHO-aU";
    private String imageURL = "http://igcdn-photos-c-a.akamaihd.net/hphotos-ak-xpa1/t51.2885-15/e35/1515066_1699209146964138_2128035994_n.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // onCreate may be execute anytime, this app always double onCreate() so need store outTxt to show on UI
        U.setTitleAppVersion(this);
        U.setTextViewTextWithTS(this, getString(R.string.app_name) + " " + BuildConfig.VERSION_CODE + " " + BuildConfig.VERSION_NAME + "\n" + outTxt);
        try {
            SCService.recreateFileOnce();
        } catch (IOException e) {
            U.e(e);
            U.setTextViewTextWithTS(this,e);
        }

        selfActivity = this;
        // ------------------ Youtube
        if (!video_flag) { //Initializing YouTube player view
            YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
            youTubePlayerView.initialize(API_KEY, this);
        }
        //------------------------ Instagram
        img = (ImageView) findViewById(R.id.img);
        if (bitmap != null) {
            img.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        //Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
        Log.d(APPNAME, "Video onInitializationFailure");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        if (!video_flag) {
            player.setPlayerStateChangeListener(playerStateChangeListener);
            player.setPlaybackEventListener(playbackEventListener);
            player.setFullscreen(true);
            selfPlayer = player;
            player.loadVideo(VIDEO_ID);
        }
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
            Log.d(APPNAME, "Video Buffering");
            SCService.writeYoutubeDATA("Buffering");
        }

        @Override
        public void onPaused() {
            Log.d(APPNAME, "Video Paused");
        }

        @Override
        public void onPlaying() {
            Log.d(APPNAME, "Video Playing");
            SCService.writeYoutubeDATA("Playing");
        }

        @Override
        public void onSeekTo(int arg0) {
            Log.d(APPNAME, "onSeekTo");
        }

        @Override
        public void onStopped() {
            Log.d(APPNAME, "Video stopped");
        }
    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
            Log.d(APPNAME, "onAdStarted");
        }

        @Override
        public void onError(ErrorReason arg0) {
            Log.d(APPNAME, "onError");
            SCService.writeYoutubeERROR("Youtube ErrorReason:"+arg0.name());
        }

        @Override
        public void onLoaded(String arg0) {
            Log.d(APPNAME, "onLoaded");
        }

        @Override
        public void onLoading() {
            Log.d(APPNAME, "onLoading");
        }

        @Override
        public void onVideoEnded() {
            Log.d(APPNAME, "onVideoEnded");
            SCService.writeYoutubeDATA("End");
            SCService.writeYoutubeDATA(SCService.END_APP);
            video_flag = true;
            if (!image_flag) {
                new LoadImage().execute(imageURL);
            } else {
                Log.d(APPNAME, "Image already loaded");
            }
        }

        @Override
        public void onVideoStarted() {
            Log.d(APPNAME, "onVideoStarted");
            SCService.writeYoutubeEVENT(SCService.START_APP);
            SCService.writeYoutubeDATA("Started");
        }
    };

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            //Log.d(APPNAME,"Loading image");
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                Log.d(APPNAME, "Loading Image..");
                SCService.writeInstagramEVENT(SCService.START_APP);
                SCService.writeInstagramDATA("Begin");
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                U.e(e);
                SCService.writeInstagramERROR(e.getMessage());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                img.setImageBitmap(image);
                pDialog.dismiss();
                Log.d(APPNAME, "Load Image OK");
                SCService.writeInstagramDATA("End");
                SCService.writeInstagramDATA(SCService.END_APP);
                image_flag = true;
                outTxt = SCService.CERSocialFile+":\n"+ SCService.readFile();
                U.d("CERSocialFile:\n" + outTxt);
            } else {
                pDialog.dismiss();
                //Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
                Log.e(APPNAME, "Load Image Error");
            }
            selfActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        }
    }

    /*---------------------------------------------------------------------------------
    * Untouch generated
     ---------------------------------------------------------------------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
