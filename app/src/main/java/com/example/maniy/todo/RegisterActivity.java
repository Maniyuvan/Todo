package com.example.maniy.todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private static final String register_url = "https://todo-mani.000webhostapp.com/todo/register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.userNameRegister);
        email = findViewById(R.id.emailRegister);
        phone = findViewById(R.id.phoneRegister);
        password = findViewById(R.id.passwordRegister);
        TextView existingUser = findViewById(R.id.existingUser);
        Button registerBtn = findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pleast wait");
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, register_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.hide();
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    if(status.equals("1")){
                        startActivity(new Intent(RegisterActivity.this,ListActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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
}
