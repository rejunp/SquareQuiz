package edu.calstatela.squarequiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.calstatela.squarequiz.models.Users;
import edu.calstatela.squarequiz.screens.StartScreen;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_USERS;

public class SignInActivity extends AppCompatActivity
        implements View.OnClickListener {
    private final String TAG = "FB_SIGNIN";
    DatabaseReference databaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etPass;
    private EditText etEmail;

    /**
     * Standard Activity lifecycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.d(TAG, "onCreate: loading Users reference...");
        databaseUsers = FirebaseDatabase.getInstance().getReference(T_USERS);
        Log.d(TAG, "onCreate: user loaded");
        // Set up click handlers and view item references
        findViewById(R.id.btnCreate).setOnClickListener(this);
        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmailAddr);
        etPass = (EditText) findViewById(R.id.etPassword);

        //  Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //  Attach a new AuthListener to detect sign in and out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "Signed in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "Currently signed out");
                }
            }
        };

        updateStatus();
    }

    /**
     * When the Activity starts and stops, the app needs to connect and
     * disconnect the AuthListener
     */
    @Override
    public void onStart() {
        super.onStart();
        //  add the AuthListener
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //  Remove the AuthListener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signUserIn();
                break;

            case R.id.btnCreate:
                createUserAccount();
                break;

            case R.id.btnSignOut:
                signUserOut();
                break;
        }
    }

    private boolean checkFormFields() {
        String email, password;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Email Required");
            return false;
        }
        if (password.isEmpty()) {
            etPass.setError("Password Required");
            return false;
        }

        return true;
    }

    private void updateStatus() {
        TextView tvStat = (TextView) findViewById(R.id.tvSignInStatus);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvStat.setText("Signed in: " + user.getEmail());
        } else {
            tvStat.setText("Signed Out");
        }
    }

    private void updateStatus(String stat) {
        TextView tvStat = (TextView) findViewById(R.id.tvSignInStatus);
        tvStat.setText(stat);
    }

    private void signUserIn() {
        if (!checkFormFields())
            return;

        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        //  sign the user in with email and password credentials
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Signed in", Toast.LENGTH_SHORT)
                                            .show();
                                    startQuiz();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Sign in failed", Toast.LENGTH_SHORT)
                                            .show();
                                }

                                updateStatus();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            updateStatus("Invalid password.");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            updateStatus("No account with this email.");
                        } else {
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });
    }

    public void startQuiz() {
        Log.d(TAG, "startQuiz: Inside...");
        Intent in = new Intent(SignInActivity.this, StartScreen.class);
        MainUtil.setCurrentUser(mAuth.getCurrentUser().getEmail());
        in.putExtra("username", mAuth.getCurrentUser());
        startActivity(in);
    }

    private void signUserOut() {
        //  sign the user out
        mAuth.signOut();
        updateStatus();
    }

    private void createUserAccount() {
        if (!checkFormFields())
            return;

        final String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        //  Create the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Logging User to DB ------------>>>>");
                                    MainUtil.setCurrentUser(email);
                                    //String id = databaseUsers.push().getKey();
                                    String id = mAuth.getUid();
                                    Log.d(TAG, "onComplete: ID--" + id);
                                    Users user = new Users(id, email, email);
                                    Log.d(TAG, "onComplete: Saving Data");
                                    databaseUsers.child(id).setValue(user);

                                    Log.d(TAG, "onComplete: User Added...");
                                    Toast.makeText(SignInActivity.this, "User created", Toast.LENGTH_SHORT)
                                            .show();
                                    startQuiz();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Account creation failed", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            updateStatus("This email address is already in use.");
                        } else {
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });
    }
}
