package com.kbaquri.olaplaystudios;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Sameer on 17-Dec-17.
 */

public class PlayerManager {

    private SimpleExoPlayer player;
    private long contentPosition;

    DefaultHttpDataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;

    public void init(Context context, SimpleExoPlayerView simpleExoPlayerView) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory audioTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(audioTrackSelectionFactory);

        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);


        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, context.getString(R.string.app_name)),null,1000,1000,true);

        // Produces Extractor instances for parsing the content media (i.e. not the ad).
        extractorsFactory = new DefaultExtractorsFactory();

    }

    public void setSong(String contentUrl){
        // This is the MediaSource representing the content media (i.e. not the ad).
        MediaSource contentMediaSource = new ExtractorMediaSource(
                Uri.parse(contentUrl), dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(contentMediaSource);
        player.setPlayWhenReady(true);
    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

}
