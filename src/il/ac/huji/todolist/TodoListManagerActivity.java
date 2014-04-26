package il.ac.huji.todolist;

import java.text.SimpleDateFormat;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.ex2todolist.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TodoListManagerActivity extends Activity {

	private static final int CALL_ID = 12345;
	ArrayList<String[]> toDoList;
	public CustomListAdapter adapter;
	ListView list;
	CustomListAdapter myListView;


	private static String PARSE_APPLICATION_ID = "SOslw4rzYrqYoRH6vx6A2JtuTm9DGMHsEf1xqvdJ";
	private static String PARSE_CLIENT_KEY = "UxvXH56PVOqVanDugUVgZ6BJ5hO3ROX560XuCKw4";

	private String deviceId; 
	public ParseUser curUser;
	
	ProgressBar progressBar;


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

		toDoList = new ArrayList<String[]>();
		adapter = new CustomListAdapter(this,R.layout.list_view, toDoList);
		list = (ListView)findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		adapter.notifyDataSetChanged();
		
		progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleSmall);
		ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
		layout.addView(progressBar);
		progressBar.setVisibility(View.GONE);

		//show data from parse
		new AsyncSetDataFromParse().execute();
	}


	@Override
	protected void onDestroy() {
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
			progressBar.setVisibility(View.VISIBLE);
			ParseObject po = new ParseObject("todo");
			po.put("title", arr[0]);
			po.put("due", arr[1]);
			po.put("user", curUser);
			po.put("userId", deviceId);
			po.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					progressBar.setVisibility(View.GONE);
				}
			});
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
			progressBar.setVisibility(View.VISIBLE);
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
						object.deleteInBackground(new DeleteCallback(){
							@Override
							public void done(ParseException e) {
								progressBar.setVisibility(View.GONE);
							}
						});
						Log.d("todo", "delete the require object");
					}
				}
			});

			//delete from array
			toDoList.remove(info.position);
			adapter.notifyDataSetChanged();

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

	private class AsyncSetDataFromParse extends AsyncTask<Void, String, Void> {


		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("todo");
			query.whereEqualTo("userId", deviceId);
			query.addAscendingOrder("createdAt");
			try {
				List<ParseObject> items = query.find();
				if (items!=null){
					for(int i =0 ; i<items.size();i++){
						publishProgress(items.get(i).getString("title"),items.get(i).getString("due"));
					}
				}
				return null;

			} catch (ParseException e) {
				Log.d("todo", "Error: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if(values!=null){
				progressBar.setVisibility(View.GONE);
				String[] arr = new String[2];
				arr[0] = values[0];
				arr[1] = values[1];
				toDoList.add(arr);
				adapter.notifyDataSetChanged();
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			super.onPostExecute(result);
		}
	}

}
