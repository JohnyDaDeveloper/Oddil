package cz.johnyapps.oddil.ui.signup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.johnyapps.oddil.R;
import cz.johnyapps.oddil.SoftKeyboardUtils;
import cz.johnyapps.oddil.user.User;
import cz.johnyapps.oddil.ui.profile.ProfileViewModel;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignUpFragment";

    private Context context;
    private FirebaseAuth firebaseAuth;
    private ProfileViewModel profileViewModel;
    private FirebaseFirestore database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        context = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_signup, container, false);

        Button signUpButton = parent.findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(this);

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupViewModel();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.SignUpButton) {
            signUp();
        }
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        profileViewModel = provider.get(ProfileViewModel.class);
    }

    private void signUp() {
        View parent = getView();

        if (parent != null) {
            EditText nameEditText = parent.findViewById(R.id.nameEditText);
            EditText emailEditText = parent.findViewById(R.id.emailEditText);
            EditText passwordEditText = parent.findViewById(R.id.passwordEditText);

            SoftKeyboardUtils.hideKeyboardFrom(context, nameEditText, emailEditText, passwordEditText);

            final String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (password.length() > 5) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    handleSignUpResult(task, name);
                                }
                            });
                } else {
                    Toast.makeText(context, "Password must have 6 or more characters", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Name, email and password must be filled!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "signUp: view is null!");
        }
    }

    private void handleSignUpResult(Task<AuthResult> task, String name) {
        if (task.isSuccessful()) {
            Log.i(TAG, "handleSignUpResult: signUp success");
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser != null) {
                Toast.makeText(context, "Registration success", Toast.LENGTH_SHORT).show();

                User user = new User(firebaseUser.getUid(), name);
                user.privateInfo.email = firebaseUser.getEmail();

                profileViewModel.setFirebaseUser(firebaseUser);
                profileViewModel.setUser(user);

                saveUserToDatabase(user);
            } else {
                Log.e(TAG, "handleSignUpResult: Logged but user is null");
            }
        } else {
            Exception exception = task.getException();
            String message = "Registration failed";

            if (exception != null) {
                message += "\n" + exception.getMessage();
            }

            Log.w(TAG, "handleSignUpResult: signUp failed", exception);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserToDatabase(User user) {
        CollectionReference collection = database.collection("users").document(user.uid).collection("data");

        collection.document("public")
                .set(user.publicInfo.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "saveNameToDatabase: public info saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveNameToDatabase: onFailure - public");
                    }
                });

        collection.document("private")
                .set(user.privateInfo.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "saveUserToDatabase: private info saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveNameToDatabase: onFailure - private");
                    }
                });
    }
}
