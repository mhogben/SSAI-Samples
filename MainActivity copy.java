package com.brightcove.player.samples.onceux.basic;

import android.os.Bundle;

import com.brightcove.player.controller.NoSourceFoundException;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;

import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.SourceCollection;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import com.brightcove.onceux.OnceUxComponent;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BrightcovePlayer {


    // Private class constants

    private  final String TAG = this.getClass().getSimpleName();



    public static void addVMAPTagSSAI(Video video, String vmapQuery){

        Map<DeliveryType,SourceCollection> map = video.getSourceCollections();
        for (SourceCollection sourceCollection : map.values()) {
            for (Source s : sourceCollection.getSources()) {
                String full = (s.getProperties().get(Source.Fields.VMAP)).toString().concat(vmapQuery);
                s.getProperties().put(Source.Fields.VMAP,full);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.onceux_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);


        final OnceUxComponent plugin = new OnceUxComponent(this, brightcoveVideoView);
        Catalog catalog = new Catalog(brightcoveVideoView.getEventEmitter(), accountID, PolicyID);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put("ad_config_id", adConfigID);
        catalog.findVideoByID(videoID, null, parameters, new VideoListener() {
            @Override
            public void onVideo(Video video) {

                String appendedValues = "&appId=p8&platformType=app&deviceType=ios&cmsId=2464276";

                addVMAPTagSSAI(video,appendedValues);

                try {
                    plugin.processVideo(video);
                } catch (NoSourceFoundException e) {
                    // If NoSourceFoundException is thrown it means a suitable VMAP URL was not found.
                    // You can try to play it on a regular view,
                    brightcoveVideoView.add(video);
                }
            }
        });

   }

}
