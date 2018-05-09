package no.nordicsemi.android.blinky;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import no.nordicsemi.android.blinky.database.AppDatabase;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_LIST_NUM_BUTTONS;
public class ButtonFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    long id = 11;
    int prefNumOfButs = 8;
    List<CorButton> corButtonList;
    public static boolean initialized = false;
    private static final String TAG = "ButtonFrag";
    RecyclerView recButView;
    ButtonAdapter adapter;
    StringBuilder msg;

    TextView tvState;
    Button btnRes;
    private ButtonsViewModel buttonsViewModel;
    private BlinkyViewModel blinkyViewModel;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public static AppDatabase appDatabase;
    public ButtonFrag() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        //blinkyViewModel.connect(device);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_button, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefNumOfButs = Integer.valueOf(sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8"));
        recButView = v.findViewById(R.id.but_rec_view);
        adapter = new ButtonAdapter(new ArrayList<CorButton>(), this, this);
        recButView.setAdapter(adapter);

        blinkyViewModel.getUartData().observe(getActivity(), s -> Log.d(TAG, "onChanged: getData " + s));

        recButView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        // Отслеживание изменения количества кнопок в настройках
        buttonsViewModel.getCorButList().observe(getActivity(), corButtonList -> {
            //Log.d(TAG, "onChanged: corButtonList.size() = ");
            if (corButtonList != null) {

                int listSize = corButtonList.size();
                if (listSize == 0) {
                    initialized = false;

                    for (int i = 0; i < 16; i++) {
                        id = i;
                        buttonsViewModel.addCorBut(new CorButton(
                                id, "", "-", 0, 0
                        ));
                    }
                } else if (listSize < prefNumOfButs){
                    initialized = true;

                    for (int i = listSize; i < prefNumOfButs; i++) {
                        id = i;
                        buttonsViewModel.addCorBut(new CorButton(
                                id, "", "-", 0, 0
                        ));
                    }
                } else if (listSize > prefNumOfButs){
                    initialized = true;
                    for(int i = listSize; i > prefNumOfButs-1; i--){
                        id = i;
                        buttonsViewModel.getCorButtonById(id).observe(getActivity(), new Observer<CorButton>() {
                            @Override
                            public void onChanged(@Nullable CorButton corButton) {
                                if(corButton != null)
                                buttonsViewModel.deleteCorBut(corButton);
                            }
                        });
                    }
                }
            }

           // Log.d("myLogs", "size of list = " + corButtonList.size());
            adapter.additems(corButtonList);

        });
        btnRes = v.findViewById(R.id.btnRes);
        tvState = v.findViewById(R.id.tv_state);
        btnRes.setOnClickListener(v1 -> blinkyViewModel.sendTX("$r&"));

        return v;

    }

    void makeMsg(CorButton corButton) {
        msg = new StringBuilder();
        msg.append("$");
        msg.append(corButton.getId()+1).append(",");
        msg.append(corButton.getCorDir());
        msg.append(corButton.getCorValue());
        if (corButton.getCorDir().contains("p")) msg.append(",").append(corButton.getCompValue());
        msg.append("&");
    }

    @Override
    public void onClick(View v) {
        CorButton corButton = (CorButton) v.getTag();
        tvState.setText(corButton.getButNum());
        makeMsg(corButton);
        Log.d(TAG, "onClick: msg = " + msg.toString());
        blinkyViewModel.sendTX(msg.toString());
    }

    @Override
    public boolean onLongClick(View v) {
        CorButton corButton = (CorButton) v.getTag();
        buttonsViewModel.setmCurCorButton(corButton);
        buttonsViewModel.setmSetButton(true);
        return false;
    }
}
