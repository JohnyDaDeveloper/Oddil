package cz.johnyapps.oddil.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import cz.johnyapps.oddil.FirestoreUtils;
import cz.johnyapps.oddil.user.PrivateInfo;
import cz.johnyapps.oddil.user.PublicInfo;
import cz.johnyapps.oddil.user.User;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    private MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();

    public ProfileViewModel() {

    }

    public void fetchUserData() {
        FirebaseUser firebaseUser = this.firebaseUser.getValue();

        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();

            final FirestoreUtils firestoreUtils = new FirestoreUtils();
            firestoreUtils.setOnPrivateInfoListener(new FirestoreUtils.OnPrivateInfoListener() {
                @Override
                public void onSuccess(PrivateInfo privateInfo) {
                    User u = user.getValue();

                    if (u == null) {
                        u = new User(uid);
                    }

                    u.privateInfo = privateInfo;
                    setUser(u);
                }

                @Override
                public void onFail() {

                }
            });
            firestoreUtils.setOnPublicInfoListener(new FirestoreUtils.OnPublicInfoListener() {
                @Override
                public void onSuccess(PublicInfo publicInfo) {
                    User u = user.getValue();

                    if (u == null) {
                        u = new User(uid);
                    }

                    u.publicInfo = publicInfo;
                    setUser(u);
                }

                @Override
                public void onFail() {

                }
            });

            firestoreUtils.fetchPrivateInfo(uid);
            firestoreUtils.fetchPublicInfo(uid);
        } else {
            Log.w(TAG, "fetchUserData: Firebase user is null");
        }
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Log.d(TAG, "setFirebaseUser: " + firebaseUser.getUid());
        } else {
            Log.d(TAG, "setFirebaseUser: null");
            setUser(null);
        }

        this.firebaseUser.setValue(firebaseUser);
    }

    public LiveData<FirebaseUser> getFirebaseUser() {
        return firebaseUser;
    }

    public void setUser(User user) {
        if (user != null) {
            Log.d(TAG, "setUser: " + user.getName());
        } else {
            Log.d(TAG, "setUser: null");
        }

        this.user.setValue(user);
    }

    public LiveData<User> getUser() {
        return user;
    }

    void setAbout(String about) {
        User user = this.user.getValue();

        if (user != null) {
            user.setAbout(about);
            this.user.setValue(user);
        } else {
            Log.w(TAG, "setAbout: user is null");
        }
    }

    public User getActiveUser() {
        return user.getValue();
    }
}
