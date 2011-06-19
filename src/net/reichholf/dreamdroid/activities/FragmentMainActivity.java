/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package net.reichholf.dreamdroid.activities;

import net.reichholf.dreamdroid.R;
import net.reichholf.dreamdroid.abstivities.MultiPaneHandler;
import net.reichholf.dreamdroid.fragment.ActivityCallbackHandler;
import net.reichholf.dreamdroid.fragment.NavigationFragment;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @author sre
 *
 */
public class FragmentMainActivity extends FragmentActivity implements MultiPaneHandler {	
	private boolean mMultiPane;
	
	private FragmentManager mFragmentManager;
	private NavigationFragment mNavigationFragment;
	private ActivityCallbackHandler mCallBackHandler;
	
	private boolean isNavigationDialog = false;
	
	public void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		checkLayout();
		
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		mNavigationFragment = new NavigationFragment();
		
		mFragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener(){

			@Override
			public void onBackStackChanged() {
				// TODO Auto-generated method stub
				
			}
			
		});
		setContentView(R.layout.dualpane);
		ft.add(R.id.navigation_view, mNavigationFragment, mNavigationFragment.getClass().getSimpleName());
		ft.commit();
	}
	
	private void checkLayout(){
		mMultiPane = false;
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		if(height >= 1024 || width >= 1024){
			mMultiPane = true;
		}		
	}
	
	
	@SuppressWarnings("rawtypes")
	public void showDetails(Class fragmentClass){
		if(mMultiPane){
			try {
				Fragment fragment = (Fragment) fragmentClass.newInstance();
				showDetails(fragment);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Intent intent = new Intent(this, SimpleFragmentActivity.class);
			intent.putExtra("fragmentClass", fragmentClass);
			startActivity(intent);
		}
			
	}
	
	@Override
	public void showDetails(Fragment fragment){
		showDetails(fragment, true);
	}
	
	public void back(){
		mFragmentManager.popBackStackImmediate();
	}
	
	/**
	 * @param fragment
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Override
	public void showDetails(Fragment fragment, boolean addToBackStack){	
		mCallBackHandler = (ActivityCallbackHandler) fragment;
		if(mMultiPane){			
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.replace(R.id.detail_view, fragment, fragment.getClass().getSimpleName());
			
			if(addToBackStack){
				ft.addToBackStack(null);
			}
			
			ft.commit();
		} else { //TODO Error Handling
			Intent intent = new Intent(this, SimpleFragmentActivity.class);
			intent.putExtra("fragmentClass", fragment.getClass());
			intent.putExtras(fragment.getArguments());
			startActivity(intent);
		}
	}
	
	@Override
	public void setTitle(CharSequence title){
		if(mMultiPane){
			TextView t = (TextView) findViewById(R.id.detail_title);
			String replaceMe = getText(R.string.app_name) + "::";
			t.setText(title.toString().replace(replaceMe, ""));
			return;
		}
		super.setTitle(title);
	}
	
	public void setIsNavgationDialog(boolean is){
		isNavigationDialog = is;
	}
	
	@Override
	protected Dialog onCreateDialog(int id){
		Dialog dialog = null;
		if(isNavigationDialog){
			dialog = mNavigationFragment.onCreateDialog(id);
			isNavigationDialog = false;
		} else {
			dialog = mCallBackHandler.onCreateDialog(id);
		}
		
		if(dialog == null){
			dialog = super.onCreateDialog(id);
		}
		
		return dialog;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(mCallBackHandler != null){
			if(mCallBackHandler.onKeyDown(keyCode, event)){
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(mCallBackHandler != null){
			if(mCallBackHandler.onKeyUp(keyCode, event)){
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
