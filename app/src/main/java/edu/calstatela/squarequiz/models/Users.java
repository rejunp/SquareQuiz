package edu.calstatela.squarequiz.models;

/*import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties*/
public class Users {
    public String uid;
    public String emailId;
    public String name;
    //public String defaultQuizLink;

    public Users() {
    }

    public Users(String uid, String emailId, String name) {
        this.uid = uid;
        this.emailId = emailId;
        this.name = name;
        //this.defaultQuizLink = defaultQuizLink;
    }

    public String getUid() {
        return uid;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getName() {
        return name;
    }

    //public String getDefaultQuizLink() {
    //    return defaultQuizLink;
    //}
}
