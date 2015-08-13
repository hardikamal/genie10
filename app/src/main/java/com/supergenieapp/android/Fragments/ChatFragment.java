package com.supergenieapp.android.Fragments;

import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;
import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.Activities.LocationActivity;
import com.supergenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.supergenieapp.android.CustomViews.Button.ButtonFlat;
import com.supergenieapp.android.CustomViews.Button.CircularButton;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingViewFlat;
import com.supergenieapp.android.CustomViews.ProgressBar.ProgressBarCircularIndeterminate;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.NotificationHandler;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.GenieFragment;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

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
    public static int position;
    long hide_time = 0;
    @InjectView(R.id.message)
    EditText message;
    @InjectView(R.id.more)
    ImageView moreButton;
    @InjectView(R.id.moreLayout)
    LinearLayout moreLayout;
    @InjectView(R.id.mute)
    CircularButton mute;
    @InjectView(R.id.muteText)
    TextView muteText;
    @InjectView(R.id.muteLayout)
    LinearLayout muteLayout;
    @InjectView(R.id.image)
    CircularButton image;
    @InjectView(R.id.imageText)
    TextView imageText;
    @InjectView(R.id.imageLayout)
    LinearLayout imageLayout;
    @InjectView(R.id.locationLayout)
    LinearLayout locationLayout;
    @InjectView(R.id.speechBox)
    LinearLayout speechBox;
    @InjectView(R.id.location)
    CircularButton location;
    @InjectView(R.id.locationText)
    TextView locationText;
    @InjectView(R.id.loadingview)
    LoadingViewFlat loadingViewFlat;
    @InjectView(R.id.doneButton)
    ButtonFlat doneButton;


    boolean wentBackground = false;
    boolean imageResource = true;
    private CustomChatAdapter chatAdapter;
    private ArrayList<Messages> messages = new ArrayList<>();
    String url;
    View rootView;
    ViewGroup viewGroup;

    public CustomChatAdapter getCustomChatAdapter() {
        return chatAdapter;
    }

    private HashMap<String, String> dataAdd = new HashMap<>();

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
        wentBackground = false;
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            if (!bundle.containsKey("position")) {
                position = -1;
            }

            id = bundle.getInt("id", 0);
            color = bundle.getString("color", color);
            hide_time = bundle.getLong("hide_time");
            url = bundle.getString("url");
        }
        loadingViewFlat.setLoadingBackgroundColor(Color.parseColor(color));
        loadingViewFlat.setTextColor(Color.parseColor(color));
        doneButton.setTextColor(Color.parseColor(color));
        dataAdd.put("Chat Fragment", String.valueOf(id));

        if (!sharedPreferences.getBoolean("agent", true)) {
            setDisable();
            dataAdd.put("Chat Enable", String.valueOf(false));
        } else {
            dataAdd.put("Chat Enable", String.valueOf(true));
            send.setButtonColor(Color.parseColor(color));
            send.setShadowColor(Color.parseColor(color));
            message.setTextColor(Color.parseColor(color));
            moreButton.setEnabled(true);
            mute.setButtonColor(Color.parseColor(color));
            mute.setShadowColor(Color.parseColor(color));
            image.setButtonColor(Color.parseColor(color));
            image.setShadowColor(Color.parseColor(color));
            location.setButtonColor(Color.parseColor(color));
            location.setShadowColor(Color.parseColor(color));
            muteText.setTextColor(Color.parseColor(color));
            imageText.setTextColor(Color.parseColor(color));
            locationText.setTextColor(Color.parseColor(color));
        }
        setMuteButton();
        new NotificationHandler(getActivity()).cancelNotification(DataFields.NotificationId);
        new NotificationHandler(getActivity()).cancelNotification(DataFields.ALERTMSG);

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
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_white_24dp, getActivity().getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
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
                    dataAdd.put("Chat", "Send Button");
                    localyticsBuild("Chat Send Button from Keyboard");
                    send.performClick();
                    return true;
                }
                return false;
            }
        });

        displayMessages(true, DataFields.ScrollPosition);

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    public void displayMessages(boolean status, int scroll) {
        messages = dbDataSource.getAllListBasedOnCategoryWithHideTime(String.valueOf(id), hide_time);
        Collections.sort(messages);

        dataAdd.put("Chat Messages", "Size " + messages.size());
        String present = "";
        String now = "";

        int i = 0;
        int lenght = messages.size();
        do {
            if (lenght > 0) {
                Messages handleMessageObject = messages.get(i);
                now = utils.convertLongToDate(handleMessageObject.getCreatedAt(), new SimpleDateFormat("yyyy MM dd"));
                if (handleMessageObject.getMessageType() != DataFields.DATESHOW) {
                    if (present.equals("") || !present.equals(now)) {
                        messages.add(i, new Messages("0", DataFields.DATESHOW, id, new MessageValues(), 0, messages.get(i).getCreatedAt(), 0, 0));
                        lenght = messages.size();
                    }
                    present = now;
                }
                i++;
            }
        } while (i != lenght);

        if (hide_time != 0 && status) {
            messages.add(0, new Messages("0", DataFields.LOADMORE, id, new MessageValues(), 0, 0, 0, 0));
        }


        recyclerView.removeAllViews();
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        if (linearLayoutManager != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() != -1) {
                        position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }
            });
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        chatAdapter = new CustomChatAdapter(messages, color, url, getActivity());
        recyclerView.setAdapter(chatAdapter);

        if (scroll == DataFields.ScrollDown) {
            scroll();
        }
        if (scroll == DataFields.ScrollPosition) {
            scroll(position);
        }
    }

    @Override
    public void onDetach() {
        hideKeyboard(getActivity());
        wentBackground = false;
        super.onDetach();
    }


    public void onActivityResultLocation(Intent data) {
        if (data.getExtras() != null) {
            dataAdd.put("Chat Display Location", data.getStringExtra("address"));
            final MessageValues messageValues = new MessageValues(DataFields.LOCATION, data.getStringExtra("address"), data.getDoubleExtra("lng", 0.00), data.getDoubleExtra("lat", 0.00));
            Messages messageObject = new Messages("1", DataFields.LOCATION, id, messageValues, 1, Utils.getCurrentTimeMillis(), 0, 0);
            dbDataSource.addNormal(messageObject);
            displayMessages(true, DataFields.ScrollDown);
            new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    do {
                        try {
                            Thread.sleep(DataFields.small400TimeOut);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i > 5) {
                            break;
                        }
                    } while (!genieApplication.getSocket().connected());

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONObject valueJSON = new JSONObject();
                                valueJSON.put("address", messageValues.getText());
                                valueJSON.put("lng", messageValues.getLng());
                                valueJSON.put("lat", messageValues.getLat());
                                emitMessage(DataFields.LOCATION, valueJSON);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        } else {
            Crouton.makeText(getActivity(), getString(R.string.errorinaccessinglocation), Style.ALERT, viewGroup).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        logging.LogV("Showed", "on Start");
        mBus.register(this);
        logging.LogI("On Start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Chat Screen");
        logging.LogV("on Resume Chat");
        dbDataSource.UpdateCatNotification(id, 0);
        new NotificationHandler(getActivity()).cancelNotification(DataFields.NotificationId);
        new NotificationHandler(getActivity()).resetNotification();
        if (wentBackground) {
            wentBackground = false;
            displayMessages(true, DataFields.ScrollDown);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        wentBackground = true;
        logging.LogV("on onPause Chat");
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        logging.LogV("Showed", "on Stop");
        localyticsBuildHashMap("General Run Chat Fragment", dataAdd);
        super.onStop();
    }

    @Subscribe
    public void onMessageReceived(final Messages messageObject) {
        dataAdd.put("Chat Message", "Received");
        if (messageObject.getCategory() == id) {
            dataAdd.put("Chat Message", "Updated");
            if (messageObject.getMessageValues().get_id() == DataFields.TEXT) {
                ((BaseActivity) getActivity()).speakOut(messageObject.getMessageValues().getText());
            } else if (messageObject.getMessageValues().get_id() == DataFields.LOCATION) {
                ((BaseActivity) getActivity()).speakOut(messageObject.getMessageValues().getText());
            } else if (messageObject.getMessageValues().get_id() == DataFields.IMAGE) {
                ((BaseActivity) getActivity()).speakOut(messageObject.getMessageValues().getText());
            } else if (messageObject.getMessageValues().get_id() == DataFields.PAYNOW) {
                ((BaseActivity) getActivity()).speakOut("pay to finish your order.");
            }
            displayMessages(true, DataFields.ScrollDown);
            scroll();
        } else {
            localyticsBuild("Message received when user is in different category");
            dataAdd.put("Message", "Different Category");
            Categories categories = dbDataSource.getCategories(messageObject.getCategory());
            if (categories != null)
                dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
            Crouton.makeText(getActivity(), genieApplication.getString(R.string.newmessagereceivedin) + categories.getName(), Style.CONFIRM, viewGroup).show();
        }
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {
        if (imageResource) {
            dataAdd.put("Chat Button", "Mic option clicked");
            hideKeyboard(getActivity());
            if (((BaseActivity) getActivity()).startSpeech()) {
//                GradientDrawable gd = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM,
//                        new int[]{Color.parseColor(color), Color.parseColor(color)});
//                gd.setAlpha(75);
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    speechBox.setBackground(gd);
//                } else {
//                    speechBox.setBackgroundDrawable(gd);
//                }
                speechBox.setVisibility(View.VISIBLE);
                message.setEnabled(false);
                send.setEnabled(false);
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
        } else {
            dataAdd.put("Chat Button", "Send clicked");
            final Animation animTranslate = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_center);
            buttonSend.startAnimation(animTranslate);
            String typedMessage = message.getText().toString().trim();
            message.setText("");
            MessageValues messageValues = new MessageValues(1, typedMessage);
            Messages messageObject = new Messages("1", DataFields.TEXT, id, messageValues, 1, Utils.getCurrentTimeMillis(), 0, 0);
            dbDataSource.addNormal(messageObject);
            displayMessages(true, DataFields.ScrollDown);
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
        dataAdd.put("Chat Message", "Sent");
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject subJson = new JSONObject();
            subJson.put("category", messageCategory);
            subJson.put("category_value", valueJSON);
            subJson.put("created_at", Utils.getCurrentTimeMillis());

            jsonObject.put("msg", subJson);
            jsonObject.put("cid", id);
            if (getActivity() != null && (((BaseActivity) getActivity()).getSocket().connected()))
                ((BaseActivity) getActivity()).getSocket().emit("user message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.more)
    public void onClickMore() {
        localyticsBuild("Clicked attachments in chat");
        if (moreLayout.getVisibility() == View.VISIBLE) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                moreButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_black_24dp, getActivity().getTheme()));
            } else {
                send.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_black_24dp));
            }
            moreLayout.setVisibility(View.GONE);
        } else if (moreLayout.getVisibility() == View.GONE) {
            moreLayout.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                moreButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp, getActivity().getTheme()));
            } else {
                send.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));
            }
        }
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

    private void scroll(int pos) {
        if (pos == 0 && this.getArguments().containsKey("position")) {
            recyclerView.scrollToPosition(pos);
            this.getArguments().remove("position");
        } else if (pos == -1) {
            scroll();
        } else {
            recyclerView.scrollToPosition(pos);
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
                moreButton.setEnabled(false);
                moreLayout.setVisibility(View.GONE);
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
                moreButton.setEnabled(true);
                messageLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }

    @OnClick(R.id.doneButton)
    public void onClickDoneButton() {
        localyticsBuild("Speech button done");
        message.setEnabled(true);
        send.setEnabled(true);
        ((BaseActivity) getActivity()).stopSpeech();
        speechBox.setVisibility(View.GONE);
    }

    @OnClick(R.id.muteLayout)
    public void onClickedMute() {
        if (sharedPreferences.getBoolean("isMuted", true)) {
            localyticsBuild("Text to speech unmuted");
            sharedPreferences.edit().putBoolean("isMuted", false).apply();
        } else {
            localyticsBuild("Text to speech muted");
            sharedPreferences.edit().putBoolean("isMuted", true).apply();
        }
        setMuteButton();
    }

    @OnClick(R.id.locationLayout)
    public void onClickedLocation() {
        localyticsBuild("Location Clicked");
        ((BaseActivity) getActivity()).startActivityForResult(new Intent((BaseActivity) getActivity(), LocationActivity.class), DataFields.LOCATIONRESULT);
        moreLayout.setVisibility(View.GONE);
        speechBox.setVisibility(View.GONE);
    }

    private void setMuteButton() {
        if (sharedPreferences.getBoolean("isMuted", true)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off_white_24dp, getActivity().getTheme()));
            } else {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off_white_24dp));
            }
            muteText.setText(getString(R.string.playsound));
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp, getActivity().getTheme()));
            } else {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
            }
            muteText.setText(getString(R.string.mutesound));
            ((BaseActivity) getActivity()).setTTS();
        }
    }

    public void postToMessageBox(String text) {
        message.setText(text);
        message.setEnabled(true);
        send.setEnabled(true);
    }

    public void closePopup() {
        speechBox.setVisibility(View.GONE);
        message.setEnabled(true);
        send.setEnabled(true);
    }
}