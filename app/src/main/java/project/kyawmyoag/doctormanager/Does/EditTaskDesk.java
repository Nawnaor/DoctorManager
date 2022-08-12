package project.kyawmyoag.doctormanager.Does;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.kyawmyoag.doctormanager.R;
import project.kyawmyoag.doctormanager.Table;

public class EditTaskDesk extends AppCompatActivity {

    private EditText CustomerName,PhoneNo,Customerpass,About,Imei,Date,Time,Service,Condition,Fee;
    private Button addTaskupdate2;
    private TextView delete;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_desk);

        CustomerName = findViewById(R.id.addTaskTitle2);
        PhoneNo = findViewById(R.id.addPhone2);
        Customerpass = findViewById(R.id.customerpass);
        About = findViewById(R.id.addTaskDescription2);
        Imei = findViewById(R.id.imei);
        Date = findViewById(R.id.taskDate2);
        Time = findViewById(R.id.taskTime2);
        Service = findViewById(R.id.taskEvent2);
        Fee = findViewById(R.id.charges2);
        Condition = findViewById(R.id.conditionbutton);

        addTaskupdate2 = findViewById(R.id.addTaskupdate2);
        delete = findViewById(R.id.delete);

        //get a value from previous page
        CustomerName.setText(getIntent().getStringExtra("CustomerName"));
        PhoneNo.setText(getIntent().getStringExtra("PhoneNo"));
        Customerpass.setText(getIntent().getStringExtra("Customerpass"));
        Date.setText(getIntent().getStringExtra("Date"));
        Time.setText(getIntent().getStringExtra("Time"));
        About.setText(getIntent().getStringExtra("About"));
        Imei.setText(getIntent().getStringExtra("IMEI"));
        Service.setText(getIntent().getStringExtra("Service"));
        Condition.setText(getIntent().getStringExtra("Condition"));
        Fee.setText(getIntent().getStringExtra("Fee"));

        final String keykeyDoes = getIntent().getStringExtra("keydoes");

        reference = FirebaseDatabase.getInstance().getReference().child("JOB LIST").
                child("Job"+keykeyDoes);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(EditTaskDesk.this,R.style.AlertDialogCustom);
                alertdialog.setTitle("သတိပေးချက်");
                alertdialog.setIcon(R.drawable.correct);
                alertdialog.setMessage("ယူသွားကြောင်း သေချာပါသလား?");
                alertdialog.setPositiveButton("သေချာပါသည်။", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent a = new Intent(EditTaskDesk.this, Home.class);
                                    a.addFlags(a.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(a);

                                } else {
                                    Toast.makeText(getApplicationContext(), "ဖျက်လိုက်ပါပြီ..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                alertdialog.setNegativeButton("ထွက်မည်။", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EditTaskDesk.this, "မဖျက်တော့ပါ", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog customAlertDialog = alertdialog.create();
                customAlertDialog.show();
            }
        });

        //make an event for button
        addTaskupdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("CustomerName").setValue(CustomerName.getText().toString());
                        dataSnapshot.getRef().child("PhoneNo").setValue(PhoneNo.getText().toString());
                        dataSnapshot.getRef().child("Customerpass").setValue(Customerpass.getText().toString());
                        dataSnapshot.getRef().child("About").setValue(About.getText().toString());
                        dataSnapshot.getRef().child("IMEI").setValue(Imei.getText().toString());
                        dataSnapshot.getRef().child("Date").setValue(Date.getText().toString());
                        dataSnapshot.getRef().child("Time").setValue(Time.getText().toString());
                        dataSnapshot.getRef().child("Service").setValue(Service.getText().toString());
                        dataSnapshot.getRef().child("Fee").setValue(Fee.getText().toString());
                        dataSnapshot.getRef().child("Condition").setValue(Condition.getText().toString());
                        dataSnapshot.getRef().child("keydoes").setValue(keykeyDoes);
                        Intent intent = new Intent(EditTaskDesk.this,Home.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConditionDialog();
            }
        });
    }
    private void showConditionDialog() {
        final int[] checkedItem = {-1};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditTaskDesk.this,R.style.AlertDialogCustom);
        alertDialog.setTitle("အခြေအနေ ရွေးပါ");
        alertDialog.setIcon(R.drawable.condition_24);
        final String[] listItems = new String[]{"ပြုပြင်နေဆဲ","ပြုပြင်ပြီး","ပြင်၍မရပါ"};
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0]=which;
                Condition.setText(""+listItems[which]);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("မထည့်ပါ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog customAlertDialog = alertDialog.create();
        customAlertDialog.show();
    }
}