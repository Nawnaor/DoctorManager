package project.kyawmyoag.doctormanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import project.kyawmyoag.doctormanager.DailyIncome.DailyMain;
import project.kyawmyoag.doctormanager.Does.Home;
import project.kyawmyoag.doctormanager.Note.NoteActivity;

public class Table extends AppCompatActivity {

    ImageView customer,order,dailysales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        ImageView customer = findViewById(R.id.customer);
        ImageView order = findViewById(R.id.order);
        ImageView dailysales = findViewById(R.id.dailysales);

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomer(v);

            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNote(v);
            }
        });

        dailysales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendailySales(v);
            }
        });

    }

    public void openCustomer (View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void openNote (View view){
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    public void opendailySales (View view){
        Intent intent = new Intent(this, DailyMain.class);
        startActivity(intent);
    }
}