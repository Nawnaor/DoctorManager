package project.kyawmyoag.doctormanager.Note;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.transition.Visibility;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import project.kyawmyoag.doctormanager.R;
import project.kyawmyoag.doctormanager.SessionManager;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<MyNote>myNotes;
    private List<MyNote>myNoteListFull;
    private Activity activity;

    public NoteAdapter(Context c,List<MyNote>myNoteList,Activity activity) {
        this.context = c;
        this.myNotes = myNoteList;
        myNoteListFull = new ArrayList<>(myNoteList);
        this.activity = activity;


    }



    @NonNull
    @Override
    public NoteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        MyNote item = myNotes.get(i);

        Log.d("Setting for:",String.valueOf(i));
        myViewHolder.Title.setText(myNotes.get(i).getTitleNote());
        myViewHolder.Description.setText(myNotes.get(i).getDescription());
        myViewHolder.Priority.setText(String.valueOf(item.getPriority()));
        myViewHolder.NoteDate.setText(item.getDate());

        if (item.getPriority().equals("အရေးကြီး")) {
            myViewHolder.Priority.setTextColor(Color.parseColor("#FF5722"));
        }else if (item.getPriority().equals("သာမန်")) {
            myViewHolder.Priority.setTextColor(Color.parseColor("#4CAF50"));
        }


        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewnotes = new Intent(context,ViewNote.class);
                viewnotes.putExtra("TitleNote",item.getTitleNote());
                viewnotes.putExtra("Description",item.getDescription());
                viewnotes.putExtra("Date",item.getDate());
                viewnotes.putExtra("NoteId",item.getNoteId());
                viewnotes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewnotes);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myNotes.size();
    }

    @Override
    public Filter getFilter() {
        return myNotesFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title,Description,Priority,NoteDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            Title = (TextView) itemView.findViewById(R.id.text_view_title);
            Description = (TextView) itemView.findViewById(R.id.text_view_description);
            Priority = (TextView) itemView.findViewById(R.id.text_view_priority);
            NoteDate = (TextView) itemView.findViewById(R.id.text_view_date);
        }

    }

    private Filter myNotesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyNote>filterlist = new ArrayList<>();
            if (constraint==null||constraint.length()==0){
                filterlist.addAll(myNoteListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (MyNote item:myNoteListFull){
                    if (item.getTitleNote().toLowerCase().contains(pattern)){
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
            myNotes.clear();
            myNotes.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };


}
