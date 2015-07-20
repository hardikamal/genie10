package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getgenieapp.android.Activities.BaseActivity;
import com.getgenieapp.android.Activities.LocationActivity;
import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.NotificationHandler;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.halfbit.tinybus.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Raviteja on 7/15/2015.
 */
public class ChatFragment extends GenieFragment {
    @InjectView(R.id.list)
    RecyclerView recyclerView;
    @InjectView(R.id.send)
    CircularButton send;
    @InjectView(R.id.messageLayout)
    LinearLayout messageLayout;
    String color = "#26ACEC";
    int id = 0;
    long hide_time = 0;
    @InjectView(R.id.message)
    EditText message;
    boolean imageResource = true;
    private CustomChatAdapter chatAdapter;
    private ArrayList<Messages> messages = new ArrayList<>();
    private int LOCATIONRESULT = 1;
    String url;
    View rootView;
    ViewGroup viewGroup;

    /**
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        this.viewGroup = container;
        rootView = inflater.inflate(R.layout.activity_chat, container, false);
        ButterKnife.inject(this, rootView);
        Crouton.cancelAllCroutons();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            id = bundle.getInt("id", 0);
            color = bundle.getString("color", color);
            hide_time = bundle.getLong("hide_time");
            url = bundle.getString("url");
        }

        if (sharedPreferences.getBoolean("agent", false)) {
            setDisable();
        }

        new NotificationHandler(getActivity()).cancelNotification(DataFields.NotificationId);
        dbDataSource.UpdateCatNotification(id, 0);
        send.setButtonColor(Color.parseColor(color));
        send.setShadowColor(Color.parseColor(color));
        message.setTextColor(Color.parseColor(color));


        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(DataFields.small400TimeOut);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                scroll();
                            }
                        });
                    }
                }).start();
            }
        });

        message.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(DataFields.small400TimeOut);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                scroll();
                            }
                        });
                    }
                }).start();
                return false;
            }
        });

        displayMessages();
        message.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scroll();
                if (message.getText().toString().trim().length() > 0) {
                    imageResource = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp, getActivity().getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));
                    }
                } else {
                    imageResource = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp, getActivity().getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send.performClick();
                    return true;
                }
                return false;
            }
        });

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void displayMessages() {
        messages = dbDataSource.getAllListBasedOnCategoryWithHideTime(String.valueOf(id), hide_time);

        String present = "";
        String now = "";

        ArrayList<Integer> messageTypes = new ArrayList<>();
        messageTypes.add(1);
        messageTypes.add(2);
        messageTypes.add(3);

        for (int i = messages.size() - 1; i >= 0; i--) {
            now = utils.convertLongToDate(messages.get(i).getCreatedAt(), new SimpleDateFormat("yyyy MM dd"));
            if (!present.equals("") && !present.equals(now)) {
                messages.add(i, new Messages("0", 1, 1, 9, id, new MessageValues(), 0, messages.get(i).getCreatedAt(), 0, 0));
            }
            if (i == 0) {
                messages.add(i, new Messages("0", 1, 1, 9, id, new MessageValues(), 0, messages.get(i).getCreatedAt(), 0, 0));
            }
            present = now;
        }

        if (hide_time != 0) {
            messages.add(0, new Messages("0", 1, 1, 8, id, new MessageValues(), 0, 0, 0, 0));
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatAdapter = new CustomChatAdapter(messages, color, url, getActivity());
        recyclerView.setAdapter(chatAdapter);
        scroll();
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_center_100);
        recyclerView.setAnimation(anim);
        anim.start();
    }

    @Override
    public void onDetach() {
        hideKeyboard(getActivity());
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getExtras() != null) {
            MessageValues messageValues = new MessageValues(3, data.getStringExtra("address"), data.getDoubleExtra("lng", 0.00), data.getDoubleExtra("lat", 0.00));
            Messages messageObject = new Messages("1", 1, 1, 2, id, messageValues, 1, System.currentTimeMillis() / 1000L, 0, 0);
            messages.add(messageObject);
            dbDataSource.addNormal(messageObject);
            chatAdapter.notifyDataSetChanged();
            scroll();
            JSONObject valueJSON = new JSONObject();
            try {
                valueJSON.put("text", messageValues.getText());
                valueJSON.put("lng", messageValues.getLng());
                valueJSON.put("lat", messageValues.getLat());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            emitMessage(DataFields.LOCATION, valueJSON);
        } else {
            Crouton.makeText(getActivity(), getString(R.string.errorinaccessinglocation), Style.ALERT, viewGroup).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        logging.LogV("on Resume Chat");
        displayMessages();
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Subscribe
    public void onMessageReceived(final Messages messageObject) {
        if (messageObject.getCategory() == id) {
            messages.add(messageObject);
            chatAdapter.notifyDataSetChanged();
            scroll();
        } else {
            Categories categories = dbDataSource.getCategories(messageObject.getCategory());
            dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
            System.out.println(dbDataSource.getCategories(messageObject.getCategory()).getNotification_count());
            Crouton.makeText(getActivity(), genieApplication.getString(R.string.newmessagereceivedin) + categories.getName(), Style.CONFIRM, viewGroup).show();
            // todo add notification
        }
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {

        final Animation animTranslate = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_center);
        buttonSend.startAnimation(animTranslate);

        if (imageResource) {
            hideKeyboard(getActivity());
            startActivityForResult(new Intent(getActivity(), LocationActivity.class), LOCATIONRESULT);
        } else {
            String typedMessage = message.getText().toString().trim();
            message.setText("");
            MessageValues messageValues = new MessageValues(1, typedMessage);
            Messages messageObject = new Messages("1", 1, 1, 1, id, messageValues, 1, System.currentTimeMillis() / 1000L, 0, 0);
            messages.add(messageObject);
            dbDataSource.addNormal(messageObject);
            chatAdapter.notifyDataSetChanged();
            scroll();
            if (typedMessage.equalsIgnoreCase("Pay Now")) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("companyname", "Genie");
                    jsonObject.put("rate", 775.00);
                    jsonObject.put("details", "This is some random text generated by me. Its being used by me to display some text at this place.");
                    jsonObject.put("cod", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MessageValues messageValues2 = new MessageValues(5, "www.google.com", jsonObject.toString());
                Messages messageObject2 = new Messages("1", 1, 1, 5, id, messageValues2, 1, System.currentTimeMillis() / 1000L, 0, 0);
                messages.add(messageObject2);
                dbDataSource.addNormal(messageObject2);
                chatAdapter.notifyDataSetChanged();
                scroll();
            }
            JSONObject valueJSON = new JSONObject();
            try {
                valueJSON.put("text", typedMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            emitMessage(DataFields.TEXT, valueJSON);
        }
    }

    private void emitMessage(int messageCategory, JSONObject valueJSON) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject subJson = new JSONObject();
            subJson.put("category", messageCategory);
            subJson.put("category_value", valueJSON);
            subJson.put("created_at", System.currentTimeMillis());

            jsonObject.put("msg", subJson);
            jsonObject.put("cid", id);
            ((BaseActivity) getActivity()).getSocket().emit("user message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.messageLayout)
    public void onClickMessageBox() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(DataFields.small400TimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        scroll();
                    }
                });
            }
        }).start();
    }

    private void scroll() {
        if (messages.size() > 1) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    public void setDisable() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.setHint(genieApplication.getString(R.string.notavailablemessage));
                message.setEnabled(false);
                send.setEnabled(false);
                send.setButtonColor(getResources().getColor(R.color.color999));
                send.setShadowColor(getResources().getColor(R.color.color999));
                messageLayout.setBackgroundColor(getResources().getColor(R.color.colorddd));
            }
        });
    }

    public void setEnable() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!message.isEnabled()) {
                    Crouton.cancelAllCroutons();

                    message.setHint(genieApplication.getString(R.string.typeamessage));
                    message.setEnabled(true);
                }
                if (!send.isEnabled()) {
                    send.setButtonColor(Color.parseColor(color));
                    send.setShadowColor(Color.parseColor(color));
                    send.setEnabled(true);
                }
                messageLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }
}