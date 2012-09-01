package fredb219.disconnectiontracker;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import fredb219.disconnectiontracker.TrackReport.Cause;

public class TrackerService extends Service {

	private final IBinder mBinder = new TrackerBinder();
	private TelephonyManager telephony;
	private PhoneStateListener phoneStateListener;
	private boolean started = false;
	private int gsmSignalStrength = -1;
	private TrackerServiceListener listener;
	private TrackReport lastReport;
	private Date startDate;
	private int runningRequest = 0;
	private TrackTimerTask trackTimerTask;
	private Timer timer = new Timer();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("TrackerService", "onCreate");
		telephony = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		phoneStateListener = new LocalPhoneStateListener();
		int mask = PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
				| PhoneStateListener.LISTEN_CALL_STATE
				| PhoneStateListener.LISTEN_CELL_LOCATION
				| PhoneStateListener.LISTEN_DATA_ACTIVITY
				| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
				| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR;
		telephony.listen(phoneStateListener, mask);

		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("TrackerService", "onStartCommand");
		startDate = new Date();
		started = true;
		trackTimerTask = new TrackTimerTask();
		timer.schedule(trackTimerTask, 0, 10000);
		if (listener != null) {
			listener.onStateChanged(started);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d("TrackerService", "onDestroy");
		trackTimerTask.cancel();
		telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("TrackerService", "onBind");
		return mBinder;
	}

	public boolean isStarted() {
		return started;
	}

	public class TrackerBinder extends Binder {
		TrackerService getService() {
			return TrackerService.this;
		}
	}

	public synchronized void setPendingRequest(boolean start) {
		if (start) {
			runningRequest++;
		} else {
			runningRequest--;
		}

		if (listener != null) {
			listener.onRequestStatusChanged(isRequestRunning());
		}
	}

	public void performTest() {
		Log.d("TrackerService", "performTest");

		new TrackTask(Cause.USER_REQUEST, this).start();

	}

	private class LocalPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {
			Log.d("LocalPhoneStateListener",
					"onCallForwardingIndicatorChanged=" + cfi);
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.d("LocalPhoneStateListener", "onCallStateChanged state="
					+ state + " number=" + incomingNumber);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			Log.d("LocalPhoneStateListener", "onCellLocationChanged="
					+ location);
		}

		@Override
		public void onDataActivity(int direction) {
			Log.d("LocalPhoneStateListener", "onDataActivity=" + direction);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			Log.d("LocalPhoneStateListener",
					"onDataConnectionStateChanged state=" + state
							+ " networkType=" + networkType);
		}

		@Override
		public void onDataConnectionStateChanged(int state) {
			Log.d("LocalPhoneStateListener", "onDataConnectionStateChanged="
					+ state);
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			Log.d("LocalPhoneStateListener",
					"onMessageWaitingIndicatorChanged=" + mwi);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			Log.d("LocalPhoneStateListener", "onServiceStateChanged="
					+ serviceState);
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			gsmSignalStrength = signalStrength.getGsmSignalStrength();
		}

	}

	public int getGsmSignalStrength() {
		return gsmSignalStrength;
	}

	public TelephonyManager getTelephony() {
		return telephony;
	}

	public void setListener(TrackerServiceListener listener) {
		this.listener = listener;
	}

	public void sendReport(TrackReport report) {
		lastReport = report;
		if (listener != null) {
			listener.onNewReport(report);
		}
	}

	public interface TrackerServiceListener {
		void onNewReport(TrackReport report);

		void onRequestStatusChanged(boolean requestRunning);

		void onStateChanged(boolean started);
	}

	public boolean isRequestRunning() {
		return (runningRequest > 0);
	}

	public TrackReport getLastReport() {
		return lastReport;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void stop() {
		if(trackTimerTask != null) {
			trackTimerTask.cancel();
		}
		started = false;
		if (listener != null) {
			listener.onStateChanged(started);
		}
	}

	private class TrackTimerTask extends TimerTask {

		@Override
		public void run() {
			if(! isRequestRunning()) {
				new TrackTask(Cause.PLANIFIED_TASK,TrackerService.this).start();
			}
		}
		
	}
	
}
