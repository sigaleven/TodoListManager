package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.ex2todolist.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String[]>{

	private Context mContext;
	private List <String[]> toDoList ;

	public CustomListAdapter(Context context, int textViewResourceId , ArrayList<String[]> list ) 
	{
		super(context, textViewResourceId, list);           
		mContext = context;
		toDoList = list ;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent){

		
		View mView = v;
		if(mView == null){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = vi.inflate(R.layout.list_view, null);
		}

		TextView tvTitle = (TextView) mView.findViewById(R.id.title);
		TextView tvDate = (TextView) mView.findViewById(R.id.date);

		String title = toDoList.get(position)[0];
		String date = toDoList.get(position)[1];
		
		String[] arr = date.split("-");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt("20"+arr[2]));
		cal.set(Calendar.MONTH, Integer.parseInt(arr[1])-1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
		
		Date d = cal.getTime();
		Date today = new Date();
		if(isDateSmallerComparator(d,today)){
			tvTitle.setTextColor(Color.RED);
			tvDate.setTextColor(Color.RED);
			tvTitle.setText(title);
			tvDate.setText(date);
		}else{
			tvTitle.setTextColor(Color.BLACK);
			tvDate.setTextColor(Color.BLACK);
			tvTitle.setText(title);
			tvDate.setText(date);
		}
		return mView;
	}

	private boolean isDateSmallerComparator(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) 
			return (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR))?true:false;
		if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) 
			return (c1.get(Calendar.MONTH) < c2.get(Calendar.MONTH))?true:false;
		if(c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)){
			return false;
		}
		return (c1.get(Calendar.DAY_OF_MONTH) < c2.get(Calendar.DAY_OF_MONTH))?true:false;
	}



}


