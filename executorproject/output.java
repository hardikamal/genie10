package com.supergenieapp.android.test;

import com.supergenieapp.android.Activities.SplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class RegisterUserTest extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public RegisterUserTest() {
		super(SplashScreenActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}
  
	public void testRun() {
        //Wait for activity: 'com.supergenieapp.android.Activities.SplashScreenActivity'
		solo.waitForActivity(com.supergenieapp.android.Activities.SplashScreenActivity.class, 2000);
        //Wait for activity: 'com.supergenieapp.android.Slides.WalkThroughActivity'
		assertTrue("com.supergenieapp.android.Slides.WalkThroughActivity is not found!", solo.waitForActivity(com.supergenieapp.android.Slides.WalkThroughActivity.class));
        //Click on ImageView
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.next));
        //Click on ImageView
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.next));
        //Click on Next
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.done));
        //Wait for activity: 'com.supergenieapp.android.Activities.RegisterActivity'
		assertTrue("com.supergenieapp.android.Activities.RegisterActivity is not found!", solo.waitForActivity(com.supergenieapp.android.Activities.RegisterActivity.class));
        //Click on Empty Text View
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.name));
        //Enter the text: 'Ravi'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.name));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.name), "Ravi");
        //Click on Empty Text View
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.number));
        //Enter the text: '5612870838'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.number));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.number), "5612870838");
        //Set default small timeout to 11883 milliseconds
		Timeout.setSmallTimeout(11883);
        //Click on Terms and Conditions
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.terms));
        //Wait for dialog
		solo.waitForDialogToOpen(5000);
        //Click on OK
		solo.clickOnView(solo.getView(android.R.id.button1));
        //Wait for dialog to close
		solo.waitForDialogToClose(5000);
        //Set default small timeout to 12573 milliseconds
		Timeout.setSmallTimeout(12573);
        //Enter the text: '2'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char2));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char2), "2");
        //Enter the text: '5'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char3));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char3), "5");
        //Enter the text: '7'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char4));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.char4), "7");
        //Wait for activity: 'com.supergenieapp.android.Activities.BaseActivity'
		assertTrue("com.supergenieapp.android.Activities.BaseActivity is not found!", solo.waitForActivity(com.supergenieapp.android.Activities.BaseActivity.class));
        //Assert that: 'ImageView' is shown
		assertTrue("'ImageView' is not shown!", solo.waitForView(solo.getView(android.widget.ImageView.class, 0)));
        //Click on Empty Text View
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.action_profile));
        //Wait for activity: 'com.supergenieapp.android.Activities.UserProfileActivity'
		assertTrue("com.supergenieapp.android.Activities.UserProfileActivity is not found!", solo.waitForActivity(com.supergenieapp.android.Activities.UserProfileActivity.class));
        //Wait for dialog to close
		solo.waitForDialogToClose(5000);
        //Click on Ravi
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.name));
        //Enter the text: 'Ravi Gadipudi'
		solo.clearEditText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.name));
		solo.enterText((android.widget.EditText) solo.getView(com.supergenieapp.android.R.id.name), "Ravi Gadipudi");
        //Wait for dialog to close
		solo.waitForDialogToClose(5000);
        //Press menu back key
		solo.goBack();
        //Click on Empty Text View
		solo.clickOnView(solo.getView(com.supergenieapp.android.R.id.action_previous_orders));
        //Wait for activity: 'com.supergenieapp.android.Activities.OrderDetailsActivity'
		assertTrue("com.supergenieapp.android.Activities.OrderDetailsActivity is not found!", solo.waitForActivity(com.supergenieapp.android.Activities.OrderDetailsActivity.class));
        //Press menu back key
		solo.goBack();
        //Press menu back key
		solo.goBack();
	}
}
