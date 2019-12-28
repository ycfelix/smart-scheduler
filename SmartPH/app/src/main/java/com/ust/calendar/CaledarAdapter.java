package com.ust.calendar;

import android.view.View;
import android.view.ViewGroup;

import com.ust.calendar.CalendarBean;

/**
 * Created by codbking on 2016/12/22.
 * email:codbking@gmail.com
 * github:https://github.com/codbking
 * blog:http://www.jianshu.com/users/49d47538a2dd/latest_articles
 */

public interface CaledarAdapter {
     View getView(View convertView, ViewGroup parentView, CalendarBean bean);
}
