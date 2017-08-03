package com.hillelevo.cityelf.fragments.auth_fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.hillelevo.cityelf.Constants;
import com.hillelevo.cityelf.Constants.Prefs;
import com.hillelevo.cityelf.Constants.WebUrls;
import com.hillelevo.cityelf.R;
import com.hillelevo.cityelf.activities.MainActivity;
import com.hillelevo.cityelf.data.UserLocalStore;
import com.hillelevo.cityelf.webutils.JsonMessageTask;
import com.hillelevo.cityelf.webutils.JsonMessageTask.JsonMessageResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment implements JsonMessageResponse, OnClickListener {

  EditText etLogEmail, etLogPassword;
  TextView tvRegisteraitUser;
  Button btnLogin;
  OnRegistrationClickListener listner;

  private String password = null;

  UserLocalStore userLocalStore;
  String responseMessage;


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    listner = (OnRegistrationClickListener) activity;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.layout_fragment_login, container, false);

    etLogEmail = (EditText) view.findViewById(R.id.etLogEmail);
    etLogPassword = (EditText) view.findViewById(R.id.etLogPassword);
    tvRegisteraitUser = (TextView) view.findViewById(R.id.tvRegisteraitUser);
    btnLogin = (Button) view.findViewById(R.id.btnLogin);

    btnLogin.setOnClickListener(this);
    tvRegisteraitUser.setOnClickListener(this);

    return view;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnLogin:

        String email = etLogEmail.getText().toString();
        password = etLogPassword.getText().toString();

        String bodyParams = "email=" + email + "&password=" + password;

        if (!email.isEmpty() && !password.isEmpty()) {

            new JsonMessageTask(LoginFragment.this).execute(WebUrls.AUTHORIZATION_URL, Constants.POST, bodyParams);
//          new JsonMessageTask(this).execute(WebUrls.AUTHORIZATION_URL, Constants.POST, returnedUser.getAuthCertificate());
          break;
        } else if (email.equals("")) {
          Toast.makeText(getContext(), "Введите email", Toast.LENGTH_SHORT).show();
          break;
        } else if (password.equals("")) {
          Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
          break;
        }


      case R.id.tvRegisteraitUser:
        listner.onRegistrationClick();
        break;
    }
  }

  @Override
  public void messageResponse(String output) {
    checkResponse(output);
  }

  public void checkResponse(String output){
    if (output == null || output.isEmpty()) {

      showMessage("LoggedIn failed");
    } else {try {
      JSONObject jsonObject = new JSONObject(output);
      if (jsonObject != null) {

        JSONObject statusJsonObject = jsonObject.getJSONObject("status");

        int code = statusJsonObject.getInt("code");
        String message = statusJsonObject.getString("message");

        showMessage(message);

        if (code == 33 && message.equals("Your login and password is correct")) {

          JSONObject userJsonObject = jsonObject.getJSONObject("user");
//          TODO: set id in User and UserLocalStore
          int id = userJsonObject.getInt("id");
          String email = userJsonObject.getString("email");
          int phone = userJsonObject.getInt("phone");

          JSONArray addressJsonArray = (JSONArray) userJsonObject.get("addresses");
          JSONObject addressJsonObject = addressJsonArray.getJSONObject(0);
          String address = addressJsonObject.getString("address");

          authenticate(email, address, password);

        }else{
          showMessage(message);
        }
      } else {
        showMessage("Incorrect user details");
      }


    } catch (JSONException e) {
      e.printStackTrace();
    }

    }

  }

  private void authenticate(String email, String address, String password) {

    showMessage("All OK");

    UserLocalStore.saveStringToSharedPrefs(getActivity().getApplicationContext(), Prefs.EMAIL,
        email);
    UserLocalStore.saveStringToSharedPrefs(getActivity().getApplicationContext(), Prefs.ADDRESS_1,
        address);
    UserLocalStore.saveStringToSharedPrefs(getActivity().getApplicationContext(), Prefs.PASSWORD,
        password);
    UserLocalStore.saveBooleanToSharedPrefs(getActivity().getApplicationContext(), Prefs.REGISTERED,
        true);

    Intent intent = new Intent(getContext(), MainActivity.class);
    LoginFragment.this.startActivity(intent);
  }

  private void showMessage(String message) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
    dialogBuilder.setMessage(message);
    dialogBuilder.setPositiveButton("Ok", null);
    dialogBuilder.show();
  }


  public interface OnRegistrationClickListener {

    void onRegistrationClick();
  }
}
