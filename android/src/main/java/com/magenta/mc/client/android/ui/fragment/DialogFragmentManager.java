package com.magenta.mc.client.android.ui.fragment;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.util.StringUtilities;

public class DialogFragmentManager {

    private FragmentManagerProducer fragmentManagerProducer;

    public DialogFragmentManager(final FragmentActivity fragmentActivity) {
        this.fragmentManagerProducer = new FragmentManagerProducer() {
            public FragmentManager getFragmentManager() {
                return fragmentActivity.getSupportFragmentManager();
            }
        };
    }

    public DialogFragmentManager(final Fragment fragment) {
        this.fragmentManagerProducer = new FragmentManagerProducer() {
            @Override
            public FragmentManager getFragmentManager() {
                return fragment.getChildFragmentManager();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> T getParent(Class<T> parentType, DialogFragment dialogFragment) {
        Fragment parentFragment = dialogFragment.getParentFragment();
        Object candidate = parentFragment == null ? dialogFragment.getActivity() : parentFragment;
        if (candidate == null) {
            MCLoggerFactory.getLogger().warn("Can't find parent activity or fragment");
            return null;
        }
        if (parentType.isAssignableFrom(candidate.getClass())) {
            return (T) candidate;
        }
        return null;
    }

    public void showDialog(DialogFragment fragment, String tag) {
        FragmentManager fm = fragmentManagerProducer.getFragmentManager();
        DialogFragment dialogFragment = (DialogFragment) fm.findFragmentByTag(tag);
        if (dialogFragment == null || !dialogFragment.isAdded()) {
            fragment.show(fm, tag);
        }
    }

    public void showDialog(DialogFragment fragment) {
        showDialog(fragment, StringUtilities.tag(fragment.getClass()));
    }

    public void dismissDialog(Class<? extends DialogFragment> fragmentClass) {
        String tag = StringUtilities.tag(fragmentClass);
        DialogFragment dialogFragment = (DialogFragment) fragmentManagerProducer.getFragmentManager().findFragmentByTag(tag);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    private interface FragmentManagerProducer {

        FragmentManager getFragmentManager();
    }
}