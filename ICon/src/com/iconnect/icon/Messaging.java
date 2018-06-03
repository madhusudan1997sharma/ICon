package com.iconnect.icon;


import java.io.UnsupportedEncodingException;

import com.iconnect.icon.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iconnect.icon.interfaces.IAppManager;
import com.iconnect.icon.services.IMService;
import com.iconnect.icon.tools.FriendController;
import com.iconnect.icon.tools.LocalStorageHandler;
import com.iconnect.icon.types.FriendInfo;
import com.iconnect.icon.types.MessageInfo;


public class Messaging extends Activity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	public String name;
	public String username;
	private EditText messageText;
	private TextView messageHistoryText;
	private Button sendMessageButton;
	private Button camera;
	private TextView you;
	private IAppManager imService;
	private FriendInfo friend = new FriendInfo();
	private LocalStorageHandler localstoragehandler; 
	private Cursor dbCursor;
	private static final int ADD_NEW_FRIEND_ID = Menu.FIRST;
	public String ownusername = new String();
	
	private ServiceConnection mConnection = new ServiceConnection() {
      
		
		
		public void onServiceConnected(ComponentName className, IBinder service) {          
            imService = ((IMService.IMBinder)service).getService();
    		ownusername = imService.getUsername();
    		setTitle(friend.name);
        	you.setText(ownusername);
        }
        public void onServiceDisconnected(ComponentName className) {
        	imService = null;
            Toast.makeText(Messaging.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	   
		
		setContentView(R.layout.messaging_screen); //messaging_screen);
		
		messageHistoryText = (TextView) findViewById(R.id.messageHistory);
		messageText = (EditText) findViewById(R.id.message);
		messageText.requestFocus();
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		camera = (Button) findViewById(R.id.cameraButton);
		you = (TextView) findViewById(R.id.textView1);
		
		camera.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				 startActivityForResult(intent, 0); 
			}
		});
		
		messageText.addTextChangedListener(new TextWatcher() {
			@SuppressWarnings("deprecation")
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if(messageText.getText().toString().trim().length() > 0) {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.send));
					camera.setVisibility(View.GONE);
				}
				else {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.microphone));
					camera.setVisibility(View.VISIBLE);
				}
				
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(messageText.getText().toString().trim().length() > 0) {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.send));
					camera.setVisibility(View.GONE);
				}
				else {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.microphone));
					camera.setVisibility(View.VISIBLE);
				}
				
			}

			@SuppressWarnings("deprecation")
			@Override
			public void afterTextChanged(Editable s) {
				if(messageText.getText().toString().trim().length() > 0) {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.send));
					camera.setVisibility(View.GONE);
				}
				else {
					sendMessageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.microphone));
					camera.setVisibility(View.VISIBLE);
				}
				
			}
		});
		
		Bundle extras = this.getIntent().getExtras();
		
		friend.name = extras.getString(FriendInfo.NAME);
		friend.userName = extras.getString(FriendInfo.USERNAME);
		friend.ip = extras.getString(FriendInfo.IP);
		friend.port = extras.getString(FriendInfo.PORT);
		String msg = extras.getString(MessageInfo.MESSAGETEXT);
		messageHistoryText.setMovementMethod(new ScrollingMovementMethod());		
		
	
		
		//EditText friendUserName = (EditText) findViewById(R.id.friendUserName);
		//friendUserName.setText(friend.userName);
		

		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(friend.userName, IMService.USERNAME);
		
		if (dbCursor.getCount() > 0){
		int noOfScorer = 0;
		dbCursor.moveToFirst();
		    while ((!dbCursor.isAfterLast())&&noOfScorer<dbCursor.getCount()) 
		    {
		        noOfScorer++;
		        
		    	if (dbCursor.getString(2).equals(you.getText())) {
		    		this.appendToMessageHistory("You" , dbCursor.getString(3));
		    	}
		    	else {
		    		this.appendToMessageHistory(dbCursor.getString(2) , dbCursor.getString(3));
		    	}
			    
		        dbCursor.moveToNext();
		    }
		}
		localstoragehandler.close();
		
		if (msg != null) 
		{
			this.appendToMessageHistory(friend.userName ,  msg);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
		}
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
			Handler handler = new Handler();
			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length()>0) 
				{		
					appendToMessageHistory(imService.getUsername(), message.toString());
					
					localstoragehandler.insert(imService.getUsername(), friend.userName, message.toString());
								
					messageText.setText("");
					Thread thread = new Thread(){					
						public void run() {
							try {
								if (imService.sendMessage(imService.getUsername(), friend.userName, message.toString()) == null)
								{
									
									handler.post(new Runnable(){	

										public void run() {
											
									        Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

											
											//showDialog(MESSAGE_CANNOT_BE_SENT);										
										}
										
									});
								}
							} catch (UnsupportedEncodingException e) {
								Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

								e.printStackTrace();
							}
						}						
					};
					thread.start();
										
				}
				
			}});
		
		messageText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if (keyCode == 66){
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
			
			
		});
				
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id)
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
		break;
		}
		
		if (message == -1)
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder(Messaging.this)       
			.setMessage(message)
			.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			})        
			.create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
		unbindService(mConnection);
		
		FriendController.setActiveFriend(null);
		
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		bindService(new Intent(Messaging.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
				
		IntentFilter i = new IntentFilter();
		i.addAction(IMService.TAKE_MESSAGE);
		
		registerReceiver(messageReceiver, i);
		
		FriendController.setActiveFriend(friend.userName);		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);		

		menu.add(0, ADD_NEW_FRIEND_ID, 0, R.string.add_new_friend);
		
		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{		

		switch(item.getItemId()) 
		{	  
			case ADD_NEW_FRIEND_ID:
			{
				Intent i = new Intent(Messaging.this, AddFriend.class);
				startActivity(i);
				return true;
			}			
		}

		return super.onMenuItemSelected(featureId, item);		
	}
	
	
	public class  MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{	
			Bundle extra = intent.getExtras();
			String username = extra.getString(MessageInfo.USERID);			
			String message = extra.getString(MessageInfo.MESSAGETEXT);
			
			if (username != null && message != null)
			{
				if (friend.userName.equals(username)) {
					appendToMessageHistory(username, message);
					localstoragehandler.insert(username, imService.getUsername(), message);
					
				}
				else {
					if (message.length() > 15) {
						message = message.substring(0, 15);
					}
					Toast.makeText(Messaging.this,  username + " says '"+
													message + "'",
													Toast.LENGTH_SHORT).show();		
				}
			}			
		}
		
	};
	private MessageReceiver messageReceiver = new MessageReceiver();
	
	public  void appendToMessageHistory(String username, String message) {
		if (username != null && message != null) {
			
			if(username != ownusername) {
				messageHistoryText.append(" " + username + ":  ");
				messageHistoryText.append(message + "\n" + "\n");
			}
			else {
				messageHistoryText.append(" " + "You" + ":  ");
				messageHistoryText.append(message + "\n" + "\n");
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (localstoragehandler != null) {
	    	localstoragehandler.close();
	    }
	    if (dbCursor != null) {
	    	dbCursor.close();
	    }
	}
	

}
