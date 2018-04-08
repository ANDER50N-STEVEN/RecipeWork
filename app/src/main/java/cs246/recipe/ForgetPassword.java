package cs246.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/*
        Allows the user to send an email to themselves with steps
        to reset their forgotten password
 */

public class ForgetPassword extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private Button mSendEmail;
    private EditText mEmailField;
    private Button mLoginBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();
        mSendEmail = findViewById(R.id.sendEmail);
        mEmailField = findViewById(R.id.emailInput);
        mLoginBtn = findViewById(R.id.loginButton);

        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetPassword.this, SignUp.class));
            }
        });
    }

    private void resetPassword()
    {
        String emailAddress = mEmailField.getText().toString();
        if(TextUtils.isEmpty(emailAddress))
        {
            Toast.makeText(ForgetPassword.this, "Fields are empty!", Toast.LENGTH_LONG).show();
        }
        else
        {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(ForgetPassword.this, "Email Sent!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
