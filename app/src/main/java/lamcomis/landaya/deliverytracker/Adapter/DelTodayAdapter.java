package lamcomis.landaya.deliverytracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lamcomis.landaya.deliverytracker.List.DelForTodayList;
import lamcomis.landaya.deliverytracker.R;

public class DelTodayAdapter extends RecyclerView.Adapter<DelTodayAdapter.ViewHolder> {
    Context context;
    private List<DelForTodayList> todayLists;

    public DelTodayAdapter(Context context, List<DelForTodayList>todayLists){
        this.context = context;
        this.todayLists = todayLists;
    }


    @NonNull
    @Override
    public DelTodayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.today_list, parent, false);
        return new DelTodayAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DelTodayAdapter.ViewHolder holder, int position) {
        DelForTodayList list = todayLists.get(position);
        holder.today_Driver.setText(list.getToday_driver());
        holder.today_Customer.setText(list.getToday_customer());
        holder.today_Contact.setText(list.getToday_contact());
      //  holder.today_Dtr.setText(list.getToday_dtr());

    }

    @Override
    public int getItemCount() {
        return todayLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView today_Driver, today_Customer, today_Contact, today_Dtr;

        public ViewHolder(View itemView) {
            super(itemView);

            today_Driver = itemView.findViewById(R.id.today_driver);
            today_Customer = itemView.findViewById(R.id.today_customer);
            today_Contact = itemView.findViewById(R.id.today_contact);
           // today_Dtr = itemView.findViewById(R.id.today_dtr);
        }
    }
}