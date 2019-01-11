package edu.calstatela.squarequiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.models.Question;
import edu.calstatela.squarequiz.models.QuestionAnswered;
import edu.calstatela.squarequiz.models.QuestionAsked;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_QUEST_ASKED;
import static edu.calstatela.squarequiz.utils.GlobalConstants.T_QUEST_BANK;
import static edu.calstatela.squarequiz.utils.GlobalConstants.T_RESPONSES;

public class AnswerScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AnswerScreen";

    List<String> questionIds = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    Map<String, String> rsVal = new HashMap<>();
    Map<String, Map<String, String>> result = new HashMap<>();
    DatabaseReference databaseQuestionBank;
    DatabaseReference databaseQuestionAsked;
    DatabaseReference databaseAnswers;
    private String currentQuestionId;
    private Integer currentQuestionCount = 0;
    private Integer currentAnswer;
    private Integer currentCorrectAnswer;
    private Integer totalScore;
    private Integer totalCorrectAnswers;
    private Long totalTimeDiff;
    private long startTime;
    private long endTime;
    private long timeDiff;
    private TextView mQuestion;
    private TextView mThankYou;
    private Button mOption1;
    private Button mOption2;
    private Button mOption3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_screen);
        mOption1 = (Button) findViewById(R.id.btnAnsOp1);
        mOption1.setOnClickListener(this);
        mOption2 = (Button) findViewById(R.id.btnAnsOp2);
        mOption2.setOnClickListener(this);
        mOption3 = (Button) findViewById(R.id.btnAnsOp3);
        mOption3.setOnClickListener(this);
        mQuestion = (TextView) findViewById(R.id.textViewQ1);
        mThankYou = (TextView) findViewById(R.id.textViewThankYou);
        totalScore = 0;
        totalCorrectAnswers = 0;
        totalTimeDiff = Long.valueOf(0);
        clearPage();

        databaseQuestionAsked = FirebaseDatabase.getInstance().getReference(T_QUEST_ASKED);
        Query query = databaseQuestionAsked.orderByChild("gameId").equalTo(MainUtil.getGm().getGameId());
        //databaseGameMaster.child("issue").orderByChild("id").equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.d(TAG, "onDataChange: databaseQuestionAsked got data");
                        questionIds = ds.getValue(QuestionAsked.class).getQuestionId();
                        //System.out.println(gm.getGameId());
                    }
                    Log.d(TAG, "onDataChange: building questions");
                    databaseQuestionBank = FirebaseDatabase.getInstance().getReference(T_QUEST_BANK);
                    //databaseQuestionBank.keepSynced(true);
                    for (String qid : questionIds) {
                        Log.d(TAG, "onDataChange: Looping...");
                        Query query = databaseQuestionBank.orderByChild("questionId").equalTo(qid);
                        //databaseGameMaster.child("issue").orderByChild("id").equalTo(0);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // dataSnapshot is the "issue" node with all children with id 0
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        // do something with the individual "issues"
                                        Log.d(TAG, "onDataChange: databaseQuestionBank got data");
                                        questions.add(ds.getValue(Question.class));
                                        Log.d(TAG, "onDataChange: QID --> " + ds.getValue(Question.class).getQuestionId());
                                        //System.out.println(gm.getGameId());
                                        if (questions.size() == 1) {
                                            Log.d(TAG, "onDataChange: going to call screenSetup");
                                            setupQuestions(ds.getValue(Question.class));
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: Cancelled");
                            }
                        });
                        //questions.add(getQuestion(qid));
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Cancelled");
            }
        });

    }


    private void setupQuestions(Question q) {
        Log.d(TAG, "setupQuestions: Inside");
        startTime = System.nanoTime();
        currentQuestionId = q.getQuestionId();
        currentCorrectAnswer = q.getAnswer();
        mQuestion.setText(q.getQuestion());
        mOption1.setText(q.getOption1());
        mOption2.setText(q.getOption2());
        mOption3.setText(q.getOption3());
    }

    private void clearPage() {
        Log.d(TAG, "clearPage: Inside...");
        mQuestion.setText("");
        mOption1.setText("");
        mOption2.setText("");
        mOption3.setText("");
    }

    private void nextQuestion() {
        Log.d(TAG, "nextQuestion: Inside...");
        clearPage();
        setupQuestions(questions.get(++currentQuestionCount));
    }

    private void saveAnswer() {
        Log.d(TAG, "saveAnswer: Inside");
        if (currentAnswer == currentCorrectAnswer) {
            Log.d(TAG, "saveAnswer: Answer is Correct");
            totalScore += 10;
            Log.d(TAG, "saveAnswer: Adding Up Time : " + totalTimeDiff);
            totalTimeDiff += timeDiff;
            totalCorrectAnswers += 1;
        }
        rsVal.put("answer", String.valueOf(currentAnswer));
        rsVal.put("time_taken", timeDiff + "");
        result.put(currentQuestionId, rsVal);
    }

    @Override
    public void onClick(View v) {
        endTime = System.nanoTime();
        switch (v.getId()) {
            case R.id.btnAnsOp1:
                Log.d(TAG, "onClick: Answer 1 Picked");
                currentAnswer = 0;
                break;
            case R.id.btnAnsOp2:
                Log.d(TAG, "onClick: Answer 2 Picked");
                currentAnswer = 1;
                break;
            case R.id.btnAnsOp3:
                Log.d(TAG, "onClick: Answer 3 Picked");
                currentAnswer = 2;
                break;
            default:
                Log.d(TAG, "onClick: Invalid Option");
                break;
        }
        timeDiff = endTime - startTime;
        Log.d(TAG, "onClick: Total Questions --> " + questions.size());
        Log.d(TAG, "onClick: Current Question Count --> " + currentQuestionCount);
        saveAnswer();
        if ((questions.size() - 1) > currentQuestionCount) {
            nextQuestion();
        } else {
            Log.d(TAG, "onClick: Out of questions...");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Log.d(TAG, "onClick: Total : " + totalScore);
            Log.d(TAG, "onClick: Time Diff : " + totalTimeDiff);
            Log.d(TAG, "onClick: Total Correct Answers : " + totalCorrectAnswers);
            Log.d(TAG, "onClick: Total : " + (totalScore * totalTimeDiff.intValue()));
            QuestionAnswered ans = new QuestionAnswered(MainUtil.getGm().getGameId(),
                    MainUtil.getCurrentUser(), totalScore, totalTimeDiff, totalCorrectAnswers,  result);
            databaseAnswers = FirebaseDatabase.getInstance().getReference(T_RESPONSES);
            databaseAnswers.child(MainUtil.getGm().getGameId() + "/" + auth.getUid()).setValue(ans);
            Log.d(TAG, "onClick: Answer Saved");
            clearPage();
            mThankYou.setText("Thank you for taking the \n" + MainUtil.getGm().getQuizLink() +
                    " Quiz \ncreated by \n " + MainUtil.getGm().getLeaderId() + "!");
            mThankYou.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(AnswerScreen.this, StartScreen.class);
                    AnswerScreen.this.startActivity(mainIntent);
                    AnswerScreen.this.finish();
                }
            }, 3000);


        }
    }
}
