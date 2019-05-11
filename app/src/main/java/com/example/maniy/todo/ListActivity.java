package com.example.maniy.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class ListActivity extends AppCompatActivity {

    private Dialog dialog;
    private Context context = this;
    private String taskName,taskDate,priority = "0";
    private Button cancel,add;
    private EditText taskname,taskdate;
    private List<Tasks> tasksList;
    private RecyclerView recyclerView;
    private CheckBox checkBox;
    private Snackbar snackbar;
    private  ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;
    private String SHARED_PREF_NAME = "Manikandan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        // instantiating widgets
        dialog = new Dialog(context);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coordinatorLayout = findViewById(R.id.coordinatelayout);

        //populate data
        tasksList = new ArrayList<>();
        loadTasklist();



//        floating button click
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setContentView(R.layout.add_task);
                taskname = dialog.findViewById(R.id.add_task_name);
                taskdate = dialog.findViewById(R.id.add_task_date);
                checkBox = dialog.findViewById(R.id.priority);
                add = dialog.findViewById(R.id.add_task);
                cancel = dialog.findViewById(R.id.cancel);


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(context, "Clicked add button", Toast.LENGTH_SHORT).show();
                        insertTaskData();
                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        finish();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void insertTaskData() {
        progressDialog.show();
        final String taskName = taskname.getText().toString().trim();
        final String taskDate = taskdate.getText().toString().trim();
        String imp = "0";
        if(checkBox.isChecked()){
             imp = "1";
        }
        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        final String userPK = sp.getString("USER_PK", null);
        final String priority = imp;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.INSERT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    progressDialog.dismiss();
                    JSONObject object = new JSONObject(response);
                    snackbar = Snackbar.make(coordinatorLayout, object.getString("msg"), snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                            loadTasklist();
                        }
                    });
                    snackbar.show();
                    Log.e("RESPONSE",response);
                    String status = object.getString("status");
                    String msg = object.getString("msg");
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_pk", userPK);
                params.put("list_name",taskName);
                params.put("list_date",taskDate);
                params.put("ls_priority",priority);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void loadTasklist() {
        progressDialog.show();
        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        final String userPK = sp.getString("USER_PK", null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.READ_URL + userPK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    //getting whole json object from the response
                    JSONObject obj = new JSONObject(response);
                    Log.e("OutPut", obj.toString());
                    Log.e("status",obj.getString("status"));
                    //getting the data array inside the response
                    JSONArray dataArray = obj.getJSONArray("data");
                    if(obj.getInt("status") == 0)  {
                        Toast.makeText(context, "No Tasks Found", Toast.LENGTH_SHORT).show();
                        exit(0);
                    }
                    //loop through the array to get all the values
                    for(int i = 0;i < dataArray.length(); i++){
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        //adding the array data to the object file's constructor
                        Tasks tasks = new Tasks(dataObj.getString("task_name"),dataObj.getString("task_date"),dataObj.getString("task_priority"));
                        tasksList.add(tasks);
                    }
                    TaskAdapter taskAdapter = new TaskAdapter(getApplicationContext(), tasksList);
                    recyclerView.setAdapter(taskAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //displaying the error in toast if occurs
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("user_pk", userPK);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
