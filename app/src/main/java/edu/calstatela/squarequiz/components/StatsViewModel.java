package edu.calstatela.squarequiz.components;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.calstatela.squarequiz.models.GameMaster;
import edu.calstatela.squarequiz.models.QuestionAnswered;
import edu.calstatela.squarequiz.models.QuestionAsked;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_GAME_MASTER;
import static edu.calstatela.squarequiz.utils.GlobalConstants.T_QUEST_ASKED;
import static edu.calstatela.squarequiz.utils.GlobalConstants.T_RESPONSES;

public class StatsViewModel extends ViewModel {
    private static final String TAG = "ResultViewModel";
    private static final DatabaseReference PLAYER_CNT_GAME_REF =
            FirebaseDatabase.getInstance().getReference("/" + T_RESPONSES + "/" + MainUtil.getGm().getGameId());
    private static final DatabaseReference GAME_MASTER_REF =
            FirebaseDatabase.getInstance().getReference("/" + T_GAME_MASTER /*+ "/" + MainUtil.getGm().getGameId()*/);
    private final FirebaseQueryLiveData playerCntLiveData = new FirebaseQueryLiveData(PLAYER_CNT_GAME_REF.orderByChild("totalScore"));
    private final MediatorLiveData<QuestionAnswered> playerCntMediatorLiveData = new MediatorLiveData<>();
    private final FirebaseQueryLiveData gameMasterLiveData = new FirebaseQueryLiveData(GAME_MASTER_REF.orderByChild("gameId").equalTo(MainUtil.getGm().getGameId()));
    private final MediatorLiveData<GameMaster> gameMasterMediatorLiveData = new MediatorLiveData<>();
    private Integer mQuestionCount;


    public StatsViewModel() {
        mQuestionCount = 0;

        playerCntMediatorLiveData.addSource(playerCntLiveData, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: <<<<<<<<<<<<<<<<<<<<<<--------------->>>>>>>>" + dataSnapshot.getValue(QuestionAnswered.class).getPlayerUid());
                            playerCntMediatorLiveData.postValue(dataSnapshot.getValue(QuestionAnswered.class));
                        }
                    }).start();
                } else {
                    playerCntMediatorLiveData.setValue(null);
                }
            }
        });

        gameMasterMediatorLiveData.addSource(gameMasterLiveData, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gameMasterMediatorLiveData.postValue(dataSnapshot.getValue(GameMaster.class));
                        }
                    }).start();
                } else {
                    gameMasterMediatorLiveData.setValue(null);
                }
            }
        });

        DatabaseReference databaseQuestionAsked = FirebaseDatabase.getInstance().getReference(T_QUEST_ASKED);
        Query query = databaseQuestionAsked.orderByChild("gameId").equalTo(MainUtil.getGm().getGameId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.d(TAG, "onDataChange: databaseQuestionAsked got data");
                        mQuestionCount = ds.getValue(QuestionAsked.class).getQuestionId().size();
                        //System.out.println(gm.getGameId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public MediatorLiveData<QuestionAnswered> getPlayerCntMediatorLiveData() {
        return playerCntMediatorLiveData;
    }

    public void toggleStatus(final String status) {
        Log.d(TAG, "toggleStatus: Inside...");
      //  new Thread(new Runnable() {
         //   @Override
          //  public void run() {
                FirebaseDatabase.getInstance().getReference(T_GAME_MASTER).child(MainUtil.getGm().getGameId()).child("active").setValue(status);
        Log.d(TAG, "toggleStatus: Updated...");
       //     }
      //  }).start();
    }

    @NonNull
    public LiveData<DataSnapshot> getPlayerCntDSLiveData() {
        return playerCntLiveData;
    }

    @NonNull
    public LiveData<DataSnapshot> getgameMasterLiveData() {
        return gameMasterLiveData;
    }


    public Integer getmQuestionCount() {
        return mQuestionCount;
    }


}
