package edu.calstatela.squarequiz.screens;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.components.StatsViewModel;
import edu.calstatela.squarequiz.models.GameMaster;
import edu.calstatela.squarequiz.models.QuestionAnswered;
import edu.calstatela.squarequiz.utils.MainUtil;

public class StatsScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "StatsScreen";
    StatsViewModel viewModel;
    private Button mBtnCreated;
    private Button mBtnQuestCnt;
    private Button mBtnPlayerCnt;
    private Button mBtnTop;
    private Button mBtnAvg;
    private Button mBtnResume;
    private Button mBtnStop;
    private Button mBtnStatus;
    private Button mBtnShare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        viewModel = ViewModelProviders.of(this).get(StatsViewModel.class);

        findViewById(R.id.buttonResume).setOnClickListener(this);
        findViewById(R.id.buttonStop).setOnClickListener(this);
        findViewById(R.id.buttonShare).setOnClickListener(this);

        mBtnCreated = (Button) findViewById(R.id.buttonCreator);
        mBtnQuestCnt = (Button) findViewById(R.id.buttonQuestCnt);
        mBtnPlayerCnt = (Button) findViewById(R.id.buttonPlayerCnt);
        mBtnTop = (Button) findViewById(R.id.buttonScrTop);
        mBtnAvg = (Button) findViewById(R.id.buttonScrAvg);
        mBtnResume = (Button) findViewById(R.id.buttonResume);
        mBtnStop = (Button) findViewById(R.id.buttonStop);
        mBtnShare = (Button) findViewById(R.id.buttonShare);
        mBtnStatus = (Button) findViewById(R.id.buttonOnline);
        operationControl();
        updateStats();

    }

    private void operationControl() {
        Log.d(TAG, "operationControl: Inside...");
        Log.d(TAG, "operationControl: GMLeader" + MainUtil.getGm().getLeaderId());
        Log.d(TAG, "operationControl: GMLeader" + MainUtil.getCurrentUser());
        if (MainUtil.getGm().getLeaderId().toString().equals(MainUtil.getCurrentUser().toString())) {
            Log.d(TAG, "operationControl: Leader");
            mBtnResume.setVisibility(View.VISIBLE);
            mBtnStop.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "operationControl: Not leader");
            mBtnResume.setVisibility(View.INVISIBLE);
            mBtnStop.setVisibility(View.INVISIBLE);
        }

    }

    private void updateStats() {
        Log.d(TAG, "updateStats: Inside...");

        mBtnCreated.setText("Created by :\n" + MainUtil.getGm().getLeaderId());


        LiveData<DataSnapshot> playerCntLiveData = viewModel.getPlayerCntDSLiveData();


        playerCntLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                Integer avg, top, childCnt;
                if (dataSnapshot != null) {

                    Log.d(TAG, "onChanged: dataSnapshot.getChildrenCount()---->>>" + dataSnapshot.getChildrenCount());
                    childCnt = new Long(dataSnapshot.getChildrenCount()).intValue();
                    avg = 0;
                    top = 0;
                    for (DataSnapshot qa : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onChanged: ------------------>>>>>>>>" + qa.getValue(QuestionAnswered.class).getPlayerUid());
                        avg += qa.getValue(QuestionAnswered.class).getCorrectAnswerCnt();
                        Log.d(TAG, "onChanged: Current Temp2 -1-->>>" + top);
                        top = top > qa.getValue(QuestionAnswered.class).getCorrectAnswerCnt() ? top : qa.getValue(QuestionAnswered.class).getCorrectAnswerCnt();
                        Log.d(TAG, "onChanged: Current Temp2 -2-->>>" + top);
                    }
                    if(childCnt>0) {
                        avg = avg / childCnt;
                    }else{
                        avg=0;
                    }
                    Log.d(TAG, "onChanged: childCnt---->>>" + childCnt);
                    mBtnPlayerCnt.setText("Players : " + childCnt);
                    mBtnTop.setText("Top : " + top);
                    mBtnAvg.setText("Avg.: " + avg);
                    mBtnQuestCnt.setText("Questions : " + viewModel.getmQuestionCount());
                }
            }
        });


        LiveData<DataSnapshot> gameMasterLiveData = viewModel.getgameMasterLiveData();


        gameMasterLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                Integer avg, top, childCnt;
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.d(TAG, "onDataChange: GAMEMASTER----------->>>>" + dataSnapshot.getChildrenCount());
                        //Log.d(TAG, "onDataChange: GAMEMASTER----------->>>>" + ds.getValue(GameMaster.class).getLeaderId());
                        String active = ds.getValue(GameMaster.class).getLeaderId();
                        Log.d(TAG, "onDataChange: GAMEMASTER----------->>>>" + active);

                        if (ds.getValue(GameMaster.class).getActive().equals("ACTIVE")) {
                            Log.d(TAG, "onChanged: Status ACTIVE");
                            //IF game Active:
                            mBtnStatus.setText("ACTIVE");
                            mBtnStatus.setBackgroundColor(getResources().getColor(R.color.qActive));
                        } else {
                            Log.d(TAG, "onChanged: Status PAUSED");
                            //IF game not active
                            mBtnStatus.setText("INACTIVE");
                            //TODO:block if inactive
                            mBtnStatus.setBackgroundColor(getResources().getColor(R.color.qDown));
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonResume:
                Log.d(TAG, "onClick: Move to active");
                viewModel.toggleStatus("ACTIVE");
                break;
            case R.id.buttonStop:
                Log.d(TAG, "onClick: Move to inactive");
                viewModel.toggleStatus("INACTIVE");
                break;
            case R.id.buttonShare:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"http://www.square-quiz.com/launchapp?quizlink="+MainUtil.getGm().getQuizLink());
                startActivity(Intent.createChooser(shareIntent,"Sharing Using"));
                break;
            default:
                Log.d(TAG, "onClick: Invalid Option");
                break;
        }


    }
}
