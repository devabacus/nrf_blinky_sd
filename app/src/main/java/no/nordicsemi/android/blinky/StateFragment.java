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

import java.util.Objects;

import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_ADC_SHOW;

/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {


    private static final String TAG = "StateFragment";
    BlinkyViewModel blinkyViewModel;
    TextView tvAdc, tvAutoMode;

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

        View v = inflater.inflate(R.layout.fragment_state, container, false);
        tvAdc = v.findViewById(R.id.tv_adc);
        tvAutoMode = v.findViewById(R.id.tv_auto_mode);
        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if(s.matches("^ad.*")){
                String adcValueStr = s.substring(s.indexOf('d') + 1);
                adcValueStr = adcValueStr.replaceAll("[^0-9]", "");
                if (adcValueStr.matches("[0-9]*")) {
                    adcValue = Integer.parseInt(adcValueStr);
                }
                //String resAdc = String.format(getResources().getString(R.string.adc), adcValue);
                String resAdc = String.format(getString(R.string.adc1), adcValue);
                tvAdc.setText(resAdc);
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
