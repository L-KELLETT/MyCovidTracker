package com.lak021.mycovidtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CaseFragment extends Fragment {
    private static final String ARG_CASE_ID = "case_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private Case mCase;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mWasCloseContact;
    private Button mReportButton;
    private Button mContactsButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private EditText mDurationButton;


    public static CaseFragment newInstance(UUID caseId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CASE_ID, caseId);
        CaseFragment fragment = new CaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID caseId = (UUID) getArguments().getSerializable(ARG_CASE_ID);
        mCase = CaseFolder.get(getActivity()).getCase(caseId);
        mPhotoFile = CaseFolder.get(getActivity()).getPhotoFile(mCase);
        setHasOptionsMenu(true);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_case, container, false);
        mTitleField = (EditText) v.findViewById(R.id.case_title);
        mTitleField.setText(mCase.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This is intentionally blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
            mCase.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                //This is also intentionally blank
            }
        });
        mDateButton = (Button) v.findViewById(R.id.case_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCase.getDate());
                dialog.setTargetFragment(CaseFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mWasCloseContact = (CheckBox) v.findViewById(R.id.case_solved);
        mWasCloseContact.setChecked(mCase.isWasCloseContact());
        mWasCloseContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCase.setWasCloseContact(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.case_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.case_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mContactsButton = (Button) v.findViewById(R.id.choose_suspect);
        if (mCase.getContacts() == "null" || mCase.getContacts() == null) {
            mContactsButton.setText("Select Contact");
        } else {
            mContactsButton.setText("Contact: " + mCase.getContacts());
        }
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);

            }
        });




        mDurationButton = (EditText) v.findViewById(R.id.duration_text);
        mDurationButton.setText(mCase.getDuration());
        mDurationButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This is intentionally blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                mCase.setDuration(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                //This is also intentionally blank
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactsButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.case_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.lak021.mycovidtracker.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.case_photo);
        updatePhotoView();
        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        CaseFolder.get(getActivity())
                .updateCase(mCase);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_case, menu);
    }

    public void findLocation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCase.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data -
                // that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCase.setContacts(suspect);
                mContactsButton.setText("Contact: " + suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.lak021.mycovidtracker.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_case:
                Case aCase = this.mCase;
                CaseFolder.get(getActivity()).deleteCase(aCase);
                getActivity().finish();
                Toast.makeText(getActivity(), "Case Deleted", Toast.LENGTH_SHORT).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCase.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCase.isWasCloseContact()) {
            solvedString = getString(R.string.case_report_solved);
        } else {
            solvedString = getString(R.string.case_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCase.getDate()).toString();

        String contacts = " " + mCase.getContacts();
        if (contacts == null) {
            contacts = getString(R.string.case_report_without_friend);
        } else {
            contacts = getString(R.string.case_report_with_friend) + contacts;
        }
        String report = getString(R.string.case_report,
                mCase.getTitle(), dateString, solvedString, contacts);
        return report;
    }

    private void updatePhotoView() {
        System.out.println("updatePhotoView started");
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            System.out.println("updatePhotoView ended");
        }
    }
}


