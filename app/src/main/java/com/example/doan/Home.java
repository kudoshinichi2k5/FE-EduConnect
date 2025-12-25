package com.example.doan;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends BaseActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottom_navigation);

        // Mặc định load HomeFragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_opportunities) {
                    selectedFragment = new OpportunitiesFragment();
                } else if (id == R.id.nav_chatbot) {
                    selectedFragment = new ChatbotFragment(); // Nhớ tạo file này dù rỗng
                } else if (id == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment(); // Nhớ tạo file này dù rỗng
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    // --- HÀM MỚI: Cho phép Fragment con yêu cầu chuyển Tab ---
    public void switchToTab(int navId) {
        bottomNav.setSelectedItemId(navId);
    }
}