package fredb219.disconnectiontracker;

import java.util.Date;

import android.telephony.TelephonyManager;

public class TrackReport {

	private Date startTime;
	private Date endTime;
	private int signalStrength;
	private int ping;
	private boolean isPinging;
	private int networkType;
	private final Cause cause;

	public enum Cause {
		USER_REQUEST, PHONE_STATE_CHANGE, PLANIFIED_TASK,
	}

	public TrackReport(Cause cause) {
		this.cause = cause;
		startTime = new Date();
	}

	public Cause getCause() {
		return cause;
	}

	public void close() {
		endTime = new Date();
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setPing(boolean isPinging, int ping) {
		this.isPinging = isPinging;
		this.ping = ping;
	}

	public int getPing() {
		return ping;
	}

	public boolean isPinging() {
		return isPinging;
	}

	public int getNetworkType() {
		return networkType;
	}

	public String getNetworkTypeName() {
		String type = "";
		switch (getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			type = "Unknown";
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			type = "GPRS (2G)";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			type = "EDGE (2.5G)";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			type = "UMTS (3G)";
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			type = "HSDPA (3.5G)";
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			type = "HSUPA (3.5G)";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			type = "HSPA (3.5G)";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			type = "CDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			type = "EVDO_0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			type = "EVDO_A";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			type = "EVDO_B";
			break;
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			type = "1xRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			type = "IDEN";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			type = "LTE (3,9G)";
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			type = "EHRPD";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			type = "HSPAP (4G)";
			break;
		default:
			type = "Very unknonw";
			break;
		}
		return type;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	public String getCauseName() {
		String causeName = "";
		switch (getCause()) {
		case PHONE_STATE_CHANGE:
			causeName = "Phone state changed";
			break;
		case PLANIFIED_TASK:
			causeName = "Planified task";
			break;
		case USER_REQUEST:
			causeName = "User request";
			break;
		default:
			causeName = "Unknown cause";
			break;
		}
		return causeName;
	}
}
