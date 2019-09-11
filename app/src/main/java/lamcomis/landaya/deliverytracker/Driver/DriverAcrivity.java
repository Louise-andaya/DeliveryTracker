package lamcomis.landaya.deliverytracker.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lamcomis.landaya.deliverytracker.Adapter.DriverAdapter;
import lamcomis.landaya.deliverytracker.Admin.AdminActivity;
import lamcomis.landaya.deliverytracker.Global.DatabaseHelper;
import lamcomis.landaya.deliverytracker.Global.InternetConnection;
import lamcomis.landaya.deliverytracker.Global.PromptActivity;
import lamcomis.landaya.deliverytracker.Global.SessionManager;
import lamcomis.landaya.deliverytracker.Global.URL;
import lamcomis.landaya.deliverytracker.List.DriverDataList;
import lamcomis.landaya.deliverytracker.R;

public class DriverAcrivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    static String LOAD_DATA = URL.url+"alldata.php";
    static String KEY_USERID = "user_id";
    String SEND_DATA = URL.url+"insertdata.php";
    public static final String DRIVER = "driver";
    public static final String SI_NUMBER = "si_number";
    public static final String DTR_NUMBER = "dtr_number";
    public static final String RECEIPT = "receipt";
    public static final String SIGNATURE = "signature";
    public static final String DATE_ADDED = "date_added";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";
    public static final String KEY_Address = "address";
    public static final String KEY_firstman = "firstman";
    public static final String KEY_secondman = "secondman";
    public static final String KEY_customer_name = "customer_name";
    static DatabaseHelper db;
    SessionManager sessionManager;
    static List<DriverDataList> dataLists;
    static RecyclerView recyclerView;
    static SwipeRefreshLayout swipeRefreshLayout;
    static String driver;
    String dateToday;
    static Cursor res;
    static ProgressDialog pd;
    public static FloatingActionButton btn_send;
    static DriverAdapter adapter;
    public static ImageView logout;
    public static TextView sel_no;

    String  send_driver, send_si, send_dr, send_receipt, send_sign, send_date, send_lat,
    send_long, send_address, send_firstman, send_secondman, send_customer;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dashboard);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Data");
        pd.show();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
        dateToday = dateFormat.format(today);
        dataLists = new ArrayList<>();
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        driver = user.get(SessionManager.KEY_USERID);
        recyclerView = findViewById(R.id.recycleViewContainer);
        btn_send = (FloatingActionButton)findViewById(R.id.btn_send);
        btn_send.setVisibility(View.GONE);
        logout = findViewById(R.id.logout);
        logout.setClickable(true);
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    PromptActivity.showAlert(DriverAcrivity.this, "logout");
                    return true;
                }
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataLists.isEmpty()){
                    Toast.makeText(DriverAcrivity.this, "No Pending Call Approval", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd.show();
                    getSelected();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
            }
        });
        res = db.getCount();
        res.moveToFirst();
        int count = res.getInt(0);
        if(count >0){
            res = db.getDelivery(dateToday);
            if(res.moveToFirst()){
                pd.dismiss();
                getOfflineData(DriverAcrivity.this);
            }
            else{
                PromptActivity.showAlert(DriverAcrivity.this, "update_data");
                pd.dismiss();
            }

        }
        else{
            PromptActivity.showAlert(DriverAcrivity.this, "empty_data");
            pd.dismiss();
        }
    }

    private void getSelected() {
        if (adapter.getSelected().size() > 0) {
            ArrayList<String> selected_ca = new ArrayList<>();
            for (int i = 0; i < adapter.getSelected().size(); i++) {
                selected_ca.add(adapter.getSelected().get(i).getCustomer_name());
            }

            String si="";
            String[] stats = new String[selected_ca.size()];
            stats = selected_ca.toArray(stats);
            //String[] strList = time.split(",");// here give an error at **time.split(",")**
            for(String splstr: stats )
            {
                si=splstr.toString();
                Log.d("test", si);
             getSaveData(si);
            }

        } else {
            Toast.makeText(this, "try", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSaveData(String si) {
          res = db.getSaveDel(si, "SVD");
        if(res.moveToFirst()){
            do {
                send_driver = res.getString(res.getColumnIndex("Driver"));
                send_si = res.getString(res.getColumnIndex("Si_number"));
                send_dr = res.getString(res.getColumnIndex("Dtr_number"));
                send_receipt = Objects.toString(res.getString(res.getColumnIndex("Receipt")), "None").toString();
                send_sign = res.getString(res.getColumnIndex("Signature"));
                send_date = res.getString(res.getColumnIndex("Date_added"));
                send_lat = Objects.toString(res.getString(res.getColumnIndex("Latitude")), "None").toString();
                send_long = Objects.toString(res.getString(res.getColumnIndex("Longitude")), "None").toString();
                send_address = Objects.toString(res.getString(res.getColumnIndex("Address")), "None").toString();
                send_firstman = Objects.toString(res.getString(res.getColumnIndex("First_man")), "None").toString();
                send_secondman = Objects.toString(res.getString(res.getColumnIndex("Second_man")), "None").toString();
                send_customer = res.getString(res.getColumnIndex("Customer_name"));
                if(InternetConnection.checkConnection(DriverAcrivity.this)) {
                    SendData(send_driver,send_si, send_dr, send_receipt, send_sign, send_date, send_lat,
                            send_long, send_address, send_firstman, send_secondman, send_customer);
                }
                else{
                    PromptActivity.showAlert(DriverAcrivity.this, "internet");
                }
            }
            while (res.moveToNext());

        }

    }



    private void SendData(String send_driver, String send_si, String send_dr, String send_receipt, String send_sign, String send_date,
                          String send_lat, String send_long, String send_address, String send_firstman, String send_secondman, String send_customer) {

        Log.d("String", send_driver +"\n"+send_si+"\n" +send_dr+"\n" +send_receipt+"\n" +send_sign+"\n"
                +"\n"+send_date+"\n" +send_lat+"\n"
                +send_long+"\n" +send_address+"\n" +send_firstman+"\n" +send_secondman+"\n" +send_customer);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                if (response.contains("success")) {
                    pd.dismiss();
                    PromptActivity.showAlert(DriverAcrivity.this, "send_sever_success");
                    db.deletePending(send_customer, "SVD");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("error", error.toString());
                   PromptActivity.showAlert(DriverAcrivity.this, "error");


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(SI_NUMBER, send_si);
                map.put(DRIVER, send_driver);
                map.put(KEY_firstman, send_firstman);
                map.put(KEY_secondman, send_secondman);
                map.put(DTR_NUMBER, send_dr);
                map.put(RECEIPT, send_receipt);
                map.put(SIGNATURE, send_sign);
                map.put(DATE_ADDED, send_date);
                map.put(KEY_LAT, send_lat);
                map.put(KEY_LONG, send_long);
                map.put(KEY_Address, send_address);
                map.put(KEY_customer_name, send_customer);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    //OFFLINE DATA
    public static void getOfflineData(Context context) {
        res = db.getAllData();
        if(res.moveToFirst()){
            do {
                DriverDataList dataList = new DriverDataList();
                dataList.setCustomer_name(res.getString(res.getColumnIndex("Customer_name")));
                dataList.setDr_number(res.getString(res.getColumnIndex("Dtr_number")));
                dataList.setContact_person(res.getString(res.getColumnIndex("Contact_person")));
                dataList.setFirst_man(res.getString(res.getColumnIndex("First_man")));
                dataList.setSecond_man(res.getString(res.getColumnIndex("Second_man")));
                dataList.setStatus(res.getString(res.getColumnIndex("Status")));
                dataLists.add(dataList);

            }while (res.moveToNext());
            SetUpAdapter(context);
        }
    }

    //SAVE DATA
    public static void SaveData(Context context) {
        pd.setMessage("Syncing Data, Please Wait...");
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                LOAD_DATA, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d("string", response);
                if(response.contains("[null]")){
                    PromptActivity.showAlert(context, "no_data");
                }
                else {
                    try {
                        JSONArray jsonarray = new JSONArray(response);
                        boolean insert = false;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject json = jsonarray.getJSONObject(i);

                            String customer_name = json.getString("customer_name").trim();
                            String customer_code = json.getString("customer_code").trim();
                            String contact_person = json.getString("contact_person").trim();
                            String si_number = json.getString("si_number").trim();
                            String dtr_number = json.getString("dtr_number").trim();
                            String remarks = json.getString("remarks").trim();
                            String first_man = json.getString("first_man").trim();
                            String second_man = json.getString("second_man").trim();
                            String dtr_date = json.getString("dtr_date").trim();
                            String status = json.getString("status").trim();

                            insert = db.insertData(si_number, driver, customer_name, contact_person, customer_code, dtr_number, remarks, first_man, second_man, dtr_date, status,
                                    "", "", "", "", "", "");
                        }
                        if (insert == true) {
                            PromptActivity.showAlert(context, "save_success");
                            pd.dismiss();

                        } else {
                            PromptActivity.showAlert(context, "saving_failed");
                            pd.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("error", e.toString());
                        pd.dismiss();
                    }
                }



            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        PromptActivity.showAlert(context, "error");
                        pd.dismiss();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERID, driver);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    private static void SetUpAdapter(Context context) {
        adapter = new DriverAdapter(context, dataLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onRefresh() {

    }

    @Override
    public void onBackPressed() {
        PromptActivity.showAlert(DriverAcrivity.this, "back");
    }
}
