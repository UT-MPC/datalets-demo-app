package edu.utexas.colin.chaacapp.ui.activity;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.edmodo.rangebar.RangeBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import net.pherth.android.emoji_library.EmojiEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import edu.utexas.colin.chaacapp.R;
import edu.utexas.colin.chaacapp.events.DataletDeleteEvent;
import edu.utexas.colin.chaacapp.events.DataletSaveEvent;
import edu.utexas.colin.chaacapp.model.ChaacModel;
import edu.utexas.colin.chaacapp.model.datalets.Policy;
import edu.utexas.colin.chaacapp.model.datalets.Datalet;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Condition;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Location;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Profile;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Schedule;
import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;
import edu.utexas.colin.chaacapp.ui.BaseActivity;
import edu.utexas.colin.chaacapp.ui.MainActivity;
import edu.utexas.colin.chaacapp.ui.helpers.MarkdownParser;
import edu.utexas.colin.chaacapp.ui.helpers.ViewHelper;

public class DataletFormActivity extends BaseActivity
		implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
		View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private static final String TAG = "DataletFormActivity";

	@BindView(R.id.datalet_title_edit_text)
	EmojiEditText mDataletTitle;

	@BindView(R.id.datalet_desc_edit_text)
	EmojiEditText mDataletDesc;

	@BindView(R.id.datalet_location_display)
	TextView mLocationDisplay;

	@BindView(R.id.datalet_location_button)
	Button mLocationButton;

	@BindView(R.id.btn_delete_datalet)
	Button mDeleteButton;

	@BindView(R.id.precondition_time_begin_text)
	TextView mPreconditionBeginText;

	@BindView(R.id.precondition_time_select_button)
	Button mPreconditionTimeSelectButton;

	@BindView(R.id.precondition_time_duration_edit)
	EditText mPreconditionDurationEdit;

	@BindView(R.id.precondition_time_repeat_spinner)
	Spinner mPreconditionRepeatSpinner;

	@BindView(R.id.precondition_time_end_wrapper)
	RelativeLayout mPreconditionTimeEndWrapper;

	@BindView(R.id.precondition_time_end_text)
	TextView mPreconditionTimeEndText;

	@BindView(R.id.precondition_time_end_button)
	Button mPreconditionTimeEndButton;

	@BindView(R.id.available_location_distance_edit)
	EditText mAvailableDistanceEdit;

	@BindView(R.id.available_profile_begin_text)
	TextView mAvailableLevelText;

	@BindView(R.id.available_level_range)
	RangeBar mAvailableLevelRange;

	private String mDataletID;
	private Datalet mDatalet;
	private GoogleApiClient mGoogleAPIClient;
	private boolean currentLocationChecked = false;
	private double mLatitude, mLongitude;
	private int mYear, mMonth, mDay, mHour, mMinute;
	private int mEndYear, mEndMonth, mEndDay;
	private int mLevelMin = 1, mLevelMax = 25;
	private String mAMOrPM;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_datalet_form;
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
		mDataletID = intent.getStringExtra(DataletInfoActivity.DATALET_ID_KEY);

		mLocationButton.setOnClickListener(this);

		mDeleteButton.setEnabled(false);
		mDeleteButton.setOnClickListener(view -> new AlertDialog.Builder(view.getContext())
				.setTitle(getString(R.string.userform_delete_title))
				.setMessage(getString(R.string.userform_delete_message))
				.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
					if (mDatalet != null) {
						ChaacModel.model().removeDatalet(mDatalet.getId());

						DataletDeleteEvent event = new DataletDeleteEvent();
						event.dataletID = mDatalet.getId();
						EventBus.getDefault().post(event);

						Intent resultIntent = new Intent();
						setResult(RESULT_OK, resultIntent);
						finish();
						dismissKeyboard();

						Intent i = new Intent(DataletFormActivity.this, MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
								| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);

						startActivity(i);
					}
				}).setNegativeButton(getString(R.string.no), (dialog, which) -> {
					dialog.dismiss();
				}).show());

		mPreconditionDurationEdit.setText("30");
		mPreconditionTimeSelectButton.setOnClickListener(this);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1;
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mEndYear = mYear;
		mEndMonth = mMonth;
		mEndDay = mDay;
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mAMOrPM = "AM";
		if (mHour > 12) {
			mHour -= 12;
			mAMOrPM = "PM";
		}
		mMinute = c.get(Calendar.MINUTE);

		mAvailableLevelRange.setOnRangeBarChangeListener(mOnRangeChange);

		ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(this,
				R.array.precondition_repeat_types, android.R.layout.simple_spinner_item);
		repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mPreconditionRepeatSpinner.setAdapter(repeatAdapter);
		mPreconditionRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectRepeatsType(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		selectRepeatsType(0);

		mPreconditionTimeEndButton.setOnClickListener(this);

		mAvailableDistanceEdit.setText("100");

		if (mDataletID != null) {
			mDatalet = ChaacModel.model().getDatalet(mDataletID);
			setTitle(mDatalet);
			updateWithDataletInfo(mDatalet);

			ViewHelper.SetBackgroundTint(mDeleteButton, ContextCompat.getColor(this, R.color.worse_10));
			mDeleteButton.setEnabled(true);
		} else {
			setTitle((Datalet) null);
			mDataletTitle.requestFocus();
		}

		updateDateDisplay();
		updateEndsDisplay();
		updateLocationDisplay();
		updateAvailableLevelText();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult: " + requestCode + ", " + ", " + resultCode);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				mLatitude = data.getDoubleExtra("latitude", mLatitude);
				mLongitude = data.getDoubleExtra("longitude", mLongitude);
				mAvailableDistanceEdit.setText(data.getStringExtra("radius"));
				updateLocationDisplay();
			}
		}
	}

	private void updateWithDataletInfo(Datalet datalet) {
		if (datalet != null) {
			currentLocationChecked = true;
			mDataletTitle.setText(datalet.getTitle());
			mDataletDesc.setText(datalet.getDescription());
			mLatitude = datalet.getLocation().latitude;
			mLongitude = datalet.getLocation().longitude;
			updatePreconditionWithDatalet(datalet);
			updateAvailabilityWithDatalet(datalet);
		}
	}

	private void updatePreconditionWithDatalet(Datalet datalet) {
		if (datalet != null && datalet.getPrecondition() != null) {
			Policy precondition = datalet.getPrecondition();
			List<Policy> children = precondition.getChildren();
			Condition condition = children.get(0).getCondition();
			if (condition instanceof Schedule) {
				updateSchedulePrecondition((Schedule) condition);
			}
		}
	}

	private void updateSchedulePrecondition(Schedule schedule) {
		if (schedule != null) {
			LocalDate begins = schedule.getBegins();
			mMonth = begins.getMonth();
			mDay = begins.getDay();
			mYear = begins.getYear();
			String time = schedule.getTime();
			String[] s1 = time.split(":");
			mHour = Integer.parseInt(s1[0]);
			String[] s2 = s1[1].split(" ");
			mMinute = Integer.parseInt(s2[0]);
			mAMOrPM = s2[1];
			String duration = schedule.getOperand();
			mPreconditionDurationEdit.setText(duration);
			if (schedule.isRepeats()) {
				if (Schedule.DAILY.equals(schedule.getFrequency())) {
					selectRepeatsType(1);
				} else if (Schedule.WEEKLY.equals(schedule.getFrequency())) {
					if (schedule.getDays().size() > 1) {
						selectRepeatsType(3);
					} else {
						selectRepeatsType(2);
					}
				} else if (Schedule.MONTHLY.equals(schedule.getFrequency())) {
					selectRepeatsType(4);
				}
				if (schedule.getEnds() != null) {
					LocalDate ends = schedule.getEnds();
					mEndMonth = ends.getMonth();
					mEndDay = ends.getDay();
					mEndYear = ends.getYear();
					updateEndsDisplay();
				}
			} else {
				selectRepeatsType(0);
			}
		}
	}

	private void updateAvailabilityWithDatalet(Datalet datalet) {
		if (datalet != null && datalet.getAvailability() != null) {
			Policy availability = datalet.getAvailability();
			List<Policy> children = availability.getChildren();

			Location location = (Location) children.get(0).getCondition();
			Profile profile = (Profile) children.get(1).getCondition();
			updateLocationAvailability(location);
			updateProfileAvailability(profile);
		}
	}

	private void updateLocationAvailability(Location location) {
		if (location != null) {
			mAvailableDistanceEdit.setText(location.getOperand());
		}
	}

	private void updateProfileAvailability(Profile profile) {
		if (profile != null) {
			String value = profile.getOperand();
			String[] range = value.split(",");
			if (range.length > 1) {
				mLevelMin = Integer.parseInt(range[0].trim());
				mLevelMax = Integer.parseInt(range[1].trim());
				mAvailableLevelRange.setThumbIndices(mLevelMin - 1, mLevelMax - 1);
			}
		}
	}

	private void showPickDataletLocation() {
		Intent intent = new Intent(this, LocationWithRadiusActivity.class);

		Bundle bundle = new Bundle();
		bundle.putDouble("latitude", mLatitude);
		bundle.putDouble("longitude", mLongitude);
		bundle.putInt("radius", Integer.parseInt(mAvailableDistanceEdit.getText().toString()));
		intent.putExtras(bundle);

		startActivityForResult(intent, 1);
	}

	private boolean savePrecondition(Datalet datalet) {
		List<Policy> policy = new ArrayList<>();

		LocalDate beginDate = new LocalDate(mMonth, mDay, mYear);
		String beginTime = String.format("%d:%02d %s", mHour, mMinute, mAMOrPM);
		String duration = mPreconditionDurationEdit.getText().toString();
		int repeatsSelected = mPreconditionRepeatSpinner.getSelectedItemPosition();
		LocalDate endDate = new LocalDate(mEndMonth, mEndDay, mEndYear);

		Schedule condition = Condition.schedule("<", duration, beginTime, beginDate);
		switch (repeatsSelected) {
			case 1:
				condition = condition.repeatsDaily(endDate);
				break;
			case 2:
				condition = condition.repeatsWeekly(Collections.singletonList(beginDate.dayOfWeek()), endDate);
				break;
			case 3:
				condition = condition.repeatsWeekly(Schedule.weekdays(), endDate);
				break;
			case 4:
				String dayOfMonth = Integer.toString(beginDate.getDay());
				condition = condition.repeatsMonthly(Collections.singletonList(dayOfMonth), endDate);
				break;
		}
		policy.add(condition.finish());


		Policy precondition = Policy.createAnd(policy.toArray(new Policy[policy.size()]));
		datalet.setPrecondition(precondition);

		return true;
	}

	private boolean saveAvailability(Datalet datalet) {
		String operator = "<";
		String distance = mAvailableDistanceEdit.getText().toString();
		Policy locPolicy = Condition.location(operator, distance).finish();

		String value = mLevelMin + "," + mLevelMax;

		String field = "application.level";
		Policy profilePolicy = Condition.profile("range", value, field).finish();


		Policy availability = Policy.createAnd(locPolicy, profilePolicy);
		datalet.setAvailability(availability);

		return true;
	}

	private boolean saveDatalet(Datalet datalet) {
		datalet.setTitle(MarkdownParser.parseCompiled(mDataletTitle.getText()));
		datalet.setDescription(MarkdownParser.parseCompiled(mDataletDesc.getText()));

		datalet.setOwnerID(ChaacModel.model().getAdminID());

		if (datalet.getTitle().isEmpty() || datalet.getOwnerID().isEmpty()) {
			return false;
		}

		if (!savePrecondition(datalet) || !saveAvailability(datalet)) {
			return false;
		}

		datalet.setLocation(new GPSPoint(mLatitude, mLongitude));

		return true;
	}

	private boolean prepareSave() {
		if (mDatalet == null) {
			mDatalet = new Datalet();
		}

		if (saveDatalet(mDatalet)) {
			ChaacModel.model().addDatalet(mDatalet);

			DataletSaveEvent event = new DataletSaveEvent();
			if (mDatalet.getId() == null) {
				event.created = true;
			}

			event.datalet = mDatalet;
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
				android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);

				mLatitude = lastLocation.getLatitude();
				mLongitude = lastLocation.getLongitude();
				currentLocationChecked = true;
				updateLocationDisplay();
			}
		}
	}

	private void updateLocationDisplay() {
		mLocationDisplay.setText(mLatitude + ", " + mLongitude);
	}

	private void setTitle(@Nullable Datalet datalet) {
		ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {

			String title;

			if (datalet != null) {
				title = "Edit Monster";
			} else {
				title = "New Monster";
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

	@Override
	public void onClick(View v) {
		if (v == mPreconditionTimeSelectButton) {
			DialogFragment dateFragment = new DatePickerFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("year", mYear);
			bundle.putInt("month", mMonth - 1);
			bundle.putInt("day", mDay);
			dateFragment.setArguments(bundle);
			dateFragment.show(getSupportFragmentManager(), "datePicker");
		} else if (v == mLocationButton) {
			showPickDataletLocation();
			// BRING UP LOCATION PICKER
		} else if (v == mPreconditionTimeEndButton) {
			DialogFragment dateFragment = new DatePickerFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("year", mEndYear);
			bundle.putInt("month", mEndMonth - 1);
			bundle.putInt("day", mEndDay);
			dateFragment.setArguments(bundle);
			dateFragment.show(getSupportFragmentManager(), "endPicker");
		}
	}

	public void selectRepeatsType(int position) {
		mPreconditionRepeatSpinner.setSelection(position);
		if (position == 0) {
			mPreconditionTimeEndWrapper.setVisibility(View.GONE);
		} else {
			mPreconditionTimeEndWrapper.setVisibility(View.VISIBLE);
		}
	}

	public void updateEndsDisplay() {
		String display = String.format("%s %d/%d/%d", getString(R.string.precondition_time_end),
				mEndMonth, mEndDay, mEndYear);
		mPreconditionTimeEndText.setText(display);
	}

	public void updateDateDisplay() {
		String displayText = String.format("%s %d/%d/%d at %d:%02d %s", getString(R.string.precondition_time_begin),
				mMonth, mDay, mYear, mHour, mMinute, mAMOrPM);
		mPreconditionBeginText.setText(displayText);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		FragmentManager frag = getSupportFragmentManager();
		if (frag.findFragmentByTag("datePicker") != null) {
			mYear = year;
			mMonth = month + 1;
			mDay = day;
			DialogFragment timeFragment = new TimePickerFragment();
			Bundle bundle = new Bundle();
			int hour = mHour;
			if (mAMOrPM.equals("PM")) {
				hour += 12;
			}
			bundle.putInt("hour", hour);
			bundle.putInt("minute", mMinute);
			timeFragment.setArguments(bundle);
			timeFragment.show(getSupportFragmentManager(), "timePicker");
		} else if (frag.findFragmentByTag("endPicker") != null) {
			Log.e(TAG, "Got to the end picker");
			mEndYear = year;
			mEndMonth = month + 1;
			mEndDay = day;
			updateEndsDisplay();
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (hourOfDay > 12) {
			hourOfDay -= 12;
			mAMOrPM = "PM";
		} else {
			mAMOrPM = "AM";
		}
		if (hourOfDay == 0) {
			hourOfDay = 12;
		}
		mHour = hourOfDay;
		mMinute = minute;
		updateDateDisplay();
	}

	public static class DatePickerFragment extends DialogFragment {
		@Override
		public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle arguments = getArguments();
			final Calendar c = Calendar.getInstance();
			int year, month, day;
			if (arguments != null) {
				year = arguments.getInt("year", c.get(Calendar.YEAR));
				month = arguments.getInt("month", c.get(Calendar.MONTH));
				day = arguments.getInt("day", c.get(Calendar.DAY_OF_MONTH));
			} else {
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}

			return new DatePickerDialog(getActivity(), (DataletFormActivity) getActivity(), year, month, day);
		}
	}

	public static class TimePickerFragment extends DialogFragment {
		@Override
		public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour, minute;
			final Calendar c = Calendar.getInstance();
			Bundle arguments = getArguments();
			if (arguments != null) {
				hour = arguments.getInt("hour", c.get(Calendar.HOUR_OF_DAY));
				minute = arguments.getInt("minute", c.get(Calendar.MINUTE));
			} else {
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
			}

			TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (DataletFormActivity) getActivity(), hour, minute, false);
			timePickerDialog.setTitle("");

			return timePickerDialog;
		}
	}

	private void updateAvailableLevelText() {
		mAvailableLevelText.setText(getString(R.string.available_profile_begin)
				+ " " + mLevelMin + " and " + mLevelMax);
	}

	private RangeBar.OnRangeBarChangeListener mOnRangeChange = new RangeBar.OnRangeBarChangeListener() {
		@Override
		public void onIndexChangeListener(RangeBar rangeBar, int leftThumb, int rightThumb) {
			mLevelMin = leftThumb + 1;
			mLevelMax = rightThumb + 1;
			updateAvailableLevelText();
		}
	};
}
