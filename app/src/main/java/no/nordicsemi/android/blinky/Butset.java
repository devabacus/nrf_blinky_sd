package no.nordicsemi.android.blinky;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import no.nordicsemi.android.blinky.database.AppDatabase;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Butset extends Fragment implements View.OnClickListener {

    private static final String TAG = "Butset";


    ButtonsViewModel buttonsViewModel;
    BlinkyViewModel blinkyViewModel;
    Button btnClose, btnSave, btnInc, btnDec;
    EditText etButName;
    CorButton curCorButton;
    SeekBar seekBar;
    EditText etCorValue;
    String butName;
    String dirCor = "+";
    int corValue;
    int compValue = 0;
    RadioGroup rgCorDir;
    CheckBox cbComp;
    FrameLayout frameComp;
    RadioButton rbMinus;
    RadioButton rbPlus;
    RadioButton rbPercent;
    long curCorButId = 0;
    AppDatabase appDatabase;
    CorButton CorButtonById;
    Boolean firstOpenSet = false;


    ConstraintLayout setLayout;

    public Butset() {
        // Required empty public constructor
    }


    void resetCor() {
        blinkyViewModel.sendTX("$r&");
        // сброс seekbar и корректировки если мы меняем направление (переключаем radiobuttons),
        // когда мы заходим первый раз удержанием кнопки мы должны выставить предыдущее сохраненное значение.
        if (!firstOpenSet) {
            seekBar.setProgress(10);
            Log.d(TAG, "resetCor: firstOpenSet");
        } else firstOpenSet = false;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        appDatabase = AppDatabase.getDatabase(this.getContext());

        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        blinkyViewModel = ViewModelProviders.of(getActivity()).get(BlinkyViewModel.class);

        final View v = inflater.inflate(R.layout.fragment_butset, container, false);
        setLayout = v.findViewById(R.id.butSetLayout);
        btnClose = v.findViewById(R.id.btnClose);
        btnSave = v.findViewById(R.id.btnSave);
        etButName = v.findViewById(R.id.etButNum);
        etCorValue = v.findViewById(R.id.etCorValue);
        btnInc = v.findViewById(R.id.btnInc);
        btnDec = v.findViewById(R.id.btnDec);
        seekBar = v.findViewById(R.id.seekBar);
        rgCorDir = v.findViewById(R.id.rgCorDir);
        cbComp = v.findViewById(R.id.cbCompPerc);
        frameComp = v.findViewById(R.id.frameFragComp);
        rbPlus = v.findViewById(R.id.rbPlus);
        rbMinus = v.findViewById(R.id.rbMinus);
        rbPercent = v.findViewById(R.id.rbPercent);

        btnDec.setOnClickListener(this);
        btnInc.setOnClickListener(this);

        rgCorDir.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbPlus:
                    cbComp.setVisibility(View.GONE);
                    frameComp.setVisibility(View.GONE);
                    dirCor = "+";
                    compValue = 0;
                    resetCor();
                    break;

                case R.id.rbMinus:
                    dirCor = "-";
                    cbComp.setVisibility(View.GONE);
                    frameComp.setVisibility(View.GONE);
                    compValue = 0;
                    resetCor();
                    break;

                case R.id.rbPercent:
                    dirCor = "p";
                    cbComp.setVisibility(View.VISIBLE);
                    if (cbComp.isChecked()) frameComp.setVisibility(View.VISIBLE);
                    resetCor();
                    break;

            }
        });

        cbComp.setOnClickListener(v1 -> {
            if (cbComp.isChecked()) frameComp.setVisibility(View.VISIBLE);
            else frameComp.setVisibility(View.GONE);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                corValue = progress;
                etCorValue.setText(String.valueOf(corValue));
                //Log.d(TAG, "onProgressChanged: curCorButton.getCorDir() = " + curCorButton.getCorDir());
                blinkyViewModel.sendTX("$" + curCorButton.getCorDir() + corValue + "&");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);


        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            curCorButton = corButton;
            if (curCorButton != null) curCorButId = curCorButton.getId();

        });

        buttonsViewModel.getCompCorValue().observe(getActivity(), integer -> {
            if (integer != null) compValue = integer;
        });


        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    setLayout.setVisibility(View.VISIBLE);
                    //CorButton newcurCorButton = buttonsViewModel.getCorButtonById(curCorButId);
                    // CorButtonById = appDatabase.buttonsDao().getItemById(curCorButId);
                    etButName.setText(curCorButton.getButNum());
                    //buttonsViewModel.setmSetButton(false);
                    firstOpenSet = true;
                }


                seekBar.setProgress(curCorButton.getCorValue());
            } else {

                setLayout.setVisibility(View.GONE);
            }

            //  Log.d("myLogs", "название: " + curCorButton.getButNum() + ", corValue: " + curCorButton.getCorValue() + ", dirCor: " + curCorButton.getCorDir() + ", compValue: " + curCorButton.getCompValue());
            if (curCorButton.getCompValue() != 0) cbComp.setChecked(true);
            else cbComp.setChecked(false);
            switch (curCorButton.getCorDir()) {
                case "+":
                    rbPlus.setChecked(true);
                    break;

                case "-":
                    rbMinus.setChecked(true);
                    break;

                case "p":
                    rbPercent.setChecked(true);
                    break;

            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnClose:
                buttonsViewModel.setmSetButton(false);
                setLayout.setVisibility(View.GONE);
                resetCor();
                break;
            case R.id.btnSave:
                butName = etButName.getText().toString();
                corValue = Integer.valueOf(etCorValue.getText().toString());
                buttonsViewModel.addCorBut(new CorButton(
                        curCorButton.getId(), butName, dirCor, corValue, compValue
                ));
                setLayout.setVisibility(View.GONE);
                resetCor();

            case R.id.btnInc:
                corValue++;
                seekBar.setProgress(corValue);
                break;

            case R.id.btnDec:
                corValue--;
                seekBar.setProgress(corValue);
                break;

        }
    }
}
