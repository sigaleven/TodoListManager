package il.ac.huji.todolist;

import java.util.ArrayList;

import com.example.ex2todolist.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

	ArrayList<String> toDoList;
	CustomListAdapter adapter;
	ListView list;
	CustomListAdapter myListView;
	EditText edtNewItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtNewItem = (EditText)findViewById(R.id.edtNewItem);

		toDoList = new ArrayList<String>();
		adapter = new CustomListAdapter(this,R.layout.list_view, toDoList);
		list = (ListView)findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		adapter.notifyDataSetChanged();

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
			menu.setHeaderTitle(toDoList.get(info.position));
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
	      return super.onContextItemSelected(item);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId()==R.id.menuItemAdd){
			String newItem = edtNewItem.getText().toString();
			toDoList.add(newItem);
			adapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

}
