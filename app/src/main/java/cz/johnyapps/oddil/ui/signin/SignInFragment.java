package cz.johnyapps.oddil.ui.signin;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cz.johnyapps.oddil.R;
import cz.johnyapps.oddil.SoftKeyboardUtils;
import cz.johnyapps.oddil.ui.profile.ProfileViewModel;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignInFragment";

    private Context context;
    private FirebaseAuth firebaseAuth;
    private ProfileViewModel profileViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        context = getContext();
        firebaseAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_signin, container, false);

        Button signInButtom = parent.findViewById(R.id.SignInButton);
        signInButtom.setOnClickListener(this);

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupViewModel();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.SignInButton) {
            signIn();
        }
    }

    private void setupViewModel() {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        profileViewModel = provider.get(ProfileViewModel.class);
    }

    private void signIn() {
        View parent = getView();

        if (parent != null) {
            EditText emailEditText = parent.findViewById(R.id.emailEditText);
            EditText passwordEditText = parent.findViewById(R.id.passwordEditText);

            SoftKeyboardUtils.hideKeyboardFrom(context, emailEditText, passwordEditText);

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                handleSignInResult(task);
                            }
                        });
            } else {
                Toast.makeText(context, "Email and password must be filled!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "signIn: view is null");
        }
    }

    private void handleSignInResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Log.i(TAG, "handleSignInResult: signIn success");

            FirebaseUser user = firebaseAuth.getCurrentUser();
            profileViewModel.setFirebaseUser(user);
            profileViewModel.fetchUserData();

            if (user != null) {
                Toast.makeText(context, "Authentication success", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "handleSignInResult: signIn failed", task.getException());
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        }
    }
}
