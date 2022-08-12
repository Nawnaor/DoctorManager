package project.kyawmyoag.doctormanager.Note;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class ViewNote extends AppCompatActivity {

    private EditText DemoTitle;
    private EditText DemoText;
    private TextView DateText;
    private FloatingActionButton SaveButton;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        DemoTitle = findViewById(R.id.demo_title);
        DemoText = findViewById(R.id.edittext_area);
        DateText = findViewById(R.id.create_time);
        SaveButton = findViewById(R.id.save);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get a value from previous page
        DemoTitle.setText(getIntent().getStringExtra("TitleNote"));
        DemoText.setText(getIntent().getStringExtra("Description"));
        DateText.setText(getIntent().getStringExtra("Date"));
        final String keykeyNotes = getIntent().getStringExtra("NoteId");
        reference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("Order"+keykeyNotes);

        //make a note for button
        SaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("TitleNote").setValue(DemoTitle.getText().toString());
                dataSnapshot.getRef().child("Description").setValue(DemoText.getText().toString());
                dataSnapshot.getRef().child("Date").setValue(DateText.getText().toString());
                Intent intent = new Intent(ViewNote.this,NoteActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(ViewNote.this, "ပြင်ဆင်ပြီးပါပြီ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteNote(){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(ViewNote.this,R.style.AlertDialogCustom);
        alertdialog.setTitle("သတိပေးချက်");
        alertdialog.setIcon(R.drawable.correct);
        alertdialog.setMessage("ဖျက်ရန်သေချာပါသလား?");
        alertdialog.setPositiveButton("သေချာပါသည်။", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent a = new Intent(ViewNote.this,NoteActivity.class);
                            a.addFlags(a.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(a);
                            final ProgressDialog dialog = ProgressDialog.show(ViewNote.this,"ဖျက်နေပါသည်...","ခေတ္တစောင့်ပါ",true);
                            dialog.show();

                        } else {
                            Toast.makeText(getApplicationContext(), "ဖျက်ပြီးပါပြီ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertdialog.setNegativeButton("ထွက်မည်။", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ViewNote.this,"မဖျက်ပါ",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog customAlertDialog= alertdialog.create();
        customAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
        customAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_note_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_note:deleteNote();
            return false;

            case R.id.edit_note:DemoTitle.setEnabled(true);
            DemoText.setEnabled(true);
                Toast.makeText(this, "ပြင်ဆင်ရန်", Toast.LENGTH_SHORT).show();


            return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        if (DemoTitle.isEnabled() || DemoText.isEnabled()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ViewNote.this,R.style.AlertDialogCustom);
            dialog.setTitle("အသိပေးချက်!!");
            dialog.setIcon(R.drawable.warning);
            dialog.setMessage("ပြင်ဆင်ထားသည့်အတွက် save လုပ်ရန်လိုအပ်ပါသည်");
            dialog.setPositiveButton("မှတ်မည်", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveNote();
                    Toast.makeText(ViewNote.this, "ပြင်ဆင်ပြီးပါပြီ", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.setNegativeButton("မမှတ်တော့ပါ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ViewNote.super.onBackPressed();
                }
            });
            AlertDialog customAlertDialog= dialog.create();
            customAlertDialog.show();

        } else {
            super.onBackPressed();
        }
    }

}