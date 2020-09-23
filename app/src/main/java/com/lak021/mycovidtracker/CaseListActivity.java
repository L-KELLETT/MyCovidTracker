package com.lak021.mycovidtracker;

import androidx.fragment.app.Fragment;

public class CaseListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CaseListFragment();
    }
}
