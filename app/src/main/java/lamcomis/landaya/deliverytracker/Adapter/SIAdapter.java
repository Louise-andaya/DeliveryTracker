package lamcomis.landaya.deliverytracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import lamcomis.landaya.deliverytracker.List.SIList;
import lamcomis.landaya.deliverytracker.R;

/**
 * Created by landaya on 3/19/2019.
 */

public class SIAdapter extends BaseAdapter {



    private Context activity;
    private List<SIList> data;
    private static LayoutInflater inflater = null;
    private View vi;
    private ViewHolder viewHolder;

    public SIAdapter(Context context, List<SIList> items) {
        this.activity = context;
        this.data = items;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        vi = view;
        //Populate the Listview
        final int pos = position;
        SIList items = data.get(pos);
        if(view == null) {
            vi = inflater.inflate(R.layout.activity_list_view_with_checkbox_item, null);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) vi.findViewById(R.id.list_view_item_checkbox);
            viewHolder.name = (TextView) vi.findViewById(R.id.list_view_item_text);
            vi.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) view.getTag();
        viewHolder.name.setText(items.getSI());
        if(items.isCheckbox()){
            viewHolder.checkBox.setChecked(true);
        }
        else {
            viewHolder.checkBox.setChecked(false);
        }
        return vi;
    }
    public List<SIList> getRecProgAllData(){
        return data;
    }
    public void setCheckBox(int position){
        //Update status of checkbox
        SIList items = data.get(position);
        items.setCheckbox(!items.isCheckbox());
        notifyDataSetChanged();
    }

    public class ViewHolder{
        TextView name;
        CheckBox checkBox;
    }
}