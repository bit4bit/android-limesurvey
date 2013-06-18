package org.bit4bit.android_limesurvey.controller.main;
/**
 * @deprecated
 */
import java.util.List;

import android.widget.ArrayAdapter;
import android.content.*;
import java.util.*;

import org.bit4bit.limesurvey.Survey;

public class SurveyArrayAdapter extends ArrayAdapter<Survey> {
	HashMap<Survey, Integer> mIdMap = new HashMap<Survey, Integer>();
	
	public SurveyArrayAdapter(Context context, int textViewResourceId, List<Survey> objects) {
		super(context, textViewResourceId, objects);
		for(int i=0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
		}
	}
	
	@Override
	public long getItemId(int position) {
		Survey item = getItem(position);
		return mIdMap.get(item);
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
}
