package com.hillelevo.cityelf.fragments;

import static com.hillelevo.cityelf.Constants.TAG;

import com.hillelevo.cityelf.Constants.Prefs;
import com.hillelevo.cityelf.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BottomDialogFragment extends DialogFragment implements OnClickListener {

  private boolean isRegistered;

  private Spinner spinner;

  private SharedPreferences settings;

  /**
   * Create a new instance of MyDialogFragment
   */
  public static BottomDialogFragment newInstance(boolean reg) {
    BottomDialogFragment f = new BottomDialogFragment();

    // Supply num input as an argument.
    Bundle args = new Bundle();
    args.putBoolean("registered", reg);
    f.setArguments(args);

    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    isRegistered = getArguments().getBoolean("registered");
    Log.d(TAG, "Dialog onCreate: isRegistered = " + isRegistered);

    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_bottom_dialog, container, true);

      getDialog().getWindow().setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM);
      getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

    TextView textViewTitle = (TextView) v.findViewById(R.id.textViewDialogTitle);
    RadioButton radioButtonElectricity =
        (RadioButton) v.findViewById(R.id.radioButtonDialogElectricity);
    RadioButton radioButtonGas = (RadioButton) v.findViewById(R.id.radioButtonDialogGas);
    RadioButton radioButtonWater = (RadioButton) v.findViewById(R.id.radioButtonDialogWater);
    Button buttonReport = (Button) v.findViewById(R.id.buttonDialogReport);
    Button buttonRegister = (Button) v.findViewById(R.id.buttonDialogRegister);
    Button buttonLogin = (Button) v.findViewById(R.id.buttonDialogLogin);
    spinner = (Spinner) v.findViewById(R.id.spinnerDialog);

    // Change fragment elements - registered and non-registered variants

    if (!isRegistered) {
      buttonLogin.setVisibility(View.VISIBLE);
      buttonRegister.setVisibility(View.VISIBLE);
      buttonReport.setVisibility(View.GONE);
      textViewTitle.setVisibility(View.GONE);
      radioButtonElectricity.setVisibility(View.GONE);
      radioButtonGas.setVisibility(View.GONE);
      radioButtonWater.setVisibility(View.GONE);
      spinner.setVisibility(View.GONE);
    }

    radioButtonElectricity.setOnClickListener(this);
    radioButtonGas.setOnClickListener(this);
    radioButtonWater.setOnClickListener(this);
    buttonReport.setOnClickListener(this);
    buttonRegister.setOnClickListener(this);

    // Radio buttons click init
    RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupDialog);

    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      public void onCheckedChanged(RadioGroup group, int checkedId) {

        // checkedId is the RadioButton selected
        switch (checkedId) {
          case R.id.radioButtonDialogElectricity:
            Log.d(TAG, "onCheckedChanged: radioButtonDialogElectricity");
            break;
          case R.id.radioButtonDialogGas:
            Log.d(TAG, "onCheckedChanged: radioButtonDialogGas");
            break;
          case R.id.radioButtonDialogWater:
            Log.d(TAG, "onCheckedChanged: radioButtonDialogWater");
            break;
        }
      }
    });

    // init Shared Prefs
    settings = getActivity().getSharedPreferences(Prefs.APP_PREFERENCES, Context.MODE_PRIVATE);

    // Add data to Spinner
    List<String> addressList = new ArrayList<>();
    String address;

    for (int i = 1; i < Prefs.MAX_ADDRESS_QUANTITY; i++) {
      address = loadFromSharedPrefs(Prefs.ADDRESS + i);
      if (!address.equals("Error")) {
        Log.d(TAG, "Dialog add to Spinner in Dialog: " + address);
        addressList.add(address);
      }
    }

    ArrayAdapter<String> adapter;

    adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
        R.layout.spinner_item, addressList);
    adapter.setDropDownViewResource(R.layout.spinner_item);
    spinner.setAdapter(adapter);

    return v;
  }

  /**
   * Handle clicks on dialog elements to send data to server, etc
   */
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.buttonDialogReport:
        Toast.makeText(getActivity(), "Dialog Report clicked", Toast.LENGTH_LONG).show();
        break;

      case R.id.buttonDialogLogin:
        Toast.makeText(getActivity(), "Dialog Login clicked", Toast.LENGTH_LONG).show();
        break;

      case R.id.buttonDialogRegister:
        Toast.makeText(getActivity(), "Dialog Register clicked", Toast.LENGTH_LONG).show();
        break;
    }
  }

  //Load data from Shared Prefs

  private String loadFromSharedPrefs(String id) {
    //Check for data by id
    if (settings != null && settings.contains(id)) {
      Log.d(TAG, "Dialog mSettings != null, contains " + id);
      return settings.getString(id, "Error");
    } else {
      Log.d(TAG, "Dialog mSettings != null, not contains " + id);
      return "Error";
    }
  }
}