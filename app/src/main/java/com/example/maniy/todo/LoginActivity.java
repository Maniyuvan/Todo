package com.example.maniy.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LoginActivity extends AppCompatActivity {


    private EditText username,password;
    private Button loginBtn;
    private TextView newUser, error;
    private ProgressDialog progressDialog;

    private static final String SHARED_PREF_NAME = "Manikandan";
    private static final String USERNAME = "";
    private static final String EMAIL = "";
    private static final String KEY_USER_PK = "";
    private static final String PHONE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("IS_LOGGEDIN", false);
        if (isLogin) {
            finish();
            startActivity(new Intent(this, ListActivity.class));
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging you in...");
        username = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginBtn);
        newUser = findViewById(R.id.newUser);
        error = findViewById(R.id.error);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateDetails())
                    loginUser();
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        progressDialog.show();
        final String userName = username.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response",response);
                    int status = jsonObject.getInt("status");
                    String errorMsg = jsonObject.getString("msg");
                    if (status == 1) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        String user_pk = dataObject.getString("usrmst_pk");
                        String userName = dataObject.getString("um_name");
                        String userEmail = dataObject.getString("um_email");
                        String userPhone = dataObject.getString("um_phone");
                        //storing the user in shared preferences
                        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("USER_PK", user_pk);
                        editor.putString("NAME", userName);
                        editor.putString("EMAIL", userEmail);
                        editor.putString("PHONE", userPhone);
                        editor.putBoolean("IS_LOGGEDIN", true);
                        editor.apply();

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), ListActivity.class));
                    }else{
                        error.setText(errorMsg);
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("OUTPUT",e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userName);
                params.put("password",userPassword);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private boolean validateDetails() {
        boolean valid = true;
        String vname = username.getText().toString().trim();
        String vpassword = password.getText().toString().trim();


        if (TextUtils.isEmpty(vname)) {
            username.setError("Enter email ID");
            username.requestFocus();
            valid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(vname).matches()) {
            username.setError("Enter valid email ID");
            username.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(vpassword)) {
            password.setError("Enter password");
            password.requestFocus();
            valid = false;
        }
        return valid;
    }
}
