package com.example.restoria.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.restoria.OrdersStatusDataModel;
import java.util.List;

public class SlideshowViewModel extends ViewModel {
    private MutableLiveData<List<OrdersStatusDataModel>> dataList;

    public SlideshowViewModel() {
        dataList = new MutableLiveData<>();
    }

    public LiveData<List<OrdersStatusDataModel>> getDataList() {
        return dataList;
    }
    public void updateSlideshowList(List<OrdersStatusDataModel> orderlist) {dataList.setValue(orderlist);}
}
