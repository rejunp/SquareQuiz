package edu.calstatela.squarequiz.components;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.calstatela.squarequiz.models.QuestionAnswered;
import edu.calstatela.squarequiz.utils.MainUtil;

import static edu.calstatela.squarequiz.utils.GlobalConstants.T_RESPONSES;

public class ResultViewModel extends ViewModel {
    private static final String TAG = "ResultViewModel";
    /*    private static final DatabaseReference GAME_REF =
                FirebaseDatabase.getInstance().getReference("/" + T_RESPONSES + "/" + MainUtil.getGm().getGameId());

        private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(GAME_REF.orderByChild("totalScore"));
        private final MediatorLiveData<QuestionAnswered> resultLiveData = new MediatorLiveData<>();*/
    private DatabaseReference GAME_REF;

    private FirebaseQueryLiveData liveData;
    private MediatorLiveData<QuestionAnswered> resultLiveData;

    public ResultViewModel() {
        GAME_REF =
                FirebaseDatabase.getInstance().getReference("/" + T_RESPONSES + "/" + MainUtil.getGm().getGameId());
        liveData = new FirebaseQueryLiveData(GAME_REF.orderByChild("totalScore"));
        resultLiveData = new MediatorLiveData<>();
        // Set up the MediatorLiveData to convert DataSnapshot objects into HotStock objects
        resultLiveData.addSource(liveData, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: <<<<<<<<<<<<<<<<<<<<<<--------------->>>>>>>>" + dataSnapshot.getValue(QuestionAnswered.class).getPlayerUid());
                            resultLiveData.postValue(dataSnapshot.getValue(QuestionAnswered.class));
                        }
                    }).start();
                } else {
                    resultLiveData.setValue(null);
                }
            }
        });

        //mRepository = new NewsItemRepository(application);
        //mAllNewsItems = mRepository.getAllNewsItems();
    }

    public MediatorLiveData<QuestionAnswered> getResultLiveData() {
        return resultLiveData;
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
