package com.syncgo.view;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * After the send button is clicked, this is called.  Sends the text message.
 * @author jkeam
 *
 */
public class SendButtonListener implements OnClickListener {

	private AutoCompleteTextView contactInput;
	private EditText messageInput;
	private EditText numberOfTimesInput;
	private Activity mainActivity;
	
	/**
	 * When you click the send button this is called.
	 */
	public void onClick(View v) {
		String contact = contactInput.getText().toString();
		String message = messageInput.getText().toString();
		Integer numberOfTimes = Integer.parseInt(numberOfTimesInput.getText().toString());
		
		if (contact != null && message != null && numberOfTimes != null) {
			for (int i = 0; i < numberOfTimes; i++) {
				sendTextMessage(contact, message);
			}
		}
	}

	/**
	 * Sends the text message.  TODO: show spinner until its done sending
	 * @param phoneNumber
	 * @param message
	 */
	protected void sendTextMessage(String phoneNumber, String message) {
		Intent intent = new Intent();
		PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, pendingIntent, null);
	}
	
	
	public AutoCompleteTextView getContactInput() {
		return contactInput;
	}

	public void setContactInput(AutoCompleteTextView contactInput) {
		this.contactInput = contactInput;
	}

	public EditText getMessageInput() {
		return messageInput;
	}

	public void setMessageInput(EditText messageInput) {
		this.messageInput = messageInput;
	}

	public EditText getNumberOfTimesInput() {
		return numberOfTimesInput;
	}

	public void setNumberOfTimesInput(EditText numberOfTimesInput) {
		this.numberOfTimesInput = numberOfTimesInput;
	}

	public Activity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(Activity mainActivity) {
		this.mainActivity = mainActivity;
	}

}
