package project.kyawmyoag.doctormanager.Does;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
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
import project.kyawmyoag.doctormanager.ToDo.ShowCalendarViewBottomSheet;

public class Home extends AppCompatActivity {

    private TextView addtask;
    private ImageView calendar,imageView;

    private DatabaseReference reference;
    private RecyclerView taskRecycler2;
    private ArrayList<MyDoes>list;
    private DoesAdapter doesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageView = findViewById(R.id.noDataImage2);
        Glide.with(this).load(R.drawable.first_note).into(imageView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        swipeRefreshLayout=findViewById(R.id.swiperefresh2);
        loading = (ProgressBar)findViewById(R.id.loading);
        calendar = findViewById(R.id.calendar2);
        addtask = findViewById(R.id.addTask2);
        Sprite circle = new Circle();
        loading.setIndeterminateDrawable(circle);

        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,NewTaskAct.class);
                startActivity(intent);
            }
        });



        //working with data
        taskRecycler2 = findViewById(R.id.taskRecycler2);
        taskRecycler2.setLayoutManager(new LinearLayoutManager(this));
        loading.setVisibility(View.VISIBLE);

        //get data from firebase
        final ArrayList<MyDoes>list= new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("JOB LIST");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    MyDoes p =dataSnapshot1.getValue(MyDoes.class);
                    list.add(p);
                }
                doesAdapter = new DoesAdapter(getApplicationContext(),list,Home.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                taskRecycler2.setLayoutManager(layoutManager);
                taskRecycler2.setAdapter(doesAdapter);
                doesAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                imageView.setVisibility(list.isEmpty() ? View.VISIBLE:View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Toast.makeText(getApplicationContext(), "အချက်အလက်များမရှိသေးပါ", Toast.LENGTH_SHORT).show();

        calendar.setOnClickListener(v -> {
            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(),showCalendarViewBottomSheet.getTag());
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
        final ArrayList<MyDoes>list= new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("JOB LIST");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    MyDoes p =dataSnapshot1.getValue(MyDoes.class);
                    list.add(p);
                }
                doesAdapter = new DoesAdapter(getApplicationContext(),list,Home.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                taskRecycler2.setLayoutManager(layoutManager);
                taskRecycler2.setAdapter(doesAdapter);
                doesAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                imageView.setVisibility(list.isEmpty() ? View.VISIBLE:View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        new FancyAlertDialog.Builder(this)
                .setTitle("သတိပေးချက်!!!")
                .setBackgroundColor(Color.parseColor("#5002a4"))  //Don't pass R.color.colorvalue
                .setMessage("ထွက်ရန်သေချာပါသလား?")
                .setNegativeBtnText("မသေချာပါ")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("သေချာသည်")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.warning, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Home.super.onBackPressed();
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
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}