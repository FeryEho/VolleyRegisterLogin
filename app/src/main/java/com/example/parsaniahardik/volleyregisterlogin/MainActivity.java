package com.example.parsaniahardik.volleyregisterlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etname, ethobby, etusername, etpassword;
    private Button btnregister;
    private TextView tvlogin;

    private PreferenceHelper preferenceHelper;
    private String registerURL = "http://192.168.27.57/rental/register.php";
    private RequestQueue rQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceHelper = new PreferenceHelper(this);

        if(preferenceHelper.getIsLogin()){
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }

        etname = (EditText) findViewById(R.id.etname);
        ethobby = (EditText) findViewById(R.id.ethobby);
        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);

        btnregister = (Button) findViewById(R.id.btn);
        tvlogin = (TextView) findViewById(R.id.tvlogin);

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMe();
            }
        });

    }

    private void registerMe(){

        final String name = etname.getText().toString();
        final String hobby = ethobby.getText().toString();
        final String username = etusername.getText().toString();
        final String password = etpassword.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        rQueue.getCache().clear();
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();

                        try {

                            parseData(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("hobby",hobby);
                params.put("username",username);
                params.put("password",password);

                return params;
            }

        };

        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(stringRequest);
    }

    private void parseData(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.optString("status").equals("true")){

            saveInfo(response);

            Toast.makeText(MainActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }else {

            Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfo(String response){

        preferenceHelper.putIsLogin(true);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putName(dataobj.getString("name"));
                    preferenceHelper.putHobby(dataobj.getString("hobby"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
