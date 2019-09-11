package lamcomis.landaya.deliverytracker.Adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lamcomis.landaya.deliverytracker.Driver.DriverAcrivity;
import lamcomis.landaya.deliverytracker.Driver.ProcessDelivery;
import lamcomis.landaya.deliverytracker.List.DriverDataList;
import lamcomis.landaya.deliverytracker.R;

public class DriverAdapter extends  RecyclerView.Adapter<DriverAdapter.ViewHolder> {
    SQLiteDatabase db;
    public static Context context;
    private List<DriverDataList> list;

    public DriverAdapter(Context context, List<DriverDataList> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public DriverAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.delivery_list, parent, false);
        return new DriverAdapter.ViewHolder(v);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DriverDataList dlist = list.get(position);

        holder.CName.setText(dlist.getCustomer_name());
        holder.DTR.setText(dlist.getDr_number());
        holder.ContactPerson.setText(dlist.getContact_person());
        holder.Fman.setText(dlist.getFirst_man());
        holder.Sman.setText(dlist.getSecond_man());
        String status = holder.status = dlist.getStatus();
        if (status.equals("ATV")) {
            holder.pending.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String customer_name = list.get(position).getCustomer_name().toString();
                    Intent i = new Intent(holder.itemView.getContext(), ProcessDelivery.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("customer", customer_name);
                    holder.itemView.getContext().startActivity(i);
                }
            });
        } else {
            holder.done.setVisibility(View.VISIBLE);
            holder.bind(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        String status;
        private TextView CName, DTR, ContactPerson, Fman, Sman;
        Button pending, done;
        ImageView img_check;

        public ViewHolder(View itemView) {
            super(itemView);
            CName = (TextView) itemView.findViewById(R.id.driver_customer);
            DTR = (TextView) itemView.findViewById(R.id.driver_dr_number);
            ContactPerson = (TextView) itemView.findViewById(R.id.driver_contact);
            Fman = (TextView) itemView.findViewById(R.id.driver_firstman);
            Sman = (TextView) itemView.findViewById(R.id.driver_secondman);
            pending = (Button) itemView.findViewById(R.id.pending);
            pending.setVisibility(View.GONE);
            done = (Button) itemView.findViewById(R.id.done);
            done.setVisibility(View.GONE);
            img_check = (ImageView) itemView.findViewById(R.id.btn_check);
            img_check.setVisibility(View.GONE);
        }

        void bind(final DriverDataList customer) {
            img_check.setVisibility(customer.isChecked() ? View.VISIBLE: View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customer.setChecked(!customer.isChecked());
                    img_check.setVisibility(customer.isChecked() ? View.VISIBLE : View.GONE);

                }
            });

        }
    }
    public List<DriverDataList> getAll() {
        return list;
    }

    public List<DriverDataList> getSelected() {
        ArrayList<DriverDataList> selected = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                selected.add(list.get(i));

            }
        }
        return selected;
    }

}