package com.davidh.davidh.clinicapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    public EditText username, lastname, email, identification, password, passwordRepeat;
    public Spinner typeUser;
    public TextView txvGgoToLogin;
    private Map<String, String> typeUserMap;
    Button btnRegister;
    String typeUserValue;
    JsonObjectRequest jsonRequest;

    private RequestQueue req;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);


        initUi();
        initActions();
        addItemsToSpinderTypeUser();
    }

    public void initUi(){
        username = (EditText) findViewById(R.id.et_register_names);
        lastname = (EditText) findViewById(R.id.et_register_lastnames);
        email = (EditText) findViewById(R.id.et_register_email);
        identification = (EditText) findViewById(R.id.et_register_identification);
        password = (EditText) findViewById(R.id.et_register_password);
        passwordRepeat = (EditText) findViewById(R.id.et_register_password_repeat);
        typeUser = (Spinner) findViewById(R.id.reisterSpinnerTypeUser);
        txvGgoToLogin = (TextView) findViewById(R.id.txv_goto_login);
        btnRegister = (Button) findViewById(R.id.btn_register);

        typeUserMap = new HashMap<>();
        typeUserMap.put("Médico", "medic");
        typeUserMap.put("Paciente", "patient");

        req = Volley.newRequestQueue(this);

        Log.d("UI", txvGgoToLogin.toString());
    }

    public void initActions(){
        txvGgoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        typeUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeUserValue = typeUser.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void registerUser() {
        if(validateRegister()){
            Map<String, String> dataToRegister = new HashMap<>();
            dataToRegister.put("username", username.getText().toString());
            dataToRegister.put("lastname", lastname.getText().toString());
            dataToRegister.put("typeUser", typeUserMap.get(typeUserValue));
            dataToRegister.put("identification", identification.getText().toString());
            dataToRegister.put("email", email.getText().toString());
            dataToRegister.put("password", password.getText().toString());

            Log.d("REGISTERING", dataToRegister.toString());

            makeRequest(dataToRegister, ConfigUrl.registerUserInApp, Request.Method.POST);
        }else{

        }
    }



    private void addItemsToSpinderTypeUser(){
        List <String> listTypeUser = new ArrayList<String>();
        listTypeUser.add("Paciente");
        listTypeUser.add("Médico");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Register.this,R.layout.support_simple_spinner_dropdown_item, listTypeUser);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeUser.setAdapter(dataAdapter);
    }

    private void goToLogin(){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        finish();
        startActivity(intent);
    }

    private Boolean validateRegister(){
        if(username.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese su nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(lastname.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese sus apellidos", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(identification.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese su identificación", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(email.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese su correo electrónico", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else if(password.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese su contraseña", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(passwordRepeat.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.container_register), "Ingrese de nuevo su contraseña", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(!password.getText().toString().equals(passwordRepeat.getText().toString())){
            Snackbar.make(findViewById(R.id.container_register), "Las contraseñas deben coincidir", Snackbar.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void makeRequest(Map<String, String> body, String url, int method){
        final ProgressDialog pd = ProgressDialog.show(this, "Registrando el nuevo usuario", "Esperando respuesta");

        req.start();
        jsonRequest = new JsonObjectRequest(
                method,
                url,
                new JSONObject(body),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try{
                            Log.d("AFTERREGISTER", response.toString());

                            pd.dismiss();

                            Snackbar.make(findViewById(R.id.container_register), response.getString("message"), Snackbar.LENGTH_SHORT).show();

                            /*if(response.get("status").equals("success") && response.getBoolean("existUser")){
                                Snackbar.make(findViewById(R.id.container_register), response.getString("message"), Snackbar.LENGTH_SHORT).show();
                            }else*/
                            if(response.get("status").equals("success") && response.getBoolean("userRegistered")){
                                goToLogin();
                            }

                            req.stop();
                        }catch(Exception error){
                            Log.d("onError", error.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onError", error.getMessage());
                        req.stop();
                    }
                }
        );

        req.add(jsonRequest);
    }
}
