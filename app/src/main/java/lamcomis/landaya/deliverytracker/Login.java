package lamcomis.landaya.deliverytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import lamcomis.landaya.deliverytracker.Admin.AdminActivity;
import lamcomis.landaya.deliverytracker.Driver.DriverAcrivity;
import lamcomis.landaya.deliverytracker.Global.InternetConnection;
import lamcomis.landaya.deliverytracker.Global.PromptActivity;
import lamcomis.landaya.deliverytracker.Global.SessionManager;
import lamcomis.landaya.deliverytracker.Global.URL;

public class Login extends AppCompatActivity {
    String LOGIN = URL.url+"login.php";
    String USERID = "driver_username";
    String PASSWORD = "driver_password";
    SessionManager sessionManager;
    EditText editTextUserID, editTextPassword;
    ProgressDialog pd;
    Button btn_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("url", LOGIN);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in...");
        sessionManager = new SessionManager(this);
        editTextUserID = findViewById(R.id.driverID);
        editTextPassword = findViewById(R.id.password);
        btn_log = findViewById(R.id.login);
        btn_log.setClickable(true);
        if(sessionManager.isLoggedIn()){
            HashMap<String, String> user = sessionManager.getUserDetails();
            String user_type = user.get(SessionManager.RESPONSE);
            if (user_type.equals("driver")){
                Intent i = new Intent(Login.this, DriverAcrivity.class);
                startActivity(i);
            }
            else{
                Intent i = new Intent(Login.this, AdminActivity.class);
                startActivity(i);
            }
        }
        else{
            btn_log.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        btn_log.setClickable(false);
                        pd.show();
                        if(InternetConnection.checkConnection(Login.this)){

                            login();
                            if(editTextUserID.getText().length()==0){
                                PromptActivity.showAlert(Login.this, "empty");
                                pd.dismiss();
                                btn_log.setClickable(true);

                            }
                            else {
                                login();
                            }

                        }
                        else{
                            PromptActivity.showAlert(Login.this, "internet");
                            pd.dismiss();
                            btn_log.setClickable(true);
                        }
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private void login() {

        String user_id = editTextUserID.getText().toString();
        String password = editTextPassword.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        if (response.trim().contains("driver")) {
                            pd.dismiss();
                            btn_log.setClickable(true);
                            String user_type = "driver";
                            sessionManager.createLoginSession(user_id, password, user_type);
                            Intent i = new Intent(Login.this, DriverAcrivity.class);
                            startActivity(i);

                        }
                        else if (response.trim().contains("admin")) {
                            pd.dismiss();
                            btn_log.setClickable(true);
                            String user_type = "admin";
                            sessionManager.createLoginSession(user_id, password, user_type);
                            Intent i = new Intent(Login.this, AdminActivity.class);
                            startActivity(i);

                        }
                        else if (response.trim().contains("invalid_password")) {
                            pd.dismiss();
                            btn_log.setClickable(true);
                            PromptActivity.showAlert(Login.this, "invalid_password");
                        }
                        else if (response.trim().contains("not_found")) {
                            pd.dismiss();
                            btn_log.setClickable(true);
                            PromptActivity.showAlert(Login.this, "not_found");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        btn_log.setClickable(true);
                        PromptActivity.showAlert(Login.this, "error");
                        Log.d("error", error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(USERID, user_id);
                map.put(PASSWORD, password);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        int socketTimeout = URL.global_timeout;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        PromptActivity.showAlert(Login.this, "back");
    }
}
