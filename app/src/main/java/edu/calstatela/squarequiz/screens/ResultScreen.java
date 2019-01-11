package edu.calstatela.squarequiz.screens;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.components.ResultRecycleViewAdaptor;
import edu.calstatela.squarequiz.components.ResultViewModel;
import edu.calstatela.squarequiz.models.QuestionAnswered;
import edu.calstatela.squarequiz.utils.ResultSortUtil;

public class ResultScreen extends AppCompatActivity {
    private static final String TAG = "ResultScreen";
    private TextView tvName;
    private TextView tvRank;
    private ResultRecycleViewAdaptor adapter;
    private RecyclerView recyclerView;

    private ResultViewModel viewModel;
    private ArrayList<QuestionAnswered> responses = new ArrayList<QuestionAnswered>();
    private Context cx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultpage);
        cx = this;
        tvName = findViewById(R.id.textViewName);
        tvRank = findViewById(R.id.textViewRank);
        //QuestionAnswered qa = new QuestionAnswered();
        //responses.add(qa);
        recyclerView = (RecyclerView) findViewById(R.id.result_recyclerview);
        adapter = new ResultRecycleViewAdaptor(responses, this);
//        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        viewModel = ViewModelProviders.of(this).get(ResultViewModel.class);
        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {

            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    // update the UI here with values in the snapshot
                    List<QuestionAnswered> lqa = new ArrayList<>();
                    Log.d(TAG, "onChanged: dataSnapshot.getChildrenCount()---->>>" + dataSnapshot.getChildrenCount());
                    for (DataSnapshot qa : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onChanged: ------------------>>>>>>>>" + qa.getValue(QuestionAnswered.class).getPlayerUid());
                        lqa.add(qa.getValue(QuestionAnswered.class));
                    }

                    Comparator c = Collections.reverseOrder(new ResultSortUtil());
                    Collections.sort(lqa, c);
                    //Collections.reverse(lqa);

                    adapter.setResultItems(lqa);
                    adapter.notifyDataSetChanged();
                    // recyclerView.setLayoutManager(new LinearLayoutManager(cx));
                }
            }
        });
    }
}
