package no.nordicsemi.android.blinky;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {


    BlinkyViewModel blinkyViewModel;
    TextView tvBleInfo;
    String bleMsg[];
    int adcValue = 0;

    public StateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);

        View v = inflater.inflate(R.layout.fragment_state, container, false);
        tvBleInfo = v.findViewById(R.id.tv_adc);
        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if(s.matches("^ad.*")){
                String adcValueStr = s.substring(s.indexOf('d') + 1);
                if(adcValueStr.matches("[0-9]*")){
                    adcValue = Integer.valueOf(adcValueStr);
                }
                //String resAdc = String.format(getResources().getString(R.string.adc), adcValue);
                String resAdc = String.format(getString(R.string.adc1), adcValue);
                //tvBleInfo.setText(getString(R.string.adc) + adcValue);
                tvBleInfo.setText(resAdc);
            }


        });

        return v;
    }

}
