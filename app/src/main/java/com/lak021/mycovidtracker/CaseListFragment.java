package com.lak021.mycovidtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CaseListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        System.out.println("OnCreate Called");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        System.out.println("OnResume Called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("OnDestroy Called");
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_case_list, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_case:
                Case aCase = new Case();
                CaseFolder.get(getActivity()).addCase(aCase);
                Intent intent = CasePagerActivity.newIntent(getActivity(), aCase.getID());
                startActivity(intent);
                return true;
            case R.id.wipe_cases:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.wipe_cases).setTitle(R.string.wipe_cases_menu);
                builder.setPositiveButton(R.string.confirm_wipe_cases, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CaseFolder.get(getActivity()).deleteCases();
                        updateUI();
                        Toast.makeText(getActivity(), "All Cases Cleared!", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(R.string.cancel_wipe_cases, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Clear Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CaseFolder caseFolder = CaseFolder.get(getActivity());
        int caseCount = caseFolder.getCases().size();
        String subtitle = getString(R.string.subtitle_format, caseCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CaseFolder crimeLab = CaseFolder.get(getActivity());
        List<Case> aCases = crimeLab.getCases();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(aCases);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCases(aCases);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Case mCase;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_case, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Case aCase) {
            mCase = aCase;
            mTitleTextView.setText(mCase.getTitle());
            mDateTextView.setText(mCase.getDate().toString());
            mSolvedImageView.setVisibility(aCase.isWasCloseContact() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCase.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = CasePagerActivity.newIntent(getActivity(), mCase.getID());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Case> mCases;

        public CrimeAdapter(List<Case> aCases) {
            mCases = aCases;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Case aCase = mCases.get(position);
            holder.bind(aCase);
        }

        @Override
        public int getItemCount() {
            return mCases.size();
        }

        public void setCases(List<Case> cases) {
            mCases = cases;
        }
    }

}