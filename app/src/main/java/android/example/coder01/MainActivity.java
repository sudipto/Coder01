package android.example.coder01;

import android.example.coder01.fragments.EditorFragment;
import android.example.coder01.fragments.OutputFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements EditorFragment.OnInputListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        pagerAdapter = new CoderPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof EditorFragment) {
            EditorFragment editorFragment = (EditorFragment) fragment;
            editorFragment.setOnInputListener(this);
        }
    }

    // Implementing the interface
    // Communicate with the fragments
    @Override
    public void onEditorInput(String text) {

        // Get the Output fragment
        OutputFragment outputFragment = (OutputFragment)
                fragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":1");

        if (outputFragment != null) {
            // Send the output to the Output fragment
            outputFragment.setOutputTextView(text);

            // Jump to that fragment
            viewPager.setCurrentItem(1, true);
        }
    }
}
