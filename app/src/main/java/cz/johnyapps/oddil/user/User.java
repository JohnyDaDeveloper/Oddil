package cz.johnyapps.oddil.user;

import android.net.Uri;

public class User {
    public String uid;

    public PrivateInfo privateInfo;
    public PublicInfo publicInfo;

    public Uri profilePicture;

    public User(String uid) {
        privateInfo = new PrivateInfo();
        publicInfo = new PublicInfo();
    }

    public User(String uid, String name) {
        this.uid = uid;

        privateInfo = new PrivateInfo();

        publicInfo = new PublicInfo();
        publicInfo.name = name;
    }

    public String getName() {
        return publicInfo.name;
    }

    public String getAbout() {
        return publicInfo.about;
    }

    public void setAbout(String about) {
        publicInfo.about = about;
    }

    public String getEmail() {
        return privateInfo.email;
    }
}
