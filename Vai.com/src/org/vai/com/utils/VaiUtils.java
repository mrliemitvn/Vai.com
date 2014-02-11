package org.vai.com.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class VaiUtils {

	/*
	 * Get screen size
	 * 
	 * @param context
	 * 
	 * @return array size 2. index 0 is screen width, index 1 is screen height
	 */
	public static int[] getScreenSize(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int[] size = new int[2];
		size[0] = displayMetrics.widthPixels;
		size[1] = displayMetrics.heightPixels;
		return size;
	}
}
