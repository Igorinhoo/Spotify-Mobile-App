package com.app.spotifyapp.Models;

import com.google.gson.annotations.SerializedName;

public class QueueResponse {

    @SerializedName("queue")
    private QueueItem[] queueItems;

    public QueueItem[] getQueueItems() {
        return queueItems;
    }
}
