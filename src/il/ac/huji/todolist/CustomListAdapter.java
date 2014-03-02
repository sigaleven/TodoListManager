package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import com.example.ex2todolist.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String>{

	private Context mContext;
	private List <String> toDoList ;

	public CustomListAdapter(Context context, int textViewResourceId , ArrayList<String> list ) 
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

		TextView text = (TextView) mView.findViewById(R.id.item);

		if(position%2==0)
		{
			text.setTextColor(Color.RED);
			text.setText(toDoList.get(position));
		}else
		{
			text.setTextColor(Color.BLUE);
			text.setText(toDoList.get(position));
		}
		return mView;
	}



}


