package com.example.maniy.todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private TextView newUser;
    private ProgressDialog progressDialog;
//    private static final String login_url = "https://todo-mani.000webhostapp.com//Todo1/web/index.php?r=login/login";
    private static final String login_url = "https://todo-mani.000webhostapp.com/todo/login.php";
    private String SHARED_PREF_NAME = "userDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        Boolean isLoggedin = sp.getBoolean("IS_LOGGEDIN",false);
        if(isLoggedin) {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging you in...");
        username = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginBtn);
        newUser = findViewById(R.id.newUser);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.hide();
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    Log.e("Response",response);
                    String status = jsonObject.getString("status");
                    JSONObject dataObject= jsonObject.getJSONObject("data");
                    String user_pk = dataObject.getString("usrmst_pk");
                    String userName = dataObject.getString("um_name");
                    String userEmail = dataObject.getString("um_email");
                    String userPhone = dataObject.getString("um_phone");

                    SharedPreferences sp =  getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("USER_PK",user_pk);
                    editor.putString("NAME",userName);
                    editor.putString("EMAIL",userEmail);
                    editor.putString("PHONE",userPhone);
                    editor.putBoolean("IS_LOGGEDIN",true);
                    editor.apply();
                    if(status.equals("1")){
                        Intent i = new Intent(LoginActivity.this,ListActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else{
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
                Log.e("Error",error.toString());
            }
        }){
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
}
