package com.sust.game;

import com.sust.game.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AboutActivity extends Activity {

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final boolean TOGGLE_ON_CLICK = true;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    Intent intent;

    Boolean isStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_about);

	final View controlsView = findViewById(R.id.fullscreen_content_controls);
	final View contentView = findViewById(R.id.about_content);

	// Set up an instance of SystemUiHider to control the system UI for
	// this activity.
	mSystemUiHider = SystemUiHider.getInstance(this, contentView,
		HIDER_FLAGS);
	mSystemUiHider.setup();
	mSystemUiHider
		.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
		    // Cached values.
		    int mControlsHeight;
		    int mShortAnimTime;

		    @Override
		    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		    public void onVisibilityChange(boolean visible) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			    // If the ViewPropertyAnimator API is available
			    // (Honeycomb MR2 and later), use it to animate the
			    // in-layout UI controls at the bottom of the
			    // screen.
			    if (mControlsHeight == 0) {
				mControlsHeight = controlsView.getHeight();
			    }
			    if (mShortAnimTime == 0) {
				mShortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			    }
			    controlsView
				    .animate()
				    .translationY(visible ? 0 : mControlsHeight)
				    .setDuration(mShortAnimTime);
			} else {
			    // If the ViewPropertyAnimator APIs aren't
			    // available, simply show or hide the in-layout UI
			    // controls.
			    controlsView.setVisibility(visible ? View.VISIBLE
				    : View.GONE);
			}

			if (visible && AUTO_HIDE) {
			    // Schedule a hide().
			    delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
		    }
		});

	// Set up the user interaction to manually show or hide the system UI.
	contentView.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View view) {
		if (TOGGLE_ON_CLICK) {
		    mSystemUiHider.toggle();
		} else {
		    mSystemUiHider.show();
		}
	    }
	});

	// Upon interacting with UI controls, delay any scheduled hide()
	// operations to prevent the jarring behavior of controls going away
	// while interacting with the UI.
	findViewById(R.id.about_back_button).setOnTouchListener(
		mDelayHideTouchListener);

	isStarted = true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);

	// Trigger the initial hide() shortly after the activity has been
	// created, to briefly hint to the user that UI controls
	// are available.
	delayedHide(100);
    }

    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
	    if (AUTO_HIDE) {
		delayedHide(AUTO_HIDE_DELAY_MILLIS);
	    }
	    if (isStarted) {
		isStarted = false;
		intent = new Intent(AboutActivity.this, MenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

		finish();
	    }

	    return false;
	}
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
	@Override
	public void run() {
	    mSystemUiHider.hide();
	}
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
	mHideHandler.removeCallbacks(mHideRunnable);
	mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	if (isStarted) {
	    isStarted = false;
	    intent = new Intent(AboutActivity.this, MenuActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    finish();
	}
    }
}