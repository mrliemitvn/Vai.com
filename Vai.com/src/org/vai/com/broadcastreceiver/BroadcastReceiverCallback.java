package org.vai.com.broadcastreceiver;

public interface BroadcastReceiverCallback {
	public void onComplete();

	/**
	 * Return error code + default error message
	 * @param requestCode
	 * @param message
	 */
	public void onError(int requestCode, String message);

	public void onSuccess();

	public void onDifferenceId();
}