package com.androideasily.olachallenge;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Anand on 16-12-2017.
 */

//The Actual song player with downlaod suppot (exoplayer)
public class FragmentPlayer extends Fragment {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private ImageButton bt_play;//play or pause button
    private ImageButton bt_favourite;//add/remove favourites button
    private ExoPlayer exoPlayer;//music streamer
    private ImageView albumImage;//display the album image
    private TextView songName, artistName;//songname and artist name display textviews
    private FavouritePreference favouritePreference;
    private String currentlyPlayingSongName = null, currentArtist = null, albumUrl = null;
    private long reference;//download reference
    private String downloadUrl;//url to download the song if user wishes to
    private PermissionManager permissionManager;//permission manager class instance to handle the permissions
    ExoPlayer.Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        exoPlayer = ExoPlayer.Factory.newInstance(1);
        favouritePreference = QueueController.getInstance(getActivity().getApplicationContext()).getFavouritePreference();
        permissionManager = new PermissionManager(getActivity());

        View playerView = inflater.inflate(R.layout.fragment_play, container, false);

        bt_play = (ImageButton) playerView.findViewById(R.id.bt_play_pause);
        ImageButton bt_stop = (ImageButton) playerView.findViewById(R.id.bt_next);
        bt_favourite = (ImageButton) playerView.findViewById(R.id.bt_previous);
        ImageButton bt_download = (ImageButton) playerView.findViewById(R.id.bt_download);
        albumImage = (ImageView) playerView.findViewById(R.id.album_cover);
        songName = (TextView) playerView.findViewById(R.id.tv_name);
        artistName = (TextView) playerView.findViewById(R.id.tv_artist);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(downloadReceiver, filter);

        //MUSIC PLAYER CONTROLS
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer.getPlaybackState() == ExoPlayer.STATE_BUFFERING && !currentlyPlayingSongName.equals(""))
                    Snackbar.make(container.getRootView(), "Please Choose A Song To Stream", Snackbar.LENGTH_LONG).show();
                else if (exoPlayer.getPlayWhenReady()) {
                    exoPlayer.setPlayWhenReady(false);
                    bt_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                } else {
                    exoPlayer.setPlayWhenReady(true);
                    bt_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                }
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.stop();
                currentlyPlayingSongName = "";
                exoPlayer.removeListener(listener);
                albumImage.setImageDrawable(getResources().getDrawable(R.drawable.op_logo));
                bt_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                artistName.setText("");
                songName.setText(R.string.stream);
            }
        });
        bt_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentlyPlayingSongName.equals("")) {
                    if (favouritePreference.checkPreference(currentlyPlayingSongName)) {
                        bt_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                        favouritePreference.updatePreference(currentlyPlayingSongName, false);
                        Snackbar.make(v, currentlyPlayingSongName + " Removed From Favourites", Snackbar.LENGTH_SHORT).show();
                    } else {
                        bt_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                        favouritePreference.updatePreference(currentlyPlayingSongName, true);
                        Snackbar.make(v, currentlyPlayingSongName + " Added To Favourites", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionManager.checkPermission() == PackageManager.PERMISSION_GRANTED) {
                    if (!currentlyPlayingSongName.equals("")) {
                        downloadSong();
                    }
                } else {
                    permissionManager.getPermission();
                }
            }
        });
        //detecting ehn song finished playing
        listener = new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    exoPlayer.stop();
                    exoPlayer.removeListener(listener);
                    currentlyPlayingSongName = "";
                    albumImage.setImageDrawable(getResources().getDrawable(R.drawable.op_logo));
                    bt_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                    artistName.setText("");
                    songName.setText(R.string.stream);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        };
        return playerView;
    }

    //Downloading the song using andorid downlaod manager
    private void downloadSong() {
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setTitle(currentlyPlayingSongName + ".mp3");
        request.setDescription("Downloading Your Song - Ola Play");
        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, "/" + currentlyPlayingSongName + ".mp3");
        reference = downloadManager.enqueue(request);
    }

    //permission request callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionManager.updatePermissions(requestCode, permissions, grantResults)) {
            Snackbar.make(getView(), "Permission Granted", Snackbar.LENGTH_SHORT).show();
            downloadSong();
        } else
            Snackbar.make(getView(), "Cannot Download Song", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //displaying the song to stream informations
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.i("LIFECYCLE", "onActivityCreatedNOTNULL");
            songName.setText(savedInstanceState.getString("name"));
            artistName.setText(savedInstanceState.getString("artist"));
            Picasso.Builder builder = new Picasso.Builder(getActivity());
            builder.downloader(new OkHttpDownloader(getActivity()));
            builder.build()
                    .load(savedInstanceState.getString("album"))
                    .resize(860, 830)
                    .centerCrop()
                    .into(albumImage);
        }
        super.onActivityCreated(savedInstanceState);
    }

    // TO implement portfolio and history - but due to lack of time wont be able to complete them
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("name", currentlyPlayingSongName);
        outState.putString("album", albumUrl);
        outState.putString("artist", currentArtist);
        super.onSaveInstanceState(outState);
    }

    //data passed from the activity (interface)
    void displayReceivedData(JSONObject message) {
        //parsing the data
        if (exoPlayer.getPlaybackState() == ExoPlayer.STATE_READY)
            exoPlayer.stop();
        String url = null;
        try {
            downloadUrl = message.getString("url");
            albumUrl = message.getString("album");
            currentlyPlayingSongName = message.getString("name");
            //displaying the image using picasso (okhttp downlaoded to deal with URL redirection)
            Picasso.Builder builder = new Picasso.Builder(getActivity());
            builder.downloader(new OkHttpDownloader(getActivity()));
            builder.build()
                    .load(albumUrl)
                    .resize(1000, 1000)
                    .centerCrop()
                    .into(albumImage);
            songName.setText(currentlyPlayingSongName);
            currentArtist = (message.getString("artist"));
            artistName.setText(currentArtist);
            //choosing image corresponding to favourite and not favourite songs
            if (favouritePreference.checkPreference(currentlyPlayingSongName))
                bt_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            else
                bt_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
            bt_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Streaming the song
        Uri streamUri = Uri.parse(downloadUrl);
        DefaultAllocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(getActivity(), "OlaChallenge");
        DataSource dataSource = new DefaultUriDataSource(getActivity(), null, userAgent, true);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                streamUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        exoPlayer.prepare(audioRenderer);
        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(true);
        final Snackbar snackbar = Snackbar.make(getView(), currentlyPlayingSongName + " Has Started Streaming", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override(eg: userLoggedIn
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(Color.WHITE).show();
    }

    //Broadcast Receiver to know when song download finishes
    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == referenceID) {
                Snackbar.make(getView(), "File Sucessfully Downloaded", Snackbar.LENGTH_LONG).show();
            }
        }
    };

}