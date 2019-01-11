package edu.calstatela.squarequiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.calstatela.squarequiz.screens.StartScreen;
import edu.calstatela.squarequiz.utils.MainUtil;

public class MainEmptyActivity extends AppCompatActivity {
    private static final String TAG = "MainEmptyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;
        try {
            Uri data = getIntent().getData();
            MainUtil.setmShareQuizLink(data.getQueryParameter("quizlink"));
        } catch (Exception ex) {
            Log.d(TAG, "onCreate: No link...");
        }
        // go straight to main if a token is stored
        if (MainUtil.checkLogin()) {
            activityIntent = new Intent(this, StartScreen.class);
            activityIntent.putExtra("username", MainUtil.getCurrentUser());
        } else {
            activityIntent = new Intent(this, SignInActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}