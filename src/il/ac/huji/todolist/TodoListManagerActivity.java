package il.ac.huji.todolist;

import java.text.SimpleDateFormat;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.ex2todolist.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

	private static final int CALL_ID = 12345;
	ArrayList<String[]> toDoList;
	CustomListAdapter adapter;
	ListView list;
	CustomListAdapter myListView;


	private static String PARSE_APPLICATION_ID = "SOslw4rzYrqYoRH6vx6A2JtuTm9DGMHsEf1xqvdJ";
	private static String PARSE_CLIENT_KEY = "UxvXH56PVOqVanDugUVgZ6BJ5hO3ROX560XuCKw4";

	private SQLiteDatabase db;
	private String deviceId; 
	public ParseUser curUser;
	private DBHelper helper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		ParseUser.enableAutomaticUser();
		curUser = ParseUser.getCurrentUser();
		curUser.saveInBackground();
		
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		deviceId = mngr.getDeviceId();
		helper = new DBHelper(this);
		helper.onUpgrade(helper.getWritableDatabase(), 0, 0);
		db = helper.getWritableDatabase();

		toDoList = new ArrayList<String[]>();
		adapter = new CustomListAdapter(this,R.layout.list_view, toDoList);
		list = (ListView)findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		adapter.notifyDataSetChanged();
		
		//show data from parse
		setDataFromParse();
	}

	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// identifies which activity returned these results
		if (resultCode ==RESULT_OK) {
			String[] arr = new String[2];

			String itemTitle=data.getStringExtra("title");          
			arr[0] = itemTitle;
			adapter.notifyDataSetChanged();//????delete

			long itemDate = data.getLongExtra("dueDate", -1);
			Date date = new Date(itemDate );

			String DATE_FORMAT = "dd-MM-yy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			String itemDateStr = sdf.format(date);

			arr[1] = itemDateStr;
			toDoList.add(arr);
			adapter.notifyDataSetChanged();
			
			//update Parse ADD
			ParseObject po = new ParseObject("todo");
			po.put("title", arr[0]);
			po.put("due", arr[1]);
			po.put("user", curUser);
			po.put("userId", deviceId);
			po.saveInBackground();

			//update SQLite ADD
			ContentValues todoDB = new ContentValues();
			todoDB.put("title", arr[0]);
			todoDB.put("due", arr[1]);
			db.insert("todo_db", null, todoDB);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.lstTodoItems) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_list, menu);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(toDoList.get(info.position)[0]);
			if(toDoList.get(info.position)[0].startsWith("Call ")){
				menu.add(0, CALL_ID, 0, toDoList.get(info.position)[0]); 
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getItemId()==R.id.menuItemDelete){

			//update Parse DELETE
			ParseQuery<ParseObject> query = ParseQuery.getQuery("todo");
			query.whereEqualTo("title", toDoList.get(info.position)[0]);
			query.whereEqualTo("due", toDoList.get(info.position)[1]);
			query.whereEqualTo("userId", deviceId);
			query.getFirstInBackground(new GetCallback<ParseObject>() {
				@Override
				public void done(ParseObject object, ParseException e) {
					if (object == null) {
						Log.d("todo", "error cant find delete");
					} else {
						object.deleteInBackground();
						Log.d("todo", "delete the require object");
					}
				}
			});
			
			//delete from array
			toDoList.remove(info.position);
			adapter.notifyDataSetChanged();
			
			//syncSQLite();
			db.delete("todo_db", null, null);
			syncSQLite();
			
		}
		if(item.getItemId()==CALL_ID){
			String title = toDoList.get(info.position)[0];
			title = (title.replace("-", ""));
			Intent dial = new Intent(Intent.ACTION_DIAL, 
					Uri.parse("tel:"+title.replace("Call ", ""))); 
			startActivity(dial);
		}
		return super.onContextItemSelected(item);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId()==R.id.menuItemAdd){
			//Start dialog activity
			Intent intent = new Intent(getApplicationContext(),AddNewToDoListActivity.class);
			startActivityForResult(intent, 1);
		}
		return super.onOptionsItemSelected(item);
	}

	//===================================================================
	//======================= helper methods ============================
	//===================================================================
	
	
	/*
	 * update SQLite according to the current list
	 */
	private void syncSQLite() {
		//Sync SQLite
		for(int i=0 ; i< toDoList.size() ; i++){
			ContentValues todoDB = new ContentValues();
			todoDB.put("title", toDoList.get(i)[0]);
			todoDB.put("due", toDoList.get(i)[1]);
			db.insert("todo_db", null, todoDB);
		}
	}
	
	/*
	 * set data from Parse and update sqLite
	 */
	private void setDataFromParse() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("todo");
		query.whereEqualTo("userId", deviceId);
		query.addAscendingOrder("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> items, ParseException e) {
		        if (e == null) {
		            Log.d("todo", "Retrieved " + items.size() + " items");
		            for(int i =0 ; i<items.size();i++){
		            	Log.d("todo", "item: "+items.get(i).get("title"));
		            	String[] arr = new String[2];
						arr[0] = items.get(i).getString("title");
						arr[1] = items.get(i).getString("due");
						toDoList.add(arr);
						adapter.notifyDataSetChanged();
		            }
		            syncSQLite();
		        } else {
		            Log.d("todo", "Error: " + e.getMessage());
		        }
		    }
		});
		
	}
	
	
}
