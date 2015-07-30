package com.supergenieapp.android.Fragments;

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

import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.Activities.LocationActivity;
import com.supergenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.supergenieapp.android.CustomViews.Button.CircularButton;
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
    public int position = 0;
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

    private HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

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
            position = bundle.getInt("position", -1);
            id = bundle.getInt("id", 0);
            color = bundle.getString("color", color);
            hide_time = bundle.getLong("hide_time");
            url = bundle.getString("url");
        }
        mixpanelDataAdd.put("Chat Fragment", id);

        if (!sharedPreferences.getBoolean("agent", true)) {
            setDisable();
            mixpanelDataAdd.put("Chat Enable", false);
        } else {
            mixpanelDataAdd.put("Chat Enable", true);
            send.setButtonColor(Color.parseColor(color));
            send.setShadowColor(Color.parseColor(color));
            message.setTextColor(Color.parseColor(color));
        }

        new NotificationHandler(getActivity()).cancelNotification(DataFields.NotificationId);

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
                    mixpanelDataAdd.put("Chat", "Send Button");
                    mixPanelBuild("Chat Send Button from Keyboard");
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

        mixpanelDataAdd.put("Chat Messages", "Size " + messages.size());
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
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                DataFields.position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        chatAdapter = new CustomChatAdapter(messages, color, url, getActivity());
        recyclerView.setAdapter(chatAdapter);

        if (scroll == DataFields.ScrollDown) {
            scroll();
        }
        if (scroll == DataFields.ScrollPosition)
            scroll(position);
    }

    @Override
    public void onDetach() {
        mixpanelDataAdd.put("Chat Keyboard", "Hidden");
        hideKeyboard(getActivity());
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getExtras() != null) {
            mixpanelDataAdd.put("Chat Display Location", data.getStringExtra("address"));
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
        mixPanelTimerStart(ChatFragment.class.getName());
        logging.LogI("On Start");
    }

    @Override
    public void onResume() {
        super.onResume();
        logging.LogV("on Resume Chat");
        dbDataSource.UpdateCatNotification(id, 0);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        mixPanelTimerStop(ChatFragment.class.getName());
        mixPanelBuildHashMap("General Run " + ChatFragment.class.getName(), mixpanelDataAdd);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Subscribe
    public void onMessageReceived(final Messages messageObject) {
        mixpanelDataAdd.put("Chat Message", "Received");
        if (messageObject.getCategory() == id) {
            mixpanelDataAdd.put("Chat Message", "Updated");
            displayMessages(true, DataFields.ScrollDown);
            scroll();
        } else {
            mixPanelBuild("Message received when user is in different category");
            mixpanelDataAdd.put("Message", "Different Category");
            Categories categories = dbDataSource.getCategories(messageObject.getCategory());
            if (categories != null)
                dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
            System.out.println(dbDataSource.getCategories(messageObject.getCategory()).getNotification_count());
            Crouton.makeText(getActivity(), genieApplication.getString(R.string.newmessagereceivedin) + categories.getName(), Style.CONFIRM, viewGroup).show();
        }
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {
        if (imageResource) {
            mixpanelDataAdd.put("Chat Button", "Location clicked");
            hideKeyboard(getActivity());
            startActivityForResult(new Intent(getActivity(), LocationActivity.class), LOCATIONRESULT);
        } else {
            mixpanelDataAdd.put("Chat Button", "Send clicked");
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
        mixpanelDataAdd.put("Chat Message", "Sent");
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject subJson = new JSONObject();
            subJson.put("category", messageCategory);
            subJson.put("category_value", valueJSON);
            subJson.put("created_at", Utils.getCurrentTimeMillis());

            jsonObject.put("msg", subJson);
            jsonObject.put("cid", id);

            ((BaseActivity) getActivity()).getSocket().emit("user message", jsonObject);
            System.out.println(jsonObject.toString());
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
            position = messages.size() - 1;
            DataFields.position = messages.size() - 1;
        }
    }

    private void scroll(int position) {
        if (position == 0 && this.getArguments().containsKey("position")) {
            recyclerView.scrollToPosition(position);
        } else if (position == -1 && this.getArguments().containsKey("position")) {
            recyclerView.scrollToPosition(0);
        } else if (position == -1) {
            scroll();
        } else if (position == 0) {
            scroll();
        } else {
            recyclerView.scrollToPosition(position);
        }
        this.getArguments().remove("position");
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