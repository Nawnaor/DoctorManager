package project.kyawmyoag.doctormanager.Graph;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import project.kyawmyoag.doctormanager.InventoryItem;
import project.kyawmyoag.doctormanager.R;
import project.kyawmyoag.doctormanager.SessionManager;

public class GraphManagerActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private String id;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_manager);
        pieChart = findViewById(R.id.chart);

        entries = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar12);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        progressBar.setVisibility(View.VISIBLE);

        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        id = details.get("id");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Manager");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(id)){
                        //found current manager

                        databaseReference.child(snapshot.getKey()).child("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                float total = 0.0f;
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    InventoryItem it = snapshot1.getValue(InventoryItem.class);
                                    total+=it.getProfit() * it.getSold();
                                }

                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren())
                                {
                                    InventoryItem it = snapshot1.getValue(InventoryItem.class);
                                    float curr = ((it.getProfit() * it.getSold()) / total) * 100f;

                                    if(curr > 0.0f)
                                    entries.add(new PieEntry(curr, it.getItemName()));
                                }

                                progressBar.setVisibility(View.GONE);
                                PieDataSet set = new PieDataSet(entries, "အမြတ်ငွေ ရလဒ်များ");
                                set.setValueTextSize(14f);
                                set.setColors(ColorTemplate.VORDIPLOM_COLORS);
                                PieData data = new PieData(set);
                                data.setValueTextColor(Color.BLACK);
                                pieChart.setData(data);
                                pieChart.invalidate(); // refresh
                                pieChart.setCenterText("သင်၏အမြတ်ငွေ\nခွဲခြမ်းစိတ်ဖြာမှု");
                                pieChart.setCenterTextSize(14f);
                                pieChart.animateXY(1000,1000, Easing.EasingOption.EaseInOutCirc, Easing.EasingOption.EaseInOutCirc);
                                pieChart.setCenterTextColor(Color.WHITE);
                                pieChart.setTransparentCircleColor(getResources().getColor(R.color.colorPrimary));
                                pieChart.setHoleColor(getResources().getColor(R.color.colorPrimaryDark));
                                pieChart.setEntryLabelColor(Color.RED);
                                pieChart.setEntryLabelTextSize(8);
                                pieChart.setHighlightPerTapEnabled(true);
                                Legend legend=pieChart.getLegend();
                                legend.setWordWrapEnabled(true);
                                legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                                legend.setTextSize(8);
                                legend.setTextColor(getResources().getColor(R.color.colorPrimary));
                                legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                                legend.setXEntrySpace(7f);
                                legend.setYEntrySpace(5f);
                                legend.setYOffset(0f);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        databaseReference.child(snapshot.getKey()).child("Inventory")
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                        entries.clear();
                                        progressBar.setVisibility(View.VISIBLE);
                                        databaseReference.child(snapshot.getKey()).child("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                float total = 0.0f;
                                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                                    InventoryItem it = snapshot1.getValue(InventoryItem.class);
                                                    total+=it.getProfit() * it.getSold();
                                                }

                                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren())
                                                {
                                                    InventoryItem it = snapshot1.getValue(InventoryItem.class);
                                                    float curr = ((it.getProfit() * it.getSold()) / total) * 100.0f;

                                                    if(curr > 0.0f)
                                                        entries.add(new PieEntry(curr, it.getItemName()));
                                                }

                                                progressBar.setVisibility(View.GONE);
                                                PieDataSet set = new PieDataSet(entries, "အမြတ်ငွေ ရလဒ်များ (%)");
                                                set.setValueTextSize(8f);
                                                set.setColors(ColorTemplate.PASTEL_COLORS);
                                                PieData data = new PieData(set);
                                                data.setValueTextColor(Color.BLACK);
                                                pieChart.setData(data);
                                                pieChart.invalidate(); // refresh
                                                pieChart.setCenterText("သင်၏အမြတ်ငွေ\nခွဲခြမ်းစိတ်ဖြာမှု");
                                                pieChart.setCenterTextSize(14f);
                                                pieChart.animateXY(1000,1000, Easing.EasingOption.EaseInOutCirc, Easing.EasingOption.EaseInOutCirc);
                                                pieChart.setCenterTextColor(Color.BLACK);
                                                pieChart.setTransparentCircleColor(Color.BLACK);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
