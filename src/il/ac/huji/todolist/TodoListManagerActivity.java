package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.ex2todolist.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toDoList = new ArrayList<String[]>();
		adapter = new CustomListAdapter(this,R.layout.list_view, toDoList);
		list = (ListView)findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		adapter.notifyDataSetChanged();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// identifies which activity returned these results
		if (requestCode == 1) {
			String[] arr = new String[2];
			
			String itemTitle=data.getStringExtra("title");          
			arr[0] = itemTitle;
			adapter.notifyDataSetChanged();
			
			long itemDate = data.getLongExtra("dueDate", -1);
			Date date = new Date(itemDate );
			
			String DATE_FORMAT = "dd-MM-yy";
		    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			String itemDateStr = sdf.format(date);
		    
		    arr[1] = itemDateStr;
		    toDoList.add(arr);
			adapter.notifyDataSetChanged();
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
	    	  toDoList.remove(info.position);
	    	  adapter.notifyDataSetChanged();
	      }
	      if(item.getItemId()==CALL_ID){
	    	 String title = toDoList.get(info.position)[0];
	    	 title = (title.replace("-", ""));
	    	 int phoneNum = Integer.parseInt(title.replace("Call ", ""));
	    	 Intent dial = new Intent(Intent.ACTION_DIAL, 
			 Uri.parse("tel:"+phoneNum)); 
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

}
