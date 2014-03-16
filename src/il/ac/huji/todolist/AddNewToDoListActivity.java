package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.Date;

import com.example.ex2todolist.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewToDoListActivity extends Activity implements OnClickListener {
	Button btnOk;
	Button btnCancel;
	DatePicker datePicker;
	String itemTitle;
	EditText edtNewItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_to_do_list);

		edtNewItem = (EditText)findViewById(R.id.edtNewItem);
		btnOk = (Button)findViewById(R.id.btnOk);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		datePicker = (DatePicker)findViewById(R.id.datePicker);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_new_to_do_list, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {

		if(arg0.getId()==R.id.btnCancel){
			finish();
		}
		if(arg0.getId()==R.id.btnOk){
			String item = edtNewItem.getText().toString();
			Date date = getTimeDatePicker();


			Intent intent = new Intent(getApplicationContext(),TodoListManagerActivity.class);
			intent.putExtra("title", item);
			intent.putExtra("dueDate", date.getTime());
			setResult(RESULT_OK,intent);
			finish();
		}

	}

	private Date getTimeDatePicker() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, datePicker.getYear() - 1900);
		cal.set(Calendar.MONTH, datePicker.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		Date date = cal.getTime();
		return date;

	}

}
