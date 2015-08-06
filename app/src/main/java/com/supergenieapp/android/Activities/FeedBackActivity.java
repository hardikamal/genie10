package com.supergenieapp.android.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.supergenieapp.android.CustomViews.Button.ButtonRectangle;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.Objects.Register;
import com.supergenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FeedBackActivity extends GenieBaseActivity {
    @InjectView(R.id.button)
    ButtonRectangle buttonRectangle;
    @InjectView(R.id.edittext)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.inject(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        buttonRectangle.setTextColor(getResources().getColor(R.color.color999));
        buttonRectangle.setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().length() > 0) {
                    buttonRectangle.setEnabled(true);
                    buttonRectangle.setTextColor(getResources().getColor(R.color.white));
                } else {
                    buttonRectangle.setEnabled(false);
                    buttonRectangle.setTextColor(getResources().getColor(R.color.color999));
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button)
    public void OnClickSubmit() {
        if (editText.getText().toString().trim().length() > 0) {
            submitFeedback(editText.getText().toString());
        } else {
            Crouton.makeText(FeedBackActivity.this, "Please type some feedback to submit", Style.INFO).show();
        }
    }

    private void submitFeedback(String feedback) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submiting Feedback");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject json = new JSONObject();
        try {
            json.put("feedback", feedback);
            JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.USERFEEDBACKURL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mixPanelTimerStop(DataFields.getServerUrl() + DataFields.USERFEEDBACKURL);
                            progressDialog.cancel();
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    mixPanelTimerStop(DataFields.getServerUrl() + DataFields.USERFEEDBACKURL);
                    Crouton.makeText(FeedBackActivity.this, getString(R.string.failedtosubmitfeedback), Style.ALERT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                    return params;
                }
            };

            genieApplication.addToRequestQueue(req);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
