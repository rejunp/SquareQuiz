package edu.calstatela.squarequiz.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.calstatela.squarequiz.models.GameMaster;

public class MainUtil {
    private static final String TAG = "MainUtil";
    private static String mCurrentUser;
    private static String mCurrentGameId;
    private static String mLeaderId;
    private static String mQuizLink;

    private static String mShareQuizLink;
    private static GameMaster gm;

    public static GameMaster getGm() {
        return gm;
    }

    public static void setGm(GameMaster gm) {
        MainUtil.gm = gm;
    }

    public static String getQuizLink() {
        return mQuizLink;
    }

    public static void setQuizLink(String mQuizLink) {
        MainUtil.mQuizLink = mQuizLink;
    }

    public static String getLeaderId() {
        return mLeaderId;
    }

    public static void setLeaderId(String mLeaderId) {
        MainUtil.mLeaderId = mLeaderId;
    }

    public static String getCurrentGameId() {
        return mCurrentGameId;
    }

    public static void setCurrentGameId(String mCurrentGameId) {
        MainUtil.mCurrentGameId = mCurrentGameId;
    }

    public static Integer isGameActive() {
        Log.d(TAG, "isGameActive: Inside...");

        if (mCurrentGameId != "") {
            Log.d(TAG, "isGameActive: User : " + mCurrentUser);
            if (mCurrentUser == mLeaderId) {
                Log.d(TAG, "isGameActive: Leader " + mLeaderId);
                return 2;//My Game Active
            } else {
                Log.d(TAG, "isGameActive: Leader Not me " + mLeaderId);
                return 1;//Game Active but not mine
            }
        } else {
            Log.d(TAG, "isGameActive: Not Active...");
            return 0; //Game not active.
        }
    }

    public static Boolean checkLogin() {
        Log.d(TAG, "checkLogin: Inside ...");
        FirebaseUser currentUser = null;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "checkLogin: Checking user ... ");
        if (currentUser != null) {
            mCurrentUser = currentUser.getEmail().trim();
            Log.d(TAG, "checkLogin: Found... Email --> " + mCurrentUser);
            return true;
        } else {
            Log.d(TAG, "checkLogin: Not Logged in ...");
            return false;
        }
    }

    public static String getCurrentUser() {
        return mCurrentUser;
    }

    public static void setCurrentUser(String pCurrentUser) {
        mCurrentUser = pCurrentUser;
    }

    public static String getmShareQuizLink() {
        return mShareQuizLink;
    }

    public static void setmShareQuizLink(String mShareQuizLink) {
        MainUtil.mShareQuizLink = mShareQuizLink;
    }

    public static void clearUserGlobals() {
        Log.d(TAG, "clearUserGlobals: Clearing globals...");
        mCurrentUser = null;
        mCurrentGameId = null;
        mLeaderId = null;
        mQuizLink = null;
        gm = null;
    }
}
