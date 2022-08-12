package project.kyawmyoag.doctormanager.Does;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import project.kyawmyoag.doctormanager.R;

public class DoesAdapter extends RecyclerView.Adapter<DoesAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<MyDoes>myDoes;
    private List<MyDoes>myDoesListFull;
    private Activity activity;
    private DatabaseReference reference;


    public DoesAdapter(Context c,List<MyDoes>myDoesList,Activity activity) {
        this.context=c;
        this.myDoes=myDoesList;
        myDoesListFull=new ArrayList<>(myDoesList);
        this.activity=activity;

    }

    @NonNull
    @Override
    public DoesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_does,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder,final int i) {
        final MyDoes item = myDoes.get(i);

        Log.d("Setting for:",String.valueOf(i));
        myViewHolder.day2.setText(item.getDate());
        myViewHolder.title2.setText(item.getCustomerName());
        myViewHolder.description2.setText(item.getAbout());
        myViewHolder.phoneNo2.setText(item.getPhoneNo());
        myViewHolder.condition.setText(item.getCondition());
        //time2 add
        myViewHolder.time2.setText(item.getTime());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewdoes = new Intent(context,ViewDoes.class);
                viewdoes.putExtra("CustomerName",item.getCustomerName());
                viewdoes.putExtra("PhoneNo",item.getPhoneNo());
                viewdoes.putExtra("Customerpass",item.getCustomerpass());
                viewdoes.putExtra("Service",item.getService());
                viewdoes.putExtra("About",item.getAbout());
                viewdoes.putExtra("IMEI",item.getIMEI());
                viewdoes.putExtra("Date",item.getDate());
                viewdoes.putExtra("Time",item.getTime());
                viewdoes.putExtra("Fee",item.getFee());
                viewdoes.putExtra("Condition",item.getCondition());
                viewdoes.putExtra("keydoes",item.getKeydoes());
                viewdoes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewdoes);
            }
        });

        myViewHolder.options2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,myViewHolder.options2);
                try {
                    Field[]fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?>classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                            setForceIcons.invoke(menuPopupHelper,true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();                }

                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Intent aa =new Intent(context,EditTaskDesk.class);
                                aa.putExtra("CustomerName",item.getCustomerName());
                                aa.putExtra("PhoneNo",item.getPhoneNo());
                                aa.putExtra("Customerpass",item.getCustomerpass());
                                aa.putExtra("Service",item.getService());
                                aa.putExtra("About",item.getAbout());
                                aa.putExtra("IMEI",item.getIMEI());
                                aa.putExtra("Date",item.getDate());
                                aa.putExtra("Time",item.getTime());
                                aa.putExtra("Fee",item.getFee());
                                aa.putExtra("Condition",item.getCondition());
                                aa.putExtra("keydoes",item.getKeydoes());
                                aa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(aa);

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myDoes.size();
    }

    @Override
    public Filter getFilter() {
        return myDoesFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView day2,title2,description2,phoneNo2,time2,condition;
        ImageView options2;

        public MyViewHolder(View itemView) {
            super(itemView);
            day2 = (TextView) itemView.findViewById(R.id.day2);
            title2 = (TextView) itemView.findViewById(R.id.title2);
            options2 = (ImageView) itemView.findViewById(R.id.options2);
            description2 = (TextView) itemView.findViewById(R.id.description2);
            phoneNo2 = (TextView) itemView.findViewById(R.id.phoneNo2);
            time2 = (TextView) itemView.findViewById(R.id.time2);
            condition = (TextView) itemView.findViewById(R.id.condition);

        }

    }

    private Filter myDoesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyDoes> filterlist = new ArrayList<>();
            if (constraint==null|| constraint.length()==0){
                filterlist.addAll(myDoesListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (MyDoes item:myDoesListFull){
                    if (item.getCustomerName().toLowerCase().contains(pattern)){
                        filterlist.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myDoes.clear();
            myDoes.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

}
