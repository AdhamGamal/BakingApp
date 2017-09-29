package com.amg.bakingapptest.ui;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.module.Recipe;
import com.amg.bakingapptest.module.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class StepsDetailFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = StepsDetailActivity.class.getSimpleName();
    private TextView longDescription;
    private Button prev;
    private Button next;
    private static SimpleExoPlayer exoPlayer;
    private static SimpleExoPlayerView playerView;
    private static MediaSessionCompat mediaSession;
    private static PlaybackStateCompat.Builder stateBuilder;

    private static Recipe recipe;
    private static Step step;
    private static int id = 0;
    private static long position = 0;
    private static boolean change, onPlay = true;
    private static Uri videoUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_detail, container, false);
        if (getArguments().containsKey("recipe")) {
            recipe = getArguments().getParcelable("recipe");
            id = getArguments().getInt("step_id");
            if (id < recipe.getSteps().size()) {
                step = recipe.getSteps().get(id);
            }
        }
        getActivity().setTitle(step.getShortDescription());

        longDescription = (TextView) rootView.findViewById(R.id.description);
        longDescription.setText(step.getDescription());

        playerView = (SimpleExoPlayerView) rootView.findViewById(R.id.player_view);

        prev = (Button) rootView.findViewById(R.id.prev);
        next = (Button) rootView.findViewById(R.id.next);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id > 0) {
                    step = recipe.getSteps().get(--id);
                    longDescription.setText(step.getDescription());
                    change = true;
                    initializePlayer(Uri.parse(step.getVideoURL()));
                    restExoPlayer(0, false);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id < recipe.getSteps().size() - 1) {
                    step = recipe.getSteps().get(++id);
                    longDescription.setText(step.getDescription());
                    change = true;
                    initializePlayer(Uri.parse(step.getVideoURL()));
                    restExoPlayer(0, false);
                }
            }
        });

        initializeMediaSession();
        videoUri = Uri.parse(step.getVideoURL());
        if ((savedInstanceState != null) && savedInstanceState.containsKey("pos")) {
            position = savedInstanceState.getLong("pos");
            exoPlayer.seekTo(position);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !MainActivity.isTablet) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            longDescription.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);

            playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            restExoPlayer(position, true);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (exoPlayer != null)
            outState.putLong("pos", exoPlayer.getCurrentPosition());
    }


    private void initializeMediaSession() {
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession = new MediaSessionCompat(getContext(), TAG);
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MySessionCallback());
        mediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(), new DefaultLoadControl());
            exoPlayer.addListener(this);
            exoPlayer.prepare(new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), Util.getUserAgent(getContext(), "Baking App")), new DefaultExtractorsFactory(), null, null));
            playerView.setPlayer(exoPlayer);
            restExoPlayer(position, false);
        }
        if (change) {
            exoPlayer.prepare(new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), Util.getUserAgent(getContext(), "Baking App")), new DefaultExtractorsFactory(), null, null));
            change = false;
        }
    }


    private void restExoPlayer(long position, boolean playWhenReady) {
        this.position = position;
        if (exoPlayer != null) {
            exoPlayer.seekTo(position);
            exoPlayer.setPlayWhenReady(playWhenReady);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            position = exoPlayer.getCurrentPosition();
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null)
            initializePlayer(videoUri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
        onPlay = false;
        mediaSession.setActive(false);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == PlaybackStateCompat.STATE_PLAYING || playbackState == PlaybackStateCompat.STATE_PAUSED) {
            position = exoPlayer.getCurrentPosition();
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            restExoPlayer(0, false);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }
}