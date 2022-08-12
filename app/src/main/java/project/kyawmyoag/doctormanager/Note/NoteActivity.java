package project.kyawmyoag.doctormanager.Note;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;

import project.kyawmyoag.doctormanager.R;

public class NoteActivity extends AppCompatActivity {

    private LinearLayout emptylayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar loading;
    private NoteAdapter noteAdapter;
    private DatabaseReference reference;
    private RecyclerView recyclerNote;
    private ArrayList<MyNote>list;

    public static final int ADD_NODE_REQUEST =1;
    public static final int EDIT_NODE_REQUEST =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final FloatingActionButton fb = findViewById(R.id.button_add_note);
        emptylayout = findViewById(R.id.empty_layout);
        loading = (ProgressBar)findViewById(R.id.loading);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_order_layout);
        Sprite circle = new Circle();
        loading.setIndeterminateDrawable(circle);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this,AddEditNoteActivity.class);
                startActivityForResult(intent,ADD_NODE_REQUEST);
            }
        });

        //working with data
        recyclerNote = findViewById(R.id.recycler_note);
        recyclerNote.setHasFixedSize(true);

        loading.setVisibility(View.VISIBLE);

        //get data from firebase
        final ArrayList<MyNote>list=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("ORDER");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    MyNote n = dataSnapshot1.getValue(MyNote.class);
                    list.add(n);
                }
                recyclerNote.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
                noteAdapter = new NoteAdapter(getApplicationContext(),list,NoteActivity.this);
                recyclerNote.setAdapter(noteAdapter);
                noteAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                emptylayout.setVisibility(list.isEmpty() ? View.VISIBLE:View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(false);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void updateList(final boolean spin){
        if (spin==true)
            loading.setVisibility(View.VISIBLE);
        final ArrayList<MyNote>list=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("ORDER");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    MyNote n = dataSnapshot1.getValue(MyNote.class);
                    list.add(n);
                }
                recyclerNote.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
                noteAdapter = new NoteAdapter(getApplicationContext(),list,NoteActivity.this);
                recyclerNote.setAdapter(noteAdapter);
                noteAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                emptylayout.setVisibility(list.isEmpty() ? View.VISIBLE:View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        new FancyAlertDialog.Builder(this)
                .setTitle("သတိပေးချက်")
                .setIcon(R.drawable.warning, Icon.Visible)
                .setBackgroundColor(Color.parseColor("#5002a4"))
                .setMessage("ထွက်ရန်သေချာပါသလား?")
                .setNegativeBtnText("မသေချာပါ")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))
                .setPositiveBtnText("သေချာသည်")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        NoteActivity.super.onBackPressed();
                    }
                }).OnNegativeClicked(new FancyAlertDialogListener() {
            @Override
            public void OnClick() {

            }
        }).build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_order,menu);
        MenuItem menuItem = menu.findItem(R.id.order_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}