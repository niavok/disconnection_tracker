package fredb219.disconnectiontracker;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import fredb219.disconnectiontracker.TrackReport.Cause;

import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class TrackTask extends Thread {

	private final Cause cause;
	private final TelephonyManager telephony;
	private final TrackerService trackerService;

	public TrackTask(Cause cause, TrackerService trackerService) {
		this.cause = cause;
		this.trackerService = trackerService;
		this.telephony = trackerService.getTelephony();
	}

	@Override
	public void run() {
		
		trackerService.setPendingRequest(true);
		
		TrackReport report = new TrackReport(cause);
		
		String line1Number = telephony.getLine1Number();
		int callState = telephony.getCallState();
		GsmCellLocation cellLocation = (GsmCellLocation) telephony
				.getCellLocation();
		List<NeighboringCellInfo> neighboringCellInfo = telephony
				.getNeighboringCellInfo();
		int dataActivity = telephony.getDataActivity();
		int dataState = telephony.getDataState();
		String deviceId = telephony.getDeviceId();
		String networkCountryIso = telephony.getNetworkCountryIso();
		String networkOperator = telephony.getNetworkOperator();
		String networkOperatorName = telephony.getNetworkOperatorName();
		int networkType = telephony.getNetworkType();
		String simCountryIso = telephony.getSimCountryIso();
		String simOperator = telephony.getSimOperator();
		String simOperatorName = telephony.getSimOperatorName();
		int simState = telephony.getSimState();
		boolean networkRoaming = telephony.isNetworkRoaming();
		// TODO Cause = manual test

		Log.d("TrackerService", "line1Number=" + line1Number);
		Log.d("TrackerService", "callState=" + callState);
		Log.d("TrackerService", "cellLocation=" + cellLocation);
		Log.d("TrackerService", "dataActivity=" + dataActivity);
		Log.d("TrackerService", "dataState=" + dataState);
		Log.d("TrackerService", "deviceId=" + deviceId);
		Log.d("TrackerService", "networkCountryIso=" + networkCountryIso);
		Log.d("TrackerService", "networkOperator=" + networkOperator);
		Log.d("TrackerService", "networkOperatorName=" + networkOperatorName);
		Log.d("TrackerService", "simCountryIso=" + simCountryIso);
		Log.d("TrackerService", "simOperator=" + simOperator);
		Log.d("TrackerService", "simOperatorName=" + simOperatorName);
		Log.d("TrackerService", "simState=" + simState);
		Log.d("TrackerService", "networkRoaming=" + networkRoaming);

		Log.d("TrackerService",
				"neighboringCellInfo=" + neighboringCellInfo.size());
		for (NeighboringCellInfo neighboringCellInfo2 : neighboringCellInfo) {
			Log.d("TrackerService", "    " + neighboringCellInfo2);
		}

		// Ping
		int pingTime = ping();
		if(pingTime == -1) {
			report.setPing(false, 0);
		} else {
			report.setPing(true, pingTime);
		}
		
		report.setNetworkType(networkType);
		report.setSignalStrength(trackerService.getGsmSignalStrength());
		
		report.close();
		
		trackerService.sendReport(report);
		
		trackerService.setPendingRequest(false);
		
	}

	private int ping() {
		
		int result = -1;
		
		try {
			
			long start = System.currentTimeMillis();
			
			HttpGet request = new HttpGet("http://google.com");

			HttpParams httpParameters = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			 HttpResponse response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				long stop = System.currentTimeMillis();
				result = (int) (stop - start);
			}

		} catch (SocketTimeoutException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		
		return result;
	}
}
