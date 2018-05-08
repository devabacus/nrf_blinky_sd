package no.nordicsemi.android.blinky.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CorButton {

    @PrimaryKey
    private long id;

    private String butNum;
    private String corDir;
    private int corValue;
    private int compValue;
//    private Boolean initialized;


    public CorButton(long id, String butNum, String corDir, int corValue, int compValue) {
        this.id = id;
        this.butNum = butNum;
        this.corDir = corDir;
        this.corValue = corValue;
        this.compValue = compValue;

    }

    public String getButNum() {
        return butNum;
    }

    public void setButNum(String butNum) {
        this.butNum = butNum;
    }

    public String getCorDir() {
        return corDir;
    }

    public void setCorDir(String corDir) {
        this.corDir = corDir;
    }

    public int getCorValue() {
        return corValue;
    }

    public void setCorValue(int corValue) {
        this.corValue = corValue;
    }

    public int getCompValue() {
        return compValue;
    }

    public void setCompValue(int compValue) {
        this.compValue = compValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public Boolean getInitialized() {
//        return initialized;
//    }
//
//    public void setInitialized(Boolean initialized) {
//        this.initialized = initialized;
//    }
}
