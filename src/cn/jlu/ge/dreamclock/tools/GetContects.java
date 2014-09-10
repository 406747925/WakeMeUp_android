package cn.jlu.ge.dreamclock.tools;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class GetContects{
final String TAG="abc";
private Context context;
private String names;
private String numbers;
public GetContects(Context c) {
	// TODO Auto-generated constructor stub
	context=c;
	names="";
	numbers="";
}
	
	public  ArrayList<String> getAllContacts()
	{	
        Cursor cursor = context.getContentResolver().query( ContactsContract.Contacts.CONTENT_URI, 
                null, null, null, null);
       int contactIdIndex = 0;
       int nameIndex = 0;
       Log.i(TAG, "1");
       if(cursor.getCount() > 0) {
           contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
           nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
       }
       while(cursor.moveToNext()) {
           String contactId = cursor.getString(contactIdIndex);
           String name = cursor.getString(nameIndex);
           name=name.replace(" ", "");
           Log.i(TAG, contactId);
           Log.i(TAG, name);
           /*
            * 查找该联系人的phone信息
            */
           Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                   null, 
                   ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, 
                   null, null);
           int phoneIndex = 0;
           if(phones.getCount() > 0) {
               phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
           }
           while(phones.moveToNext()) {
               String phoneNumber = phones.getString(phoneIndex);
               if(phoneNumber.contains("+86"))
               {
            	  phoneNumber= phoneNumber.substring(3);
               }
               phoneNumber=phoneNumber.replace(" ", "");
               Log.i(TAG, phoneNumber);
               try{
               numbers=numbers+URLEncoder.encode(phoneNumber,"UTF-8")+",";
               names=names+URLEncoder.encode(name, "UTF-8")+",";
               ContactsDBAdapter dbadapter=new ContactsDBAdapter(context);
               
               dbadapter.insertRow(phoneNumber,name);
               }catch(Exception e){}
           }
           Log.i(TAG, "1");
		
	}
     numbers=  numbers.substring(0, numbers.length()-1);
     names=names.substring(0,names.length()-1);
    ArrayList<String> l=new ArrayList();
     l.add(numbers);
     l.add(names);
	return l;
	}

}
