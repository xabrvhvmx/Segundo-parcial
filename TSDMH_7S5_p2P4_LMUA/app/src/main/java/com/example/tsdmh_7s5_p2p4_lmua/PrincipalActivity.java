package com.example.tsdmh_7s5_p2p4_lmua;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class PrincipalActivity extends AppCompatActivity {

    Button btnImageFragment, btnFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnImageFragment = findViewById(R.id.btnImageFragment);
        btnFormFragment = findViewById(R.id.btnFormFragment);

        if (savedInstanceState == null) {
            loadFragment(new ImagenFragment());
        }

        btnImageFragment.setOnClickListener(v -> loadFragment(new ImagenFragment()));
        btnFormFragment.setOnClickListener(v -> loadFragment(new FormularioFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .setReorderingAllowed(true)
                .commit();
    }
}