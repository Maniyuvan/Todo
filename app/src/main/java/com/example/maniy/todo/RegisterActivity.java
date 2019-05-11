package com.example.maniy.todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    private EditText username,email,phone,password;
    private ProgressDialog progressDialog;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.userNameRegister);
        email = findViewById(R.id.emailRegister);
        phone = findViewById(R.id.phoneRegister);
        password = findViewById(R.id.passwordRegister);
        TextView existingUser = findViewById(R.id.existingUser);
        coordinatorLayout = findViewById(R.id.register_layout);
        Button registerBtn = findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering...");


        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateDetails())
                    registerUser();
            }
        });
    }

    private void registerUser() {

        progressDialog.show();
        final String userName = username.getText().toString().trim();
        final String userEmail = email.getText().toString().trim();
        final String userPhone = phone.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    if(status.equals("1")){
                        snackbar = Snackbar.make(coordinatorLayout, "Registered Successfully", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                startActivity(new Intent(RegisterActivity.this, ListActivity.class));
                            }
                        });
                        snackbar.show();
                    }else {
                        snackbar = Snackbar.make(coordinatorLayout, "Something went wrong", Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                startActivity(new Intent(RegisterActivity.this, ListActivity.class));
                            }
                        });
                        snackbar.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    snackbar = Snackbar.make(coordinatorLayout, error.toString(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uname",userName);
                params.put("email",userEmail);
                params.put("phone",userPhone);
                params.put("password",userPassword);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean validateDetails() {
        Boolean valid = true;
        String vname = username.getText().toString().trim();
        String vemail = email.getText().toString().trim();
        String vphone = phone.getText().toString().trim();
        String vpassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(vname)) {
            username.setError("Enter username");
            username.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(vemail)) {
            email.setError("Enter email ID");
            email.requestFocus();
            valid = false;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(vemail).matches()) {
            email.setError("Enter valid email ID");
            email.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(vphone)) {
            phone.setError("Enter phone number");
            phone.requestFocus();
            valid = false;
        }

        if (TextUtils.isDigitsOnly(vphone) || vphone.length() != 10) {
            phone.setError("Enter valid phone number");
            phone.requestFocus();
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
