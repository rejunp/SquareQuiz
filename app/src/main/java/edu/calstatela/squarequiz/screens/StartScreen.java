package edu.calstatela.squarequiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.SignInActivity;
import edu.calstatela.squarequiz.models.GameMaster;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_GAME_MASTER;


public class StartScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "StartScreen";
    DatabaseReference databaseGameMaster;

    private EditText mQuizLink;
    private TextView mUsername;

    //private String mCurrentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bundle bundle=getIntent().getExtras();
        //String data=bundle.get("username").toString();

        databaseGameMaster = FirebaseDatabase.getInstance().getReference(T_GAME_MASTER);

        setContentView(R.layout.activity_startpage);
        findViewById(R.id.btnStartQuiz).setOnClickListener(this);
        findViewById(R.id.btnJoinQuiz).setOnClickListener(this);
        findViewById(R.id.btnResult).setOnClickListener(this);
        findViewById(R.id.btnStatus).setOnClickListener(this);
        mQuizLink = (EditText) findViewById(R.id.textQuizLink);
        if (MainUtil.getmShareQuizLink() != "") {
            mQuizLink.setText(MainUtil.getmShareQuizLink());
        }
        mUsername = (TextView) findViewById(R.id.textViewUserName);
        mUsername.setText(MainUtil.getCurrentUser());

    }

    /*    private void createNewGame() {
            Log.d(TAG, "createNewGame: Inside...");
            String id = databaseGameMaster.push().getKey();
            Log.d(TAG, "createNewGame: id : " + id);
            GameMaster gm = new GameMaster(id, MainUtil.getCurrentUser(), mQuizLink.getText().toString(), "ACTIVE");
            Log.d(TAG, "createNewGame: Setting global id");
            MainUtil.setCurrentGameId(id);
            MainUtil.setGm(gm);
            MainUtil.setLeaderId(MainUtil.getCurrentUser());
            databaseGameMaster.child(id).setValue(gm);
        }*/
    private void createNewGame() {
        Log.d(TAG, "createNewGame: Inside...");
        Query query = databaseGameMaster.orderByChild("quizLink").equalTo(mQuizLink.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    String id = databaseGameMaster.push().getKey();
                    Log.d(TAG, "createNewGame: id : " + id);
                    GameMaster gm = new GameMaster(id, MainUtil.getCurrentUser(), mQuizLink.getText().toString(), "ACTIVE");
                    Log.d(TAG, "createNewGame: Setting global id");
                    MainUtil.setCurrentGameId(id);
                    MainUtil.setGm(gm);
                    MainUtil.setLeaderId(MainUtil.getCurrentUser());
                    databaseGameMaster.child(id).setValue(gm);
                    Intent in = new Intent(StartScreen.this, QuestionSetupScreen.class);
                    startActivity(in);
                } else {
                    Toast.makeText(StartScreen.this, "Link Taken !! Please try a new link !!", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Cancelled");
            }
        });

    }

    private Boolean validateQuizLink() {
        if (TextUtils.isEmpty(mQuizLink.getText())) {
            Toast.makeText(StartScreen.this, "Quiz Link Required !", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    private void navigateTo(final String targetScr) {
        Intent targetScreen = null;
        if (targetScr.equals("RESULT")) {
            targetScreen = new Intent(StartScreen.this, ResultScreen.class);
        } else if (targetScr.equals("JOIN")) {

            targetScreen = new Intent(StartScreen.this, AnswerScreen.class);
        } else if (targetScr.equals("STATS")) {
            targetScreen = new Intent(StartScreen.this, StatsScreen.class);
        }

        String ql = mQuizLink.getText().toString();
        MainUtil.setQuizLink(ql);
        //validating if the game id is present ....
        Query query = databaseGameMaster.orderByChild("quizLink").equalTo(ql);
        //databaseGameMaster.child("issue").orderByChild("id").equalTo(0);
        final Intent finalTargetScreen = targetScreen;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: Inside... Checking if Data Exist");
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: datasnapshot exist");
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.d(TAG, "onDataChange: got data");
                        MainUtil.setGm(issue.getValue(GameMaster.class));
                        //System.out.println(gm.getGameId());
                    }
                    //Intent in = new Intent(StartScreen.this, targetScreen );
                    if (MainUtil.getGm().getActive().equals("INACTIVE") && targetScr.equals("JOIN")) {
                        Toast.makeText(StartScreen.this, "Game Inactive! Please contact " + MainUtil.getGm().getLeaderId(), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        startActivity(finalTargetScreen);
                    }
                } else {
                    Log.d(TAG, "onDataChange: No Data");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Cancelled");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (validateQuizLink()) {
            switch (v.getId()) {
                case R.id.btnStartQuiz:
                    Log.d(TAG, "onClick: Starting Quiz");
                    //if (MainUtil.isGameActive() < 2) {
                    createNewGame();
                    //Intent in = new Intent(StartScreen.this, QuestionSetupScreen.class);
                    //startActivity(in);
                    //}
                    break;
                case R.id.btnResult:
                    Log.d(TAG, "onClick: Result");
                    navigateTo("RESULT");
                    break;
                case R.id.btnJoinQuiz:
                    Log.d(TAG, "onClick: Join QL");
                    navigateTo("JOIN");
                    break;
                case R.id.btnStatus:
                    Log.d(TAG, "onClick: Status QL");
                    navigateTo("STATS");
                    break;
                default:
                    Log.d(TAG, "onClick: Invalid Option");
                    break;
            }
        }
    }

    //Menu Changes : Start
    //Muriel Menu Code Starts >>>
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(TAG, "onOptionsItemSelected: going to call refresh sync");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                MainUtil.clearUserGlobals();
                Intent in = new Intent(this, SignInActivity.class);
                startActivity(in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Menu Changes : End;

}