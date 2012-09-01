package fredb219.disconnectiontracker;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	private DisconnectionTrackerActivity disconnectionTrackerActivity;

	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private Switch switchService;


	private Button testNowButton;


	private TextView textViewLastTest;


	private TextView textViewStartDate;


	private TextView textViewCause;


	private TextView textViewNetworkType;


	private TextView textViewPingStatus;


	private TextView textViewSignalStrength;
	
	private SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm:ss dd/MM");


	private ProgressBar progressBarRequesting;

	public SettingsFragment() {
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		disconnectionTrackerActivity = (DisconnectionTrackerActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("SettingsFragment", "onCreateView");
		
		View inflate = inflater.inflate(R.layout.layout_settings, container, false);

		switchService = (Switch) inflate.findViewById(R.id.switchService);
		textViewLastTest = (TextView) inflate.findViewById(R.id.textViewLastTest);
		textViewStartDate = (TextView) inflate.findViewById(R.id.textViewStartDate);
		textViewCause = (TextView) inflate.findViewById(R.id.textViewCause);
		textViewNetworkType = (TextView) inflate.findViewById(R.id.textViewNetworkType);
		textViewPingStatus = (TextView) inflate.findViewById(R.id.textViewPingStatus);
		textViewSignalStrength = (TextView) inflate.findViewById(R.id.textViewSignalStrength);
		progressBarRequesting = (ProgressBar) inflate.findViewById(R.id.progressBarRequesting);
		
		
		switchService.setChecked(disconnectionTrackerActivity.isServiceRunning());
		
		
		testNowButton = (Button) inflate.findViewById(R.id.buttonTestNow);
		
		switchService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("SettingsFragment", "onCheckedChanged");
				FragmentActivity activity = SettingsFragment.this.getActivity();
				if(isChecked) {
					activity.startService(new Intent(activity, TrackerService.class));
				} else {
					if(disconnectionTrackerActivity.getService() != null) {
						disconnectionTrackerActivity.getService().stop();
					}
					activity.stopService(new Intent(activity, TrackerService.class));
				}
			}
		});
		
		testNowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disconnectionTrackerActivity.getService().performTest();
			}
		});
		
		update();
		
		return inflate; 
	}


	public void update() {
		TrackReport trackReport = null;
		
		TrackerService trackerService = disconnectionTrackerActivity.getService();
		if(trackerService != null) {
			if(trackerService.isStarted()) {
				switchService.setChecked(true);
				textViewStartDate.setText("Tracking since "+dateFormat.format(trackerService.getStartDate()));
			} else {
				switchService.setChecked(false);
				textViewStartDate.setText("");
			}
			
			trackReport = trackerService.getLastReport();
			
			if(trackerService.isRequestRunning()) {
				testNowButton.setEnabled(false);
				progressBarRequesting.setIndeterminate(true);
			} else {
				testNowButton.setEnabled(true);
				progressBarRequesting.setIndeterminate(false);
			}
			
		} else {
			textViewStartDate.setText("");
			testNowButton.setEnabled(false);
		}
		
		if(trackReport == null) {
			textViewLastTest.setText("No last report");
			textViewSignalStrength.setText("");
			textViewPingStatus.setText("");
			textViewNetworkType.setText("");
			textViewCause.setText("");
		} else {
			textViewLastTest.setText("Last report at "+ dateFormat.format(trackReport.getStartTime()));
			
			textViewSignalStrength.setText("Signal strength: "+trackReport.getSignalStrength()+"/31");
			if(trackReport.isPinging()) {
				textViewPingStatus.setText("Ping: "+ trackReport.getPing()+" ms");	
			} else {
				textViewPingStatus.setText("Ping: FAIL");
			}
			
			textViewNetworkType.setText("Network: "+trackReport.getNetworkTypeName());
			textViewCause.setText("Report cause: "+ trackReport.getCauseName());
		}
	}
}
