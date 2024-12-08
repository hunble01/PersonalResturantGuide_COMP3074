package ca.gbc.personalrestaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {
    ImageView ivHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ivHome = findViewById(R.id.ivHome);

        // Navigate to MainActivity on Home button click
        ivHome.setOnClickListener(view -> {
            startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
            finishAffinity();
        });
    }
}
