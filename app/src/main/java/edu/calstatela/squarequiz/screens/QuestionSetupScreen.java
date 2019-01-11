package edu.calstatela.squarequiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.models.Question;
import edu.calstatela.squarequiz.models.QuestionAsked;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_QUEST_ASKED;
import static edu.calstatela.squarequiz.utils.GlobalConstants.T_QUEST_BANK;

public class QuestionSetupScreen extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "QuestionSetupScreen";
    List<String> questionIds = new ArrayList<>();
    List<Question> mQuestions = new ArrayList<>();
    DatabaseReference databaseQuestionBank;
    DatabaseReference databaseQuestionAsked;
    private EditText mQuestion;
    private EditText mOption1;
    private EditText mOption2;
    private EditText mOption3;
    private TextView mLabelQuestion;
    private TextView mLabelAnswer;
    private TextView mStatusDone;
    private Spinner mSpinner;
    private Button mBtnAddMore;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        databaseQuestionBank = FirebaseDatabase.getInstance().getReference(T_QUEST_BANK);
        databaseQuestionAsked = FirebaseDatabase.getInstance().getReference(T_QUEST_ASKED);

        findViewById(R.id.btnAddMore).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        mQuestion = (EditText) findViewById(R.id.editTextQuestion);
        mOption1 = (EditText) findViewById(R.id.editTextOption1);
        mOption2 = (EditText) findViewById(R.id.editTextOption2);
        mOption3 = (EditText) findViewById(R.id.editTextOption3);
        mLabelQuestion = (TextView) findViewById(R.id.textViewLabelQuestion);
        mLabelAnswer = (TextView) findViewById(R.id.textViewLabelAnswer);
        mStatusDone = (TextView) findViewById(R.id.textViewDone);
        mBtnAddMore = (Button) findViewById(R.id.btnAddMore);
        mBtnSubmit = (Button) findViewById(R.id.btnSubmit);

        mSpinner = findViewById(R.id.spinnerAnswer);
        mSpinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Option 1");
        categories.add("Option 2");
        categories.add("Option 3");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);
    }

    private void clearPage() {
        mQuestion.setText("");
        mOption1.setText("");
        mOption2.setText("");
        mOption3.setText("");
        mSpinner.setSelection(0);
    }

    private String addToQuestionBank() {
        Log.d(TAG, "addToQuestionBank: Inside...");
        String question = mQuestion.getText().toString();
        String option1 = mOption1.getText().toString();
        String option2 = mOption2.getText().toString();
        String option3 = mOption3.getText().toString();
        Integer answer = mSpinner.getSelectedItemPosition();
        String id = databaseQuestionBank.push().getKey();
        Question q = new Question(id, MainUtil.getCurrentUser(), question, option1, option2, option3, answer);
        mQuestions.add(q);
        return id;
    }

    private void addQuestionToGame() {
        Log.d(TAG, "addQuestionToGame: Inside...");
        QuestionAsked qa = new QuestionAsked(MainUtil.getCurrentGameId(), MainUtil.getLeaderId(), questionIds);
        //String id = databaseQuestionAsked.push().getKey();
        databaseQuestionAsked.child(MainUtil.getCurrentGameId()).setValue(qa);
    }

    private void saveQuestions() {
        Log.d(TAG, "onComplete: Saving Data");
        for (Question q : mQuestions) {
            databaseQuestionBank.child(q.getQuestionId()).setValue(q);
        }
        Log.d(TAG, "saveQuestions: Clearing Global Quesitons");
        mQuestions = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddMore:
                Log.d(TAG, "onClick: Addmore");
                questionIds.add(addToQuestionBank());
                clearPage();
                break;
            case R.id.btnSubmit:
                Log.d(TAG, "onClick: Submit Quiz");
                questionIds.add(addToQuestionBank());
                addQuestionToGame();
                saveQuestions();
                clearPage();
                mQuestion.setVisibility(View.INVISIBLE);
                mOption1.setVisibility(View.INVISIBLE);
                mOption2.setVisibility(View.INVISIBLE);
                mOption3.setVisibility(View.INVISIBLE);
                mSpinner.setVisibility(View.INVISIBLE);
                mLabelQuestion.setVisibility(View.INVISIBLE);
                mLabelAnswer.setVisibility(View.INVISIBLE);
                mBtnAddMore.setVisibility(View.INVISIBLE);
                mBtnSubmit.setVisibility(View.INVISIBLE);
                mStatusDone.setText("Questions Registered to Quiz Link : " + MainUtil.getGm().getQuizLink());
                mStatusDone.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent mainIntent = new Intent(QuestionSetupScreen.this, StartScreen.class);
                        QuestionSetupScreen.this.startActivity(mainIntent);
                        QuestionSetupScreen.this.finish();
                    }
                }, 3000);

                break;
            default:
                Log.d(TAG, "onClick: Invalid Option");
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
