package project.kyawmyoag.doctormanager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static project.kyawmyoag.doctormanager.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzaitsev.meternumberpicker.MeterView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
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
import java.util.HashMap;

import project.kyawmyoag.doctormanager.Chat.ChatRoom;
import project.kyawmyoag.doctormanager.Chat.PersonalChatActivitySalesperson;
import project.kyawmyoag.doctormanager.Graph.GraphSalesperson;
import project.kyawmyoag.doctormanager.Graph.GraphSalespersonActivity;
import project.kyawmyoag.doctormanager.Leaderboard.LeaderBoardSalesperson;
import project.kyawmyoag.doctormanager.receiver.NetworkStateChangeReceiver;

public class SalespersonMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private ProgressBar spinkit;
    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList <InventoryItem> list;
    private FloatingActionButton fab;
    private String managerName, salespersonName ,id,role;
    private String SalesPersonName;
    private int sold;

    //
    private static final String WIFI_STATE_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesperson_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab=findViewById(R.id.fab_salesperson);
        spinkit = (ProgressBar) findViewById(R.id.spinkit);
        swipeRefreshLayout=findViewById(R.id.swiperefresh1);

        //
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
        id = details.get("id");
        role = details.get("role");

        databaseReference = FirebaseDatabase.getInstance().getReference(role);
        mRecyclerView = findViewById(R.id.items_list1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(id,role,false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> itemlist=new ArrayList<>();
                itemlist=getItemList(id,role);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SalespersonMain.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_selling, null);
                final AutoCompleteTextView autoCompleteTextView;
                final MeterView numberPicker;
                Button ok = (Button) mView.findViewById(R.id.ok);
                autoCompleteTextView = mView.findViewById(R.id.autoCompleteTextView1);
                numberPicker=mView.findViewById(R.id.sold_number);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mView.getContext(),R.layout.support_simple_spinner_dropdown_item,itemlist);

                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(adapter);
                mBuilder.setView(mView);
                mBuilder.setTitle("ရောင်းချမည့်ပစ္စည်းကို ရွေးချယ်ပါ");
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                final ArrayList<String> finalItemlist = itemlist;
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do selling work here

                        sold = numberPicker.getValue();
                        final String itemName = autoCompleteTextView.getText().toString();

                        if(TextUtils.isEmpty(itemName) || sold==0)
                        {

                            Toast.makeText(getApplicationContext(),"အချက်အလက်များအားလုံးကိုထည့်ပါ!", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            int i1;
                            for(i1 = 0; i1< finalItemlist.size(); i1++)
                            {
                                if(finalItemlist.get(i1).equals(itemName))
                                    break;
                                //

                                //
                            }
                            if(i1 == finalItemlist.size()){
                                Toast.makeText(getApplicationContext(),"ရနိုင်သည့်ပစ္စည်းများထဲမှသာ ရွေးချယ်ပါ!", Toast.LENGTH_LONG).show();

                            }
                            else
                            {

                                databaseReference = FirebaseDatabase.getInstance().getReference("Salesperson");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                            // increasing sold for current salesperson only
                                            if(snapshot.getKey().equals(id)){

                                                SalesPerson sp = snapshot.getValue(SalesPerson.class);
                                                salespersonName = sp.getName();
                                                managerName = sp.getManagerName();

                                                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Salesperson");
                                                databaseReference1.child(id).child("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                                            if(snapshot1.getValue(InventoryItem.class).getItemName().equals(itemName)){
                                                                InventoryItem it1 = snapshot1.getValue(InventoryItem.class);
                                                                if(it1.getTotal_available()<sold)
                                                                {
                                                                    Toast.makeText(getApplicationContext(), "ပစ္စည်းကုန်သွားပါပြီ!", Toast.LENGTH_LONG).show();
                                                                    sold=0;
                                                                }
                                                                InventoryItem it = new InventoryItem(itemName,it1.getItemName2(),it1.getdate(),it1.getTotal_available(),sold + it1.getSold(), it1.getProfit());
                                                                databaseReference1.child(id).child("Inventory").child(snapshot1.getKey()).setValue(it);


                                                                // updating manager's sold
                                                                updateManagerSold(managerName,itemName,sold);

                                                                // updating graph node
                                                                if(sold>0)
                                                                {
                                                                    for(int i=0;i<100;i++)
                                                                    {
                                                                        System.out.println(salespersonName+" "+String.valueOf(it1.getProfit()*sold));
                                                                    }
                                                                    GraphSalesperson.create(String.valueOf(it1.getProfit()*sold), salespersonName);
                                                                }

                                                                ////////////////////////////////////////////////////////////////////////////////////////////
                                                                // decreasing the current item total remaining for all attached salespersons

                                                                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Salesperson");
                                                                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for(final DataSnapshot snapshot3 : dataSnapshot.getChildren()){
                                                                            SalesPerson sp = snapshot3.getValue(SalesPerson.class);

                                                                            if(sp.getManagerName().equals(managerName)){

                                                                                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Salesperson");
                                                                                databaseReference1.child(snapshot3.getKey()).child("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                                                                            Log.d("TAG :: ", snapshot1.getValue(InventoryItem.class).getItemName()+ " " + itemName);
                                                                                            if(snapshot1.getValue(InventoryItem.class).getItemName().equals(itemName)){
                                                                                                InventoryItem it1 = snapshot1.getValue(InventoryItem.class);
                                                                                                InventoryItem it = new InventoryItem(itemName,it1.getItemName2(),it1.getdate(), it1.getTotal_available()-sold,it1.getSold(), it1.getProfit());
                                                                                                databaseReference1.child(snapshot3.getKey()).child("Inventory").child(snapshot1.getKey()).setValue(it);

                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                        updateList(id,role,true);
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                                ///////////////////////////////////////////////////////////////////////////////////////////
                                                                updateList(id,role,true);
                                                                break;
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

                        dialog.dismiss();
                    }
                });
            }
        });



        spinkit.setVisibility(View.VISIBLE);

        list = new ArrayList<>();
        databaseReference.child(id).child("Inventory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                            list.add(it1);
                        }
                        mAdapter=new SalespersonInventoryAdapter(getApplicationContext(),list);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        spinkit.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
        final TextView headerSalespersonName = headView.findViewById(R.id.SalespersonName);
        final TextView headerSalespersonEmail = headView.findViewById(R.id.SalespersonMail);
        final ImageView headerSalespersonImage = headView.findViewById(R.id.imageView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Salesperson");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().equals(id)){
                        SalesPerson sm = snapshot.getValue(SalesPerson.class);
                        headerSalespersonName.setText(sm.getName());
                        headerSalespersonEmail.setText(sm.getEmailId());
                        ImageSetter.setImage(getApplicationContext(),headerSalespersonImage,sm.getEmailId(),spinkit);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateManagerSold(final String managerName, final String itemName, final int sold) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Manager");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("TAGM :: ",snapshot.getValue(SalesManager.class).getName() + " " + managerName);
                    if(snapshot.getValue(SalesManager.class).getName().equals(managerName)){
                        databaseReference.child(snapshot.getKey()).child("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    Log.d("TAGI :: ",snapshot1.getValue(InventoryItem.class).getItemName() + " " + itemName);
                                    if(snapshot1.getValue(InventoryItem.class).getItemName().equals(itemName)){
                                        InventoryItem it1 = snapshot1.getValue(InventoryItem.class);
                                        InventoryItem itNew = new InventoryItem(itemName,it1.getItemName2(),it1.getdate(),it1.getTotal_available(),it1.getSold() + sold, it1.getProfit());
                                        databaseReference.child(snapshot.getKey()).child("Inventory").child(snapshot1.getKey()).setValue(itNew);
                                        break;
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

    private ArrayList<String> getItemList(String id, String role) {
        final ArrayList<String> itemlist=new ArrayList<>();
        databaseReference.child(id).child("Inventory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                            itemlist.add(it1.getItemName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return itemlist;
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
                    .setNegativeBtnText("အသေချာပါ")
                    .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("သေချာသည်")
                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.warning, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            SalespersonMain.super.onBackPressed();
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
        getMenuInflater().inflate(R.menu.salesperson_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id1 = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id1 == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id1 = item.getItemId();

        if (id1 == R.id.dashboard) {
            Intent intent = new Intent(SalespersonMain.this, Table.class);
            startActivity(intent);

        } else if (id1 == R.id.my_account) {

            //show the salesperson's myaccount (used same class AccountManager for salesperson also)
            Intent intent = new Intent(SalespersonMain.this,AccountManager.class);
            startActivity(intent);

        } else if (id1 == R.id.leaderboard) {

            Intent intent = new Intent(SalespersonMain.this, LeaderBoardSalesperson.class);
            startActivity(intent);

        } else if (id1 == R.id.statistics) {

            Intent intent=new Intent(SalespersonMain.this,GraphSalespersonActivity.class);
            startActivity(intent);

        } else if (id1 == R.id.nav_share) {

            //Share app with others
            ApplicationInfo api = getApplicationContext().getApplicationInfo();
            String apkpath = api.sourceDir;
            Intent share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.setType("application/vnd.android.package-archive");
            share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkpath)));
            startActivity(Intent.createChooser(share_intent, "Share app using"));

        } else if(id1 == R.id.message_manager){

                databaseReference = FirebaseDatabase.getInstance().getReference("Salesperson");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if (snapshot.getKey().equals(id)) {

                                SalesPerson sp = snapshot.getValue(SalesPerson.class);
                                salespersonName = sp.getName();
                                managerName = sp.getManagerName();
                                Intent intent = new Intent(SalespersonMain.this, PersonalChatActivitySalesperson.class);
                                intent.putExtra("SalespersonName", salespersonName);
                                intent.putExtra("ManagerName", managerName);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }else if(id1 == R.id.chat_room){

            SessionManager sessionManager = new SessionManager(getApplicationContext());
            final String idSP = sessionManager.getUserDetails().get("id");
            databaseReference = FirebaseDatabase.getInstance().getReference("Salesperson");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     String thisManager="";
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                        if(idSP.equals(dataSnapshot1.getKey())){

                            SalesPerson salesPerson = dataSnapshot1.getValue(SalesPerson.class);
                            thisManager = salesPerson.getManagerName();
                            SalesPersonName=salesPerson.getName();
                            break;
                        }
                    }
                    final String thisManagerName = thisManager;
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Manager");
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                SalesManager salesManager = dataSnapshot1.getValue(SalesManager.class);
                                if(salesManager.getName().equals(thisManagerName)){

                                    Intent intent = new Intent(SalespersonMain.this, ChatRoom.class);
                                    intent.putExtra("ManagerNumber", salesManager.getNumber());
                                    intent.putExtra("Name",SalesPersonName);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

    void updateList(String id, String role, final boolean spin)
    {
        if(spin==true)
        {
            spinkit.setVisibility(View.VISIBLE);
        }

        final ArrayList<InventoryItem> list= new ArrayList<>();
        databaseReference.child(id).child("Inventory")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            InventoryItem it1 = snapshot.getValue(InventoryItem.class);
                            list.add(it1);
                        }
                        mAdapter=new SalespersonInventoryAdapter(getApplicationContext(), list);

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
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
            Intent intent1 = new Intent(SalespersonMain.this,SalespersonMain.class);
            startActivity(intent1);
            finish();

        }
    }

}
