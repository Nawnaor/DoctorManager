package project.kyawmyoag.doctormanager.MyTeam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import project.kyawmyoag.doctormanager.ImageSetter;
import project.kyawmyoag.doctormanager.R;
import project.kyawmyoag.doctormanager.SalesPerson;

public class MyTeamAdapter extends BaseAdapter {
    private ArrayList <SalesPerson> list;
    private Context context;

    public MyTeamAdapter(Context context,ArrayList<SalesPerson> list)
    {
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.team_layout,parent,false);
        }

        TextView teamName=convertView.findViewById(R.id.team_name);
        ImageView teamImage=convertView.findViewById(R.id.team_image);
        ProgressBar spinnerImage=convertView.findViewById(R.id.progressBar11);

        teamName.setText(list.get(position).getName());
        //spinnerImage.setVisibility(View.VISIBLE);
        ImageSetter.setImage(parent.getContext(),teamImage,list.get(position).getEmailId(),spinnerImage);
        //spinnerImage.setVisibility(View.GONE);
        return convertView;
    }
}
