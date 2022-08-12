package project.kyawmyoag.doctormanager.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import project.kyawmyoag.doctormanager.ManagerMain;
import project.kyawmyoag.doctormanager.R;
import project.kyawmyoag.doctormanager.SalesManager;
import project.kyawmyoag.doctormanager.SalesPerson;
import project.kyawmyoag.doctormanager.SalespersonMain;
import project.kyawmyoag.doctormanager.SessionManager;

public class MainActivity extends AppCompatActivity{

    private TextView manager,salesperson;
    private Button login;
    private TextInputEditText inputEmail, inputPassword;
    private Button forgot_password;
    private FirebaseAuth auth;
    private RadioGroup radioGroup_type;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private String email, password;
    private int flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SessionManager sm = new SessionManager(getApplicationContext());
            HashMap<String,String> details = sm.getUserDetails();
            String tmp1 = details.get("id");
            String tmp2 = details.get("role");
            if(!TextUtils.isEmpty(tmp1)&& !TextUtils.isEmpty(tmp2)){
                Toast.makeText(this, "အကောင့်ဝင်ခြင်း "+tmp1+" အောင်မြင်သည်", Toast.LENGTH_SHORT).show();

                if(tmp2.equals("Manager")){
                    goto_Manager();
                }
                else {
                    goto_Salesperson();
                }
            }
        }

        setContentView(R.layout.login);

        manager = findViewById(R.id.Manager);
        salesperson=findViewById(R.id.salesperson);
        login=findViewById(R.id.login_press);
        radioGroup_type = findViewById(R.id.radioGroup_type_person);
        inputEmail = findViewById(R.id.emailid);
        inputPassword = findViewById(R.id.password);
        forgot_password = findViewById(R.id.btn_reset_password);
        //
        progressBar = findViewById(R.id.spinkit);
        Sprite wave = new Wave();
        progressBar.setIndeterminate(wave.isVisible());


        auth = FirebaseAuth.getInstance();


        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpSalesManager.class);
                startActivity(intent);
                finish();
            }
        });

        salesperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpSalesperson.class);
                startActivity(intent);
                finish();
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();



                if (TextUtils.isEmpty(email)) {
                    Snackbar snackbar = Snackbar.make(v, "အီးမေးလ်ထည့်ပါ!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar snackbar = Snackbar.make(v, "စကားဝှက်ထည့်ပါ!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                                else {
                                    if (radioGroup_type.getCheckedRadioButtonId()==-1){
                                        Toast.makeText(MainActivity.this, "ရောင်းသူ (သို့) မန်နေဂျာ ရွေးပါ!!", Toast.LENGTH_LONG).show();
                                        goto_MainActivity();
                                        return;

                                    }

                                    if(RadioButtonSelect(radioGroup_type.getCheckedRadioButtonId()).equals("မန်နေဂျာ")){

                                        databaseRef = FirebaseDatabase.getInstance().getReference("Manager");
                                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                    SalesManager sm1 = snapshot.getValue(SalesManager.class);
                                                    if(sm1.getEmail().equals(email)) {
                                                        SessionManager sm = new SessionManager(getApplicationContext());
                                                        sm.createLoginSession(snapshot.getKey(), "Manager");
                                                        flag = 1;
                                                        progressBar.setVisibility(View.GONE);
                                                        goto_Manager();
                                                        break;
                                                    }
                                                }
                                                // authentication done but chose wrong radio button
                                                if(flag != 1){
                                                    Toast.makeText(getApplicationContext(),"ထိုအီးမေးလ်သည် မန်နေဂျာအဖြစ်မှတ်ပုံမတင်ထားပါ!!",Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    goto_MainActivity();

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                    else{
                                        databaseRef = FirebaseDatabase.getInstance().getReference("Salesperson");
                                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    SalesPerson sm1 = snapshot.getValue(SalesPerson.class);
                                                    if(sm1.getEmailId().equals(email)){
                                                        SessionManager sm = new SessionManager(getApplicationContext());
                                                        flag=1;
                                                        sm.createLoginSession(snapshot.getKey().toString(), "Salesperson");
                                                        progressBar.setVisibility(View.GONE);
                                                        goto_Salesperson();
                                                    }
                                                }
                                                if(flag != 1){
                                                    Toast.makeText(getApplicationContext(),"ထိုအီးမေးလ်သည် ရောင်းသူအဖြစ်မှတ်ပုံမတင်ထားပါ!!",Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    goto_MainActivity();

                                                }

                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });

                                    }
                                }
                            }
                        });
            }
        });

        // for underline purpose(UI)
        SpannableString text = new SpannableString("မန်နေဂျာစာရင်းသွင်းရန်");
        text.setSpan(new UnderlineSpan(), 0, 20, 0);
        manager.setText(text);

        // for underline purpose(UI)
        SpannableString text1 = new SpannableString("ရောင်းသူစာရင်းသွင်းရန်");
        text1.setSpan(new UnderlineSpan(), 0, 20, 0);
        salesperson.setText(text1);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                if(TextUtils.isEmpty(email)){

                    Snackbar snackbar = Snackbar.make(view,"အပေါ်တွင် အီးမေးလ်ထည့်ပြီး နောက်တစ်ကြိမ်ထပ်နှိပ်ပါ!!",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "စကားဝှက်ပြောင်းရန် အချက်အလက်များ သင့်အီးမေးလ်ထံပေးပို့ထားပါသည်!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "အီးမေးလ်ပြောင်းခြင်းမအောင်မြင်ပါ!", Toast.LENGTH_LONG).show();
                                    }

                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                }
            }
        });
    }

    void goto_Manager() {
        Intent intent=new Intent(MainActivity.this,ManagerMain.class);
        startActivity(intent);
        finish();
    }

    void goto_Salesperson() {
        Intent intent=new Intent(MainActivity.this,SalespersonMain.class);
        startActivity(intent);
        finish();

    }

    void goto_MainActivity(){
        SessionManager sm = new SessionManager(getApplicationContext());
        sm.logoutUser();
        auth.signOut();
        Intent intent=new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    String RadioButtonSelect(int selectId){
        RadioButton radioButton = findViewById(selectId);
        return radioButton.getText().toString();

    }
}