package lamcomis.landaya.deliverytracker.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lamcomis.landaya.deliverytracker.R;

public class PostedDelivery extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_posted_delivery, viewGroup, false);
        return view;

    }
}
