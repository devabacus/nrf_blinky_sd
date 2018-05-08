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

import no.nordicsemi.android.blinky.database.AppDatabase;
import no.nordicsemi.android.blinky.database.CorButton;

import java.util.ArrayList;
import java.util.List;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_LIST_NUM_BUTTONS;


/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    long id = 11;
    Button addButton;
    int prefNumOfButs = 8;
    List<CorButton> corButtonList;

    public static boolean initialized = false;
    private static final String TAG = "myLogs";

    RecyclerView recButView;
    ButtonAdapter adapter;

    private ButtonsViewModel buttonsViewModel;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public static AppDatabase appDatabase;

    public ButtonFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);

        View v = inflater.inflate(R.layout.fragment_button, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefNumOfButs = Integer.valueOf(sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8"));

        recButView = v.findViewById(R.id.but_rec_view);
        adapter = new ButtonAdapter(new ArrayList<CorButton>(), this, this);
        recButView.setAdapter(adapter);
        recButView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        buttonsViewModel.getCorButList().observe(getActivity(), new Observer<List<CorButton>>() {
            @Override
            public void onChanged(@Nullable List<CorButton> corButtonList) {
                Log.d(TAG, "onChanged: corButtonList.size() = ");
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

                Log.d("myLogs", "size of list = " + corButtonList.size());
                adapter.additems(corButtonList);

            }
        });



        addButton = v.findViewById(R.id.add_btns);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                id++;
                buttonsViewModel.addCorBut(new CorButton(
                        id, "", "-", 0, 0
                ));
            }
        });
        return v;
    }


    @Override
    public void onClick(View v) {

        CorButton corButton = (CorButton) v.getTag();


        //если настроена компенсация
        Log.d("myLogs", "corValue " + corButton.getCorValue() + ", corDir " + corButton.getCorDir());

        if (corButton.getCorDir().equals("-%") && (corButton.getCompValue() != 0)) {
            Log.d("myLogs", ", compValue = " + corButton.getCompValue());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        CorButton corButton = (CorButton) v.getTag();

        buttonsViewModel.setmCurCorButton(corButton);
        buttonsViewModel.setmSetButton(true);
        //Log.d("myLogs", "onLongClick " + corButton.getButNum());
        return false;
    }


}
