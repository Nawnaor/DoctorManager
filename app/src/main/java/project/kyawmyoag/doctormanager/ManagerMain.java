package project.kyawmyoag.doctormanager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static project.kyawmyoag.doctormanager.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.alexzaitsev.meternumberpicker.MeterView;
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


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import project.kyawmyoag.doctormanager.Chat.ChatRoom;
import project.kyawmyoag.doctormanager.Does.Home;
import project.kyawmyoag.doctormanager.Graph.GraphManagerActivity;
import project.kyawmyoag.doctormanager.MyTeam.MyTeam;
import project.kyawmyoag.doctormanager.receiver.NetworkStateChangeReceiver;

public class ManagerMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private EditText itemName,itemName2, profit;
    private MeterView q;
    private DatabaseReference databaseRef;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private InventoryItem it;
    private ProgressBar spinkit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "ManagerMain";

    //
    private static final String WIFI_STATE_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);

        //
        spinkit = (ProgressBar)findViewById(R.id.spinkit);
        Sprite circle = new Circle();
        spinkit.setIndeterminateDrawable(circle);


        //
        registerForNetworkChangeEvents(this);

        //
        final IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable=intent.getBooleanExtra(IS_NETWORK_AVAILABLE,false);

                if (isNetworkAvailable){


                }
                else {
                    String networkStatus=isNetworkAvailable?"":"အင်တာနက်ချိတ်ဆက်ထားခြင်းမရှိပါ";
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout),""+networkStatus,Snackbar.LENGTH_INDEFINITE);
                    View sbView=snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplication(),R.color.colorPrimary));
                    View snackView = snackbar.getView();
                    TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.BLUE);
                    snackbar.setAction("ပြန်ချိတ်",new TryAgainListener());
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();

                }

            }
        },intentFilter);



        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        final String id = details.get("id");
        final String role = details.get("role");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(id,role,false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        updateList(id,role,true);

        spinkit.setVisibility(View.VISIBLE);


        databaseRef = FirebaseDatabase.getInstance().getReference(role);
        mRecyclerView=findViewById(R.id.items_list);
        final ArrayList<InventoryItem> list= new ArrayList<>();
        databaseRef.child(id).child("Inventory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                            list.add(it1);
                        }
                                        /* CustomAdapter mAdapter = new CustomAdapter(getApplicationContext(),data);
                                        listView.setAdapter(mAdapter); */
                        mAdapter=new InventoryAdapter(getApplicationContext(), list, ManagerMain.this);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        spinkit.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // dialog box to add item on dashboard
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManagerMain.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_box_add_item_inventory, null);
                mBuilder.setTitle("ရောင်းရန်ပစ္စည်းထည့်ပါ");

                final Button ok = (Button) mView.findViewById(R.id.ok);
                itemName = mView.findViewById(R.id.item_name);
                itemName2 = mView.findViewById(R.id.item_name2);
                profit = mView.findViewById(R.id.profit);
                q = mView.findViewById(R.id.quantity);

                //
                mDisplayDate = mView.findViewById(R.id.tvDate);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                //
                mDisplayDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(ManagerMain.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setTitle("နေ့စွဲ");
                        dialog.show();
                    }
                });
                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        Log.d(TAG,"onDateSet: mm/dd/yyy:"+month+"/"+day+"/"+year);
                        String date = month + "/" + day + "/" + year;
                        mDisplayDate.setText(date);
                        mDisplayDate.setTextColor(Color.RED);

                    }
                };
                //

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String item = itemName.getText().toString();
                        String item2 = itemName2.getText().toString();
                        String date = mDisplayDate.getText().toString();
                        String q1 = String.valueOf(q.getValue());
                        int profitAmount = Integer.parseInt(profit.getText().toString());


                        if (TextUtils.isEmpty(item)|| TextUtils.isEmpty(item2) || TextUtils.isEmpty(q1) || TextUtils.isEmpty(date))
                        {
                            Toast.makeText(getApplicationContext(), "အချက်အလက်များအားလုံး ထည့်ပေးပါ!", Toast.LENGTH_LONG).show();

                        }
                        else {

                            int quant = Integer.parseInt(q1);
                            it = new InventoryItem(item,item2,date,quant,0,profitAmount);

                            String key = databaseRef.child(id).child("Inventory").push().getKey();
                            databaseRef.child(id).child("Inventory").child(key).setValue(it);
                            dialog.dismiss();

                            spinkit.setVisibility(View.VISIBLE);

                            mRecyclerView = findViewById(R.id.items_list);
                            final ArrayList<InventoryItem> list = new ArrayList<>();
                            databaseRef.child(id).child("Inventory")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                                                list.add(it1);
                                            }

                                            mAdapter = new InventoryAdapter(getApplicationContext(), list, ManagerMain.this);

                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                            layoutManager.setReverseLayout(true);
                                            layoutManager.setStackFromEnd(true);
                                            mRecyclerView.setLayoutManager(layoutManager);

                                            mRecyclerView.setAdapter(mAdapter);
                                            mAdapter.notifyDataSetChanged();

                                           spinkit.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                            Toast.makeText(getApplicationContext(), "ပစ္စည်းထည့်သွင်းမှုအောင်မြင်သည် !", Toast.LENGTH_LONG).show();


                            // get current manager name and compare across all salesperson's
                            databaseRef = FirebaseDatabase.getInstance().getReference("Manager");
                            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    SalesManager sm1;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        sm1 = snapshot.getValue(SalesManager.class);
                                        if (snapshot.getKey().equals(id)) {

                                            final String ManagerName = sm1.getName();

                                            // compare manager name and insert item in his inventory
                                            final DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference("Salesperson");
                                            databaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                    for( DataSnapshot snapshot1 : dataSnapshot2.getChildren())
                                                    {
                                                        SalesPerson sp1 = snapshot1.getValue(SalesPerson.class);
                                                        String salesperson_id = snapshot1.getKey();


                                                        if(sp1.getManagerName().equals(ManagerName))
                                                        {
                                                            String key1 = databaseRef1.child(salesperson_id).child("Inventory").push().getKey();
                                                            databaseRef1.child(salesperson_id).child("Inventory").child(key1).setValue(it);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView = navigationView.getHeaderView(0);
        final TextView headerManagerName = headView.findViewById(R.id.ManagerName);
        final TextView headerManagerEmail = headView.findViewById(R.id.ManagerMail);
        final ImageView headerManagerImage = headView.findViewById(R.id.imageView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Manager");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().equals(id)){
                        SalesManager sm = snapshot.getValue(SalesManager.class);
                        headerManagerName.setText(sm.getName());
                        headerManagerEmail.setText(sm.getEmail());
                        ImageSetter.setImage(getApplicationContext(),headerManagerImage,sm.getEmail(),spinkit);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            // exit dialog box
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
                            ManagerMain.super.onBackPressed();
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .build();
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if(id == R.id.dashboard) {

            Intent intent = new Intent(ManagerMain.this, Table.class);
            startActivity(intent);

        }
        else if (id == R.id.my_account) {

            //show the manager's account
            Intent intent = new Intent(ManagerMain.this,AccountManager.class);
            startActivity(intent);

        } else if (id == R.id.my_team) {
            Intent intent= new Intent(this,MyTeam.class);
            startActivity(intent);

        } else if (id == R.id.statistics) {
            Intent intent=new Intent(ManagerMain.this,GraphManagerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            // Share app with others
            ApplicationInfo api = getApplicationContext().getApplicationInfo();
            String apkpath = api.sourceDir;
            Intent share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.setType("application/vnd.android.package-archive");
            share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkpath)));
            startActivity(Intent.createChooser(share_intent, "Share app using"));

        } else if (id == R.id.nav_send) {
            // Share invite-code with salespersons

            SessionManager sm = new SessionManager(getApplicationContext());
            HashMap<String, String> details = sm.getUserDetails();
            final String key = details.get("id");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Manager");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if(snapshot.getKey().equals(key)){
                            String shareBody = "ဟိတ်, ငါ့နာမည်နဲ့ - " + snapshot.getValue(SalesManager.class).getName()
                                                 + " Dr.Manager မှာစာရင်းသွင်းပါ";
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            // sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite code -");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Send invite"));
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if(id == R.id.chat_room){

            SessionManager sessionManager = new SessionManager(getApplicationContext());
            final String idManager = sessionManager.getUserDetails().get("id");
            databaseRef = FirebaseDatabase.getInstance().getReference("Manager");
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals(idManager)){

                            SalesManager salesManager = dataSnapshot1.getValue(SalesManager.class);
                            Intent intent = new Intent(ManagerMain.this, ChatRoom.class);
                            intent.putExtra("ManagerNumber", salesManager.getNumber());
                            intent.putExtra("Name",salesManager.getName());
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateList(String id, String role, final boolean spin)
    {
        if(spin==true)
            spinkit.setVisibility(View.VISIBLE);
        databaseRef = FirebaseDatabase.getInstance().getReference(role);
        mRecyclerView=findViewById(R.id.items_list);
        final ArrayList<InventoryItem> list= new ArrayList<>();
        databaseRef.child(id).child("Inventory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                            list.add(it1);
                        }
                        mAdapter=new InventoryAdapter(getApplicationContext(),list, ManagerMain.this);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        if(spin==true)
                        spinkit.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }
    //
    public static void registerForNetworkChangeEvents(final Context context){
        NetworkStateChangeReceiver networkStateChangeReceiver = new NetworkStateChangeReceiver();
        context.registerReceiver(networkStateChangeReceiver,new IntentFilter(CONNECTIVITY_ACTION));
        context.registerReceiver(networkStateChangeReceiver,new IntentFilter(WIFI_STATE_CHANGE_ACTION));

    }

    private class TryAgainListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent1 = new Intent(ManagerMain.this,ManagerMain.class);
            startActivity(intent1);
            finish();

        }
    }


}
