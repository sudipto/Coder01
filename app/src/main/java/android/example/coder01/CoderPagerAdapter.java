package android.example.coder01;

import android.example.coder01.fragments.EditorFragment;
import android.example.coder01.fragments.OutputFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CoderPagerAdapter extends FragmentPagerAdapter {
    private static final String TITLE[] = { "Editor", "Output"};

    public CoderPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int id) {
        // Return the fragment for the selected tab
        if (id == 1)
            return new OutputFragment();
        else if (id == 0)
            return new EditorFragment();
        else
            return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        // Get the title for the tab
        return TITLE[position];
    }
}
