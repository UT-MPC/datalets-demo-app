package edu.utexas.colin.chaacapp.ui.activity;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import net.pherth.android.emoji_library.EmojiEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.events.UserDeleteEvent;
import edu.utexas.colin.chaacapp.events.UserSaveEvent;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;
import edu.utexas.colin.chaacapp.model.users.User;
import edu.utexas.colin.chaacapp.ui.BaseActivity;
import edu.utexas.colin.chaacapp.ui.MainActivity;
import edu.utexas.colin.chaacapp.ui.helpers.MarkdownParser;
import edu.utexas.colin.chaacapp.ui.helpers.ViewHelper;
import edu.utexas.colin.chaacapp.ui.recycler.ApplicationAdapterHolder;

public class UserFormActivity extends BaseActivity
        implements OnConnectionFailedListener, ConnectionCallbacks {

    public static final String USER_ID_KEY = "userID";
    public static final int DATE_DIALOG_ID = 0;
    public static final String TAG = "UserFormActivity";
	public static final String LEVEL = "level";

	@BindView(R.id.user_firstname_edittext)
    EmojiEditText userFirstNameText;

    @BindView(R.id.user_lastname_edittext)
    EmojiEditText userLastNameText;

    @BindView(R.id.user_email_edittext)
    EmojiEditText userEmailText;

    @BindView(R.id.user_gender_spinner)
    Spinner userGenderSpinner;

    @BindView(R.id.user_birthdate_display)
    TextView userBirthdateText;
    @BindView(R.id.user_birthdate_button)
    Button userBirthdatePicker;

    @BindView(R.id.user_location_display)
    TextView userLocationText;
    @BindView(R.id.user_location_button)
    Button userLocationPicker;

	@BindView(R.id.user_current_level)
	TextView userLevelText;
	@BindView(R.id.user_level_seekbar)
	SeekBar userLevelSeekBar;

    @BindView(R.id.btn_delete_user)
    Button btnDelete;

    private String userID;
    private User mUser;
	private int mLevel = 1;
    private LocalDate birthdate;
    private DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        birthdate = new LocalDate((short)(month + 1), (short)dayOfMonth, year);
        updateCalendarDisplay();
    };
    private boolean currentLocationChecked = false;
    private double mLatitude;
    private double mLongitude;
	private ApplicationAdapterHolder mApplicationAdapter;
    private GoogleApiClient mGoogleAPIClient;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_form;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
        }

        mGoogleAPIClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(LocationServices.API)
                .build();
        mGoogleAPIClient.connect();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userID = bundle.getString(USER_ID_KEY);
        }

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGenderSpinner.setAdapter(genderAdapter);
        userGenderSpinner.setSelection(0);

        userBirthdatePicker.setOnClickListener(view -> {
            showDialog(DATE_DIALOG_ID);
        });

        final Calendar c = Calendar.getInstance();
        birthdate = new LocalDate((short)(c.get(Calendar.MONTH) + 1), (short)c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.YEAR));

        userLocationPicker.setOnClickListener(view -> {
            showLocationPicker();
        });

		userLevelSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        btnDelete.setEnabled(false);
        btnDelete.setOnClickListener(view -> new AlertDialog.Builder(view.getContext())
                .setTitle(getString(R.string.userform_delete_title))
                .setMessage(getString(R.string.userform_delete_message)).setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (mUser != null) {
                        ChaacModel.model().removeUser(mUser.getId());
                    }

                    finish();
                    dismissKeyboard();

                    UserDeleteEvent event = new UserDeleteEvent();
                    event.userID = mUser.getId();
                    EventBus.getDefault().post(event);

					Intent i = new Intent(UserFormActivity.this, MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
						| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);

					startActivity(i);
				}).setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                }).show());

        if (userID != null) {
            mUser = ChaacModel.model().getUser(userID);
            setTitle(mUser);

			updateWithUserInfo(mUser);

            ViewHelper.SetBackgroundTint(btnDelete, ContextCompat.getColor(this, R.color.worse_10));
            btnDelete.setEnabled(true);
        } else {
            setTitle((User) null);
			Log.e(TAG, "RequestFocus");
            userFirstNameText.requestFocus();
        }

		updateCalendarDisplay();
		updateLocationDisplay();
		updateSeekBarText();
    }

    @Override
    protected void onStart() {
        if (mGoogleAPIClient != null) {
            mGoogleAPIClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleAPIClient != null) {
            mGoogleAPIClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateCurrentLocation();
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            finishActivitySuccessfully();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        dateSetListener,
                        birthdate.getYear(), birthdate.getMonth() - 1, birthdate.getDay());
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + ", " + ", " + resultCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "RESULT_OK");
                mLatitude = data.getDoubleExtra("latitude", mLatitude);
                mLongitude = data.getDoubleExtra("longitude", mLongitude);
                updateLocationDisplay();
            }
        }
    }

    private void updateWithUserInfo(User user) {
		if (user != null) {
			currentLocationChecked = true;
			userFirstNameText.setText(user.getFirstName());
			userLastNameText.setText(user.getLastName());
			userEmailText.setText(user.getEmail());
			if ("Male".equals(user.getGender())) {
				userGenderSpinner.setSelection(0);
			} else if ("Female".equals(user.getGender())) {
				userGenderSpinner.setSelection(1);
			}
			birthdate = user.getBirthdate();
			GPSPoint location = user.getLocation();
			mLatitude = location.latitude;
			mLongitude = location.longitude;
			Map<String, String> applicationData = user.getApplication();
			if (applicationData != null && applicationData.containsKey(LEVEL)) {
				mLevel = Integer.parseInt(applicationData.get(LEVEL));
				userLevelSeekBar.setProgress(mLevel - 1);
			}
		}
	}

    private boolean saveUser(User user) {
        user.setFirstName(MarkdownParser.parseCompiled(userFirstNameText.getText()));
        user.setLastName(MarkdownParser.parseCompiled(userLastNameText.getText()));
        user.setEmail(MarkdownParser.parseCompiled(userEmailText.getText()));

		String level = String.valueOf(mLevel);

        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty()
				|| user.getEmail().isEmpty() || level == null || level.isEmpty()) {
            return false;
        }

        if (userGenderSpinner.getSelectedItemPosition() == 0) {
            user.setGender("Male");
        } else if (userGenderSpinner.getSelectedItemPosition() == 1) {
            user.setGender("Female");
        }

        user.setBirthdate(birthdate);
        user.setLocation(new GPSPoint(mLatitude, mLongitude));

		Map<String, String> applicationData = new HashMap<>();
		applicationData.put(LEVEL, level);
		user.setApplication(applicationData);

		return true;
    }

    private boolean prepareSave() {
        if (mUser == null) {
            mUser = new User();
        }

        if (saveUser(mUser)) {
            ChaacModel.model().addUser(mUser);

            UserSaveEvent event = new UserSaveEvent();
            if (mUser.getId() == null) {
                event.created = true;
            }

            event.user = mUser;
            EventBus.getDefault().post(event);

			return true;
        }

		return false;
    }

    private void finishActivitySuccessfully() {
        if (prepareSave()) {
			finishWithSuccess();
		}

		dismissKeyboard();
    }

    private void finishWithSuccess() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (!currentLocationChecked) {
                Log.e(TAG, "Grab current location");
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);

                mLatitude = lastLocation.getLatitude();
                mLongitude = lastLocation.getLongitude();
                currentLocationChecked = true;
                updateLocationDisplay();
            }
        }
    }

    private void showLocationPicker() {
        Intent intent = new Intent(this, UserLocationActivity.class);

        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", mLatitude);
        bundle.putDouble("longitude", mLongitude);
        intent.putExtras(bundle);

        startActivityForResult(intent, 1);
    }

    private void updateLocationDisplay() {
        userLocationText.setText(mLatitude + ", " + mLongitude);
    }

    private void updateCalendarDisplay() {
        userBirthdateText.setText(
                new StringBuilder()
                    .append(birthdate.getMonthAsString()).append(" ")
                    .append(birthdate.getDay()).append(", ")
                    .append(birthdate.getYear()).append(" ")
        );
    }

	private void updateSeekBarText() {
		userLevelText.setText(String.valueOf(mLevel));
	}

    private void setTitle(@Nullable User user) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            String title;

            if (user != null) {
                title = getResources().getString(R.string.edit_user);
            } else {
                title = getResources().getString(R.string.new_player);
            }

            actionBar.setTitle(title);
        }
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			mLevel = progress + 1;
			updateSeekBarText();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};
}
