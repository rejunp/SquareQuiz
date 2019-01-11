package edu.calstatela.squarequiz.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.squarequiz.R;
import edu.calstatela.squarequiz.models.QuestionAnswered;

public class ResultRecycleViewAdaptor extends RecyclerView.Adapter<ResultRecycleViewAdaptor.ResultItemViewHolder> {
    private static final String TAG = "RecycleViewAdaptor";
    List<QuestionAnswered> mResultItems = new ArrayList<>();
    private Context mContext;

    public ResultRecycleViewAdaptor() {
    }

    public ResultRecycleViewAdaptor(List<QuestionAnswered> mResultItems, Context mContext) {
        this.mResultItems = mResultItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ResultItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Inside...");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_result, viewGroup, false);
        Log.d(TAG, "onCreateViewHolder: Creating View holder");
        ResultItemViewHolder resultItemViewholder = new ResultItemViewHolder(view);
        return resultItemViewholder;
    }

    @Override
    public void onBindViewHolder(ResultItemViewHolder holder, int position) {

        holder.textViewResultRank.setText(String.valueOf(position+1));
        holder.textViewResultName.setText(mResultItems.get(position).getPlayerUid());

    }

    @Override
    public int getItemCount() {
        return mResultItems.size();
    }

    public void setResultItems(List<QuestionAnswered> resultItems) {
        mResultItems = resultItems;
        notifyDataSetChanged();
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "NewsItemViewHolder";
        TextView textViewResultRank;
        TextView textViewResultName;

        public ResultItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "NewsItemViewHolder: constructor");
            textViewResultRank = (TextView) itemView.findViewById(R.id.textViewRank);
            textViewResultName = (TextView) itemView.findViewById(R.id.textViewName);
        }
    }
}
