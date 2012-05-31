package com.syncgo.model;

import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.ContactsContract;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

/**
 * ContactAdapter, prepares the data for use by the AutocompleteSelect
 * @author jkeam
 *
 */
public class ContactAdapter extends SimpleCursorAdapter implements FilterQueryProvider {
	private ContentResolver contentResolver;
	public static final String ID_COLUMN = "_id";		//this field is required b/c of the simpleCursorAdapter requires it.  I'm setting to the number id so that I can look it up if I need to
	public static final String ENTRY_COLUMN = "entry";
	public static final int MAX_SUGGESTIONS = 5;
	private HashMap<String, String> entryToNumber = new HashMap<String, String>();	//map of the visual entry to the number used to call.
	
	public ContactAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		contentResolver = context.getContentResolver();
		setFilterQueryProvider(this);
	}
	
	@Override
	public String convertToString(Cursor cursor) {
		String entry = cursor.getString(cursor.getColumnIndex(ENTRY_COLUMN));
		String number = entryToNumber.get(entry);
		return number;
	}
	
	public ContentResolver getContentResolver() {
		return contentResolver;
	}

	public void setContentResolver(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		//create temporary hashset to test for duplicates
		HashSet<String> uniqueEntries = new HashSet<String>();
		
		//create matrix cursor
		String[] columnNames = new String[]{ID_COLUMN, ENTRY_COLUMN};
		MatrixCursor matrixCursor = new MatrixCursor(columnNames);

		//get names
        StringBuilder buffer = null;
        String[] args = null;
        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append(ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0 AND ");
            buffer.append("UPPER(");
            buffer.append(ContactsContract.Contacts.DISPLAY_NAME);
            buffer.append(") GLOB ?");
            args = new String[] { constraint.toString().toUpperCase() + "*" };
        }

        String sort = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        Cursor nameCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, buffer == null ? null : buffer.toString(), args, sort);
        
        //get numbers
        if (nameCursor.getCount() > 0) {
        	Integer i = 0;
        	entryToNumber.clear();
        	while (nameCursor.moveToNext() && i < MAX_SUGGESTIONS) {
        		if (Integer.parseInt(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
        			String contactId = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts._ID));
        			String name = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        			Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        			while (phoneCursor.moveToNext() && i < MAX_SUGGESTIONS) {
        				String type = getTypeLabel(phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
        				String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        				String numberId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
        				number = number.replace("-", "");
        				number = number.replace(" ", "");
        				number = number.replace("+", "");
        				
        				String entry = "";
        				if (type == null) {
        					entry = name + ": " + number;
        				}
        				else {
        					entry = name + " (" + type + "): " + number;
        				}
        				
        				if (uniqueEntries.add(entry)) {
	        				matrixCursor.addRow(new String[]{numberId, entry});
	        				i++;
	        				entryToNumber.put(entry, number);
        				}
        			}
        			phoneCursor.close();
        		}
        	}
        }
        uniqueEntries.clear();
        uniqueEntries = null;
        
        return matrixCursor;
	}

	/**
	 * Given the type number, looks up the type label to be shown.  Surprised that the android api doesn't have anything like this.
	 * 	Return null if nothing found.
	 * @param type
	 * @return
	 */
	protected String getTypeLabel(int type) {
		String typeLabel = null;
		switch (type) {
		case 19: typeLabel = "asst"; break;	//assistant
		case 8: typeLabel = "call";	break;	//callback
		case 9: typeLabel = "car"; break;		
		case 10: typeLabel = "company main"; break;
		case 5: typeLabel = "hf"; break;		//home fax
		case 4: typeLabel = "wf"; break;		//work fax
		case 1: typeLabel = "h"; break;		//home
		case 11: typeLabel = "isdn"; break;	//isdn
		case 12: typeLabel = "main"; break;	//main
		case 20: typeLabel = "mms"; break;		//mms
		case 2: typeLabel = "m"; break;		//mobile
		case 7: typeLabel = "o"; break;		//other
		case 13: typeLabel = "of"; break;	//other fax
		case 6: typeLabel = "p"; break;		//pager
		case 14: typeLabel = "r"; break;		//radio
		case 15: typeLabel = "telex"; break;	//telex
		case 16: typeLabel = "tty"; break;
		case 3: typeLabel = "w"; break;	//work
		case 17: typeLabel = "wm"; break;		//work mobile
		case 18: typeLabel = "wp"; break;		//work pager
		default: typeLabel = null; break;
		}
		
		return typeLabel;
	}
}
