package no.nordicsemi.android.blinky;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.StateViewModel;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_ADC_SHOW;

/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {


    private static final String TAG = "StateFragment";
    private BlinkyViewModel blinkyViewModel;
    private StateViewModel stateViewModel;
    TextView tvAdc, tvAutoMode, tvCorMode;

    String bleMsg[];
    int adcValue = 0;
    Boolean adcShow = false;

    public StateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
        View v = inflater.inflate(R.layout.fragment_state, container, false);

        tvAdc = v.findViewById(R.id.tv_adc);
        tvAutoMode = v.findViewById(R.id.tv_auto_mode);
        tvCorMode = v.findViewById(R.id.tv_cor_mode);

        stateViewModel.getAutoCorMode().observe(getActivity(), i -> {
            if (i != null) {
                if (i == 1) tvAutoMode.setVisibility(View.VISIBLE);
                else if (i == 0) {
                    tvAutoMode.setVisibility(View.GONE);
                    tvAutoMode.setText("Авторежим: нет значения");
                }
            }

        });
        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if (s.matches("^ad.*")) {
                String adcValueStr = s.substring(s.indexOf('d') + 1);
                adcValueStr = adcValueStr.replaceAll("[^0-9]", "");
                if (adcValueStr.matches("[0-9]*")) {
                    adcValue = Integer.parseInt(adcValueStr);
                }
                //String resAdc = String.format(getResources().getString(R.string.adc), adcValue);
                String resAdc = String.format(getString(R.string.adc1), adcValue);
                tvAdc.setText(resAdc);
            } else if (s.matches("n.*/.*")) {
                int slashIndex = s.indexOf('/');
                int notif_num = Integer.valueOf(s.substring(1, slashIndex));
                int notif_value = Integer.valueOf(s.substring(slashIndex + 1).replaceAll("[^0-9]", ""));

                switch (notif_num) {
                    // состояние корректировки при работе в авторежиме
                    case 1:
                        if (notif_value == 1) tvAutoMode.setText("Авторежим: активно");
                        else if (notif_value == 0) tvAutoMode.setText("Авторежим: сброс");
                        else if (notif_value == 2) tvAutoMode.setText("Авторежим: ожидание");
                        else if (notif_value == 3) tvAutoMode.setText("Авторежим: сброс значения");

                        break;
                        //notify auto correct_mode_change
                    case 3:
                        if(notif_value == 1) tvCorMode.setText("Режим работы: авто");
                        else if (notif_value == 2) tvCorMode.setText("Режим работы: ручной");

                }
//                Log.d(TAG, "onCreateView: notif_num = " + notif_num);
//                Log.d(TAG, "onCreateView: notif_value = " + notif_value);


            }


        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        prefNumOfButs = Integer.valueOf(sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8"));
        adcShow = sharedPreferences.getBoolean(KEY_ADC_SHOW, false);

        Log.d(TAG, "onResume: ");
        if (adcShow) {
            tvAdc.setVisibility(View.VISIBLE);
            blinkyViewModel.sendTX(Cmd.ADC_SHOW_ON);
        } else {
            tvAdc.setVisibility(View.GONE);
            blinkyViewModel.sendTX(Cmd.ADC_SHOW_OFF);
        }
    }
}
