package lamcomis.landaya.deliverytracker.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;

import lamcomis.landaya.deliverytracker.Adapter.DelTodayAdapter;
import lamcomis.landaya.deliverytracker.Global.InternetConnection;
import lamcomis.landaya.deliverytracker.Global.PromptActivity;
import lamcomis.landaya.deliverytracker.Global.URL;
import lamcomis.landaya.deliverytracker.List.DelForTodayList;
import lamcomis.landaya.deliverytracker.R;

public class DeliveryForToday extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String DEL_TODAY = URL.url+"product.php";
    List<DelForTodayList>todayLists;
    RecyclerView recyclerView;
    ProgressDialog pd;
    RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

   public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_delivery_for_today, viewGroup, false);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please Wait....");
        recyclerView = view.findViewById(R.id.today_recycleViewContainer);
        todayLists = new ArrayList<>();
       swipeRefreshLayout = view.findViewById(R.id.today_swipe_container);
       swipeRefreshLayout.setOnRefreshListener(this);
       swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
               android.R.color.holo_green_dark,
               android.R.color.holo_orange_dark,
               android.R.color.holo_blue_dark);
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               FragmentTransaction ft = getFragmentManager().beginTransaction();
               ft.detach(DeliveryForToday.this).attach(DeliveryForToday.this).commit();
           }
       });
        if (InternetConnection.checkConnection(getActivity())){
            getToday();
            pd.show();
        }
        else{
            PromptActivity.showAlert(getActivity(), "internet");
            pd.dismiss();
        }
        return view;

   }

    private void getToday() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DEL_TODAY, new Response.Listener<String>() {


            public void onResponse(String response) {
                try {
                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonObject = jsonarray.getJSONObject(i);
                        DelForTodayList list = new DelForTodayList();
                        list.setToday_driver(jsonObject.getString("driver_name"));
                        list.setToday_customer(jsonObject.getString("customer_name"));
                        list.setToday_contact(jsonObject.getString("contact_person"));
                       // list.setToday_dtr(jsonObject.getString("dtr_no"));
                        todayLists.add(list);
                    }

                    SetUpAdapter();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }




        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        PromptActivity.showAlert(getActivity(), "error");
                        pd.dismiss();
                    }
                }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void SetUpAdapter() {
        adapter = new DelTodayAdapter(getActivity(), todayLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {

    }
}
