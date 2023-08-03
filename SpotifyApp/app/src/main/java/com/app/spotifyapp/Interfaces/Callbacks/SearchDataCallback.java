package com.app.spotifyapp.Interfaces.Callbacks;

import java.util.ArrayList;
import java.util.List;

public interface SearchDataCallback<T> {
    void onSearchDataCallback(ArrayList<T> data);
}
