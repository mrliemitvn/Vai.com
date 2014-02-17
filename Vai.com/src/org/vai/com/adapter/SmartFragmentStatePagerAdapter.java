package org.vai.com.adapter;

import com.actionbarsherlock.app.SherlockFragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/* 
 Extension of FragmentStatePagerAdapter which intelligently caches 
 all active fragments and manages the fragment lifecycles. 
 Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
 */
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
	// Sparse array to keep track of registered fragments in memory
	private SparseArray<SherlockFragment> registeredFragments = new SparseArray<SherlockFragment>();

	public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	// Register the fragment when the item is instantiated
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		SherlockFragment fragment = (SherlockFragment) super.instantiateItem(container, position);
		registeredFragments.put(position, fragment);
		return fragment;
	}

	// Unregister when the item is inactive
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	// Returns the fragment for the position (if instantiated)
	public SherlockFragment getRegisteredFragment(int position) {
		return registeredFragments.get(position);
	}
}
