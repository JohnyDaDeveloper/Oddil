package cz.johnyapps.oddil.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import cz.johnyapps.oddil.R;
import cz.johnyapps.oddil.SoftKeyboardUtils;
import cz.johnyapps.oddil.user.ProfilePictureUtils;
import cz.johnyapps.oddil.user.User;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";
    private static final int RESULT_PICK_PROFILE_PICTURE = 0;

    private Context context;
    private FirebaseAuth firebaseAuth;
    private ProfileViewModel profileViewModel;
    private FirebaseFirestore database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        context = getContext();

        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_profile, container, false);

        Button signOutButton = parent.findViewById(R.id.SignOutButton);
        signOutButton.setOnClickListener(this);

        Button saveBioButton = parent.findViewById(R.id.SaveAboutButton);
        saveBioButton.setOnClickListener(this);

        Button uploadProfilePictureButton = parent.findViewById(R.id.UploadProfilePictureButton);
        uploadProfilePictureButton.setOnClickListener(this);

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupViewModel();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        if (view.getId() == R.id.SignOutButton) {
            signOut();
        } else if (view.getId() == R.id.SaveAboutButton) {
            saveAbout();
        } else if (view.getId() == R.id.UploadProfilePictureButton) {
            pickProfilePicture();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PICK_PROFILE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uploadProfilePicture(data.getData());
                } else {
                    Log.w(TAG, "pickProfilePicture: result data is null");
                }
            } else {
                Log.w(TAG, "pickProfilePicture: error occurred while picking profile picture");
            }
        }
    }

    private void getProfilePicture() {
        ProfilePictureUtils profilePictureUtils = new ProfilePictureUtils();
        profilePictureUtils.setPictureDownloadListener(new ProfilePictureUtils.PictureDownloadListener() {
            @Override
            public void onSuccess(Uri profilePictureUri) {
                Log.d(TAG, "getProfilePicture: onSuccess");

                View parent = getView();

                if (parent != null) {
                    ImageView profilePictureImageView = parent.findViewById(R.id.profilePictureImageView);
                    profilePictureImageView.setImageURI(profilePictureUri);
                }

                User user = profileViewModel.getActiveUser();

                if (user != null) {
                    user.profilePicture = profilePictureUri;
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "getProfilePicture: onFailure");
            }
        });

        try {
            profilePictureUtils.download(firebaseAuth.getUid());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pickProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpg");
        startActivityForResult(intent, RESULT_PICK_PROFILE_PICTURE);
    }

    private void uploadProfilePicture(Uri profilePictureUri) {
        View parent = getView();

        if (parent != null) {
            Button uploadProfilePictureButton = parent.findViewById(R.id.UploadProfilePictureButton);
            uploadProfilePictureButton.setEnabled(false);
        }

        ProfilePictureUtils profilePictureUtils = new ProfilePictureUtils();
        profilePictureUtils.setPictureUploadListener(new ProfilePictureUtils.PictureUploadListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Profile picture successfully uploaded", Toast.LENGTH_LONG).show();
                enableUploadProfilePictureButton();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Profile picture upload failed", Toast.LENGTH_LONG).show();
                enableUploadProfilePictureButton();
            }
        });

        profilePictureUtils.upload(profilePictureUri, firebaseAuth.getUid());
    }

    private void enableUploadProfilePictureButton() {
        View parent = getView();

        if (parent != null) {
            Button uploadProfilePictureButton = parent.findViewById(R.id.UploadProfilePictureButton);
            uploadProfilePictureButton.setEnabled(true);
        }
    }

    private void signOut() {
        profileViewModel.setFirebaseUser(null);
        firebaseAuth.signOut();
    }

    private void saveAbout() {
        Log.d(TAG, "saveBio");
        Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT).show();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            View parent = getView();

            if (parent != null) {
                EditText aboutEditText = parent.findViewById(R.id.AboutEditText);
                String about = aboutEditText.getText().toString();

                SoftKeyboardUtils.hideKeyboardFrom(context, aboutEditText);
                profileViewModel.setAbout(about);

                database.collection("users").document(firebaseUser.getUid()).collection("data").document("public")
                        .update("about", about)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "saveAbout: onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "saveAbout: onFailure");
                            }
                        });
            } else {
                Log.w(TAG, "saveBio: view is null!");
            }
        } else {
            Log.e(TAG, "saveBio: user is not signed!");
        }
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        profileViewModel = provider.get(ProfileViewModel.class);

        profileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                userChanged(user);
            }
        });
    }

    private void userChanged(User user) {
        View parent = getView();

        if (parent != null) {
            TextView nameTextView = parent.findViewById(R.id.nameTextView);
            TextView emailTextView = parent.findViewById(R.id.emailTextView);
            TextView aboutEditText = parent.findViewById(R.id.AboutEditText);
            ImageView profilePictureImageView = parent.findViewById(R.id.profilePictureImageView);

            if (user != null) {
                Log.d(TAG, "userChanged: " + user.getName());

                if (user.profilePicture == null) {
                    getProfilePicture();
                }

                nameTextView.setText(user.getName());
                emailTextView.setText(user.getEmail());
                aboutEditText.setText(user.getAbout());
                profilePictureImageView.setImageURI(user.profilePicture);
            } else {
                Log.d(TAG, "userChanged: null");

                nameTextView.setText(null);
                emailTextView.setText(null);
                aboutEditText.setText(null);
                profilePictureImageView.setImageURI(null);
            }
        } else {
            Log.w(TAG, "userChanged: view is null!");
        }
    }
}
//https://www.youtube.com/watch?v=Gwt8M5Cf4SE&list=RDMMrKC9IusBPQk&index=11
