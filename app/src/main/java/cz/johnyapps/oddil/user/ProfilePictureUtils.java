package cz.johnyapps.oddil.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ProfilePictureUtils {
    private static final String TAG = "ProfilePictureUtils";

    private StorageReference storage;

    public ProfilePictureUtils() {
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void upload(Uri picture, String uid) {
        final StorageReference pictureReference = storage.child("profile_pictures/" + uid + ".jpg");

        pictureReference.putFile(picture)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pictureUploadListener.onSuccess();

                        if (pictureUploadListener != null) {
                            pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: " + uri);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure", e);
                        e.printStackTrace();

                        if (pictureUploadListener != null) {
                            pictureUploadListener.onFailure();
                        }
                    }
                });
    }

    private PictureUploadListener pictureUploadListener;
    public interface PictureUploadListener {
        void onSuccess();
        void onFailure();
    }

    public void setPictureUploadListener(PictureUploadListener pictureUploadListener) {
        this.pictureUploadListener = pictureUploadListener;
    }

    public void download(String uid) throws IOException {
        if (uid != null) {
            Log.d(TAG, "download: profile_pictures/resized/" + uid + "_680x680.jpg");

            final File picture = File.createTempFile(uid, "jpg");
            final StorageReference pictureReference = storage.child("profile_pictures/resized/" + uid + "_680x680.jpg");

            pictureReference.getFile(picture)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess");

                            if (pictureDownloadListener != null) {
                                pictureDownloadListener.onSuccess(Uri.fromFile(picture));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "onFailure", e);
                            e.printStackTrace();

                            if (pictureDownloadListener != null) {
                                pictureDownloadListener.onFailure();
                            }
                        }
                    });
        }
    }

    private PictureDownloadListener pictureDownloadListener;
    public interface PictureDownloadListener {
        void onSuccess(Uri profilePictureUri);
        void onFailure();
    }

    public void setPictureDownloadListener(PictureDownloadListener pictureDownloadListener) {
        this.pictureDownloadListener = pictureDownloadListener;
    }
}
