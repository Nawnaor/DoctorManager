package project.kyawmyoag.doctormanager.Does;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;

import project.kyawmyoag.doctormanager.R;

public class ViewDoes extends AppCompatActivity {

    private TextView cusName, cusPhone, cusPass, cusAbout,cusImei, cusDate, cusTime,
            cusService, cusFee;
    private Context context;
    
    private static final int REQUEST_CALL =1;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_does);

        cusName = findViewById(R.id.cus_name);
        cusPhone = findViewById(R.id.cus_phone);
        cusPass = findViewById(R.id.cus_pass);
        cusAbout = findViewById(R.id.cus_about);
        cusImei = findViewById(R.id.cus_imei);
        cusDate = findViewById(R.id.cus_date);
        cusTime = findViewById(R.id.cus_time);
        cusService = findViewById(R.id.cus_service);
        cusFee = findViewById(R.id.cus_fee);

        //Get a values from Previous Page

        cusName.setText(getIntent().getStringExtra("CustomerName"));
        cusPhone.setText(getIntent().getStringExtra("PhoneNo"));
        cusPass.setText(getIntent().getStringExtra("Customerpass"));
        cusDate.setText(getIntent().getStringExtra("Date"));
        cusTime.setText(getIntent().getStringExtra("Time"));
        cusAbout.setText(getIntent().getStringExtra("About"));
        cusImei.setText(getIntent().getStringExtra("IMEI"));
        cusService.setText(getIntent().getStringExtra("Service"));
        cusFee.setText(getIntent().getStringExtra("Fee"));

        final String keydoes = getIntent().getStringExtra("keydoes");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("JOB LIST")
                .child("Job" + keydoes);

        cusPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();

            }
        });
    }
    public void makePhoneCall() {
        String number = cusPhone.getText().toString();
        if (number.trim().length()>0) {
            if (ContextCompat.checkSelfPermission(ViewDoes.this, Manifest.permission.CALL_PHONE)!=
            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ViewDoes.this,new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL);
            } else {
                String dial = "tel:" + cusPhone;
                startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
            }
        } else {
            Toast.makeText(ViewDoes.this, "Enter", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}