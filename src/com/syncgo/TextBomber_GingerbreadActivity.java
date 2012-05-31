package com.syncgo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.syncgo.model.ContactAdapter;
import com.syncgo.view.ContactListTextWatcher;
import com.syncgo.view.SendButtonListener;

public class TextBomber_GingerbreadActivity extends Activity {
//	private static final String[] contacts = {"Jon", "Jack", "Jill"};
	//private static final HashMap<String, String> displayToNumber = new HashMap<String, String>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupContactTextInput();
        setupSendButton();
    }

    /**
     * Example of how to setup a text view programmatically.
     */
    protected void setupHelloWorldTextView() {
        TextView textView = new TextView(this);
        textView.setText("Hello World!!!!!");
        setContentView(textView);
    }
    
    /**
     * Setup the send button.
     */
    protected void setupSendButton() {
    	Button sendButton = (Button)findViewById(R.id.button_send);
    	AutoCompleteTextView contactInput = (AutoCompleteTextView)findViewById(R.id.input_contact);
    	EditText messageInput = (EditText)findViewById(R.id.input_message);
    	EditText numberOfTimesInput = (EditText)findViewById(R.id.input_number);
    	
    	SendButtonListener onClickListener = new SendButtonListener();
    	onClickListener.setContactInput(contactInput);
    	onClickListener.setMessageInput(messageInput);
    	onClickListener.setNumberOfTimesInput(numberOfTimesInput);
    	onClickListener.setMainActivity(this);
    	sendButton.setOnClickListener(onClickListener);
    }
    
    /**
     * Setup the contact list input.
     */
    protected void setupContactTextInput() {
    	//create a contact adapter to the customized data
    	ContactAdapter adapter = new ContactAdapter(this, R.layout.contact_display, null, new String[]{ContactAdapter.ENTRY_COLUMN}, new int[]{R.id.contact_name});
    	
    	//setup the input
    	AutoCompleteTextView contactInput = (AutoCompleteTextView)findViewById(R.id.input_contact);
    	contactInput.addTextChangedListener(new ContactListTextWatcher());
    	contactInput.setAdapter(adapter);
    }
    
}