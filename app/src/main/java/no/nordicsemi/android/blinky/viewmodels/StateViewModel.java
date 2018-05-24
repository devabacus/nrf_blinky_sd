package no.nordicsemi.android.blinky.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class StateViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mAutoCorMode = new MutableLiveData<>();

    public LiveData<Integer> getAutoCorMode() {
        return mAutoCorMode;
    }
    public void setmAutoCorMode(final Integer autoCorMode){
        mAutoCorMode.setValue(autoCorMode);
    }

    public StateViewModel(@NonNull Application application) {
        super(application);
    }





}
