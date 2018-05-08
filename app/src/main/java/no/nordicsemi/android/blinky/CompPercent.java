package no.nordicsemi.android.blinky;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;

import no.nordicsemi.android.blinky.database.CorButton;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompPercent extends Fragment implements View.OnClickListener {

    private static final String TAG = "CompPercent";

    ButtonsViewModel buttonsViewModel;

    EditText etCompValue;
    Button btnDec, btnInc;
    SeekBar seekBar;
    RadioButton rbMinus;
    int compCorValue = 0;

    public CompPercent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);

        View v = inflater.inflate(R.layout.fragment_comp_percent, container, false);
        etCompValue = v.findViewById(R.id.etCompValue);
        btnInc = v.findViewById(R.id.btnInc);
        btnDec = v.findViewById(R.id.btnDec);
        seekBar = v.findViewById(R.id.seekBarComp);
        rbMinus = v.findViewById(R.id.rbMinusComp);

        btnInc.setOnClickListener(this);
        btnDec.setOnClickListener(this);

        etCompValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                compCorValue = Integer.valueOf(etCompValue.getText().toString());
                //Log.d("myLogs", "compCorValue" + String.valueOf(compCorValue));
                buttonsViewModel.setmCompCorValue(compCorValue);

            }
        });

        buttonsViewModel.getmCurCorButton().observe(getActivity(), new Observer<CorButton>() {
            @Override
            public void onChanged(@Nullable CorButton corButton) {
                assert corButton != null;
                compCorValue = corButton.getCompValue();
            }
        });

        buttonsViewModel.ismSetButton().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

                if (aBoolean != null) {
                    if (aBoolean) {
                        if (compCorValue < 0) {
                            compCorValue = -compCorValue;
                            rbMinus.setChecked(true);
                        }
                        seekBar.setProgress(compCorValue);
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                compCorValue = progress;
                etCompValue.setText(String.valueOf(compCorValue));
                if (rbMinus.isChecked()) compCorValue = -compCorValue;
                buttonsViewModel.setmCompCorValue(compCorValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInc:
                compCorValue++;
                seekBar.setProgress(compCorValue);


                break;

            case R.id.btnDec:
                compCorValue--;
                seekBar.setProgress(compCorValue);
                break;
        }
    }
}
