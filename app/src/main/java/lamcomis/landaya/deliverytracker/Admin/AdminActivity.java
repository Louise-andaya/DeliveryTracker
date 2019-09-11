package lamcomis.landaya.deliverytracker.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import lamcomis.landaya.deliverytracker.Driver.DriverAcrivity;
import lamcomis.landaya.deliverytracker.Global.PromptActivity;
import lamcomis.landaya.deliverytracker.Global.TabsAdapter;
import lamcomis.landaya.deliverytracker.R;

public class AdminActivity extends AppCompatActivity {
    private TabsAdapter tabadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        logout = findViewById(R.id.logout);
        tabadapter = new TabsAdapter(getSupportFragmentManager());
        tabadapter.addFragment(new DeliveryForToday(), "Delivery For Today");
        tabadapter.addFragment(new PostedDelivery(), "Posted Delivery");
        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);
        logout = findViewById(R.id.logout);
        logout.setClickable(true);
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    PromptActivity.showAlert(AdminActivity.this, "logout");

                    return true;
                }
                return false;
            }
        });
    }
}
