package com.cscorner.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, inputFullName;
    private Button buttonSignUp;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        inputFullName = findViewById(R.id.input_full_name);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonSignUp.setOnClickListener(v -> registerUser());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void registerUser() {
        String fullName = inputFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Check if email is already registered
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                if (emailExists) {
                    Toast.makeText(this, "Email already in use, please login", Toast.LENGTH_LONG).show();
                } else {
                    // 🔹 Email not registered yet, create new user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(regTask -> {
                                if (regTask.isSuccessful()) {
                                    // Optional: store full name in user profile
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.updateProfile(
                                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                        .setDisplayName(fullName)
                                                        .build()
                                        );
                                    }
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, BottomActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this,
                                            "Error: " + regTask.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this,
                        "Error checking email: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
