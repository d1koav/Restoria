package com.example.restoria.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<List<String>> galleryListLiveData;

    public GalleryViewModel() {
        galleryListLiveData = new MutableLiveData<>();
    }

    public LiveData<List<String>> getGalleryListLiveData() {
        return galleryListLiveData;
    }

    public void updateGalleryList(List<String> galleryList) {
        galleryListLiveData.setValue(galleryList);
    }
}

