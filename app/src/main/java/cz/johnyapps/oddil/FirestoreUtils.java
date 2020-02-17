package cz.johnyapps.oddil;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.johnyapps.oddil.user.PrivateInfo;
import cz.johnyapps.oddil.user.PublicInfo;

public class FirestoreUtils {
    private static final String TAG = "FirestoreUtils";

    private FirebaseFirestore database;

    public FirestoreUtils() {
        database = FirebaseFirestore.getInstance();
    }

    public void fetchPrivateInfo(final String uid) {
        database.collection("users").document(uid).collection("data").document("private")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            assert document != null;
                            if (document.exists()) {
                                Log.d(TAG, "fetchPrivateInfo: success");

                                if (onPrivateInfoListener != null) {
                                    PrivateInfo privateInfo = new PrivateInfo();
                                    privateInfo.fromMap(document.getData());
                                    onPrivateInfoListener.onSuccess(privateInfo);
                                }

                                return;
                            } else {
                                Log.w(TAG, "fetchPrivateInfo: no such document");
                            }
                        } else {
                            Log.d(TAG, "fetchPrivateInfo: task failed");

                            Exception e = task.getException();

                            if (e != null) {
                                e.printStackTrace();
                            }
                        }

                        if (onPrivateInfoListener != null) {
                            onPrivateInfoListener.onFail();
                        }
                    }
                });
    }

    private OnPrivateInfoListener onPrivateInfoListener;
    public interface OnPrivateInfoListener {
        void onSuccess(PrivateInfo privateInfo);
        void onFail();
    }

    public void setOnPrivateInfoListener(OnPrivateInfoListener onPrivateInfoListener) {
        this.onPrivateInfoListener = onPrivateInfoListener;
    }

    public void fetchPublicInfo(String uid) {
        database.collection("users").document(uid).collection("data").document("public")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            assert document != null;
                            if (document.exists()) {
                                Log.d(TAG, "fetchPublicInfo: success");

                                if (onPublicInfoListener != null) {
                                    PublicInfo publicInfo = new PublicInfo();
                                    publicInfo.fromMap(document.getData());
                                    onPublicInfoListener.onSuccess(publicInfo);
                                }

                                return;
                            } else {
                                Log.w(TAG, "fetchPublicInfo: no such document");
                            }
                        } else {
                            Log.d(TAG, "fetchPublicInfo: task failed");

                            Exception e = task.getException();

                            if (e != null) {
                                e.printStackTrace();
                            }
                        }

                        if (onPublicInfoListener != null) {
                            onPublicInfoListener.onFail();
                        }
                    }
                });
    }

    private OnPublicInfoListener onPublicInfoListener;
    public interface OnPublicInfoListener {
        void onSuccess(PublicInfo publicInfo);
        void onFail();
    }

    public void setOnPublicInfoListener(OnPublicInfoListener onPublicInfoListener) {
        this.onPublicInfoListener = onPublicInfoListener;
    }
}
