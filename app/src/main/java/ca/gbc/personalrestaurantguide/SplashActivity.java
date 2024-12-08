package ca.gbc.personalrestaurantguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the background image
        ImageView backgroundImage = findViewById(R.id.backgroundImage);

        // Load the fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        backgroundImage.startAnimation(fadeIn);

        // Log message for debugging
        Log.d(TAG, "Splash screen displayed");

        // Delay and transition to the main activity
        findViewById(android.R.id.content).postDelayed(() -> {
            Log.d(TAG, "Transitioning to MainActivity");
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close splash screen
        }, 3000); // 3 seconds

    }
}