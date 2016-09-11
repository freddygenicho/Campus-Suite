package com.thegads.uliza.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.thegads.uliza.R;
import com.thegads.uliza.activity.MessageActivity;
import com.thegads.uliza.adapter.MessageAdapter;
import com.thegads.uliza.helper.SimpleDividerItemDecoration;
import com.thegads.uliza.interfaces.RecyclerView_OnClickListener;
import com.thegads.uliza.model.NewMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private static final String TAG = MessageFragment.class.getSimpleName();
    private static final String MESSAGE_LIST = "messageList";
    private List<NewMessage> messageList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout no_notify;
    private LinearLayout refreshLayout;
    private MessageAdapter adapter;

    public MessageFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.messageList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: ");
        outState.putParcelableArrayList(MESSAGE_LIST, (ArrayList<? extends Parcelable>) messageList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        if (savedInstanceState != null) {
            messageList = savedInstanceState.getParcelableArrayList(MESSAGE_LIST);
            updateDisplay(messageList);
        } else {
            messageList = NewMessage.listAll(NewMessage.class, "id desc");
            updateDisplay(messageList);
        }
    }

    public void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_message_recycler_view);
        no_notify = (LinearLayout) view.findViewById(R.id.no_notification);
        no_notify.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.notification_swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageList = NewMessage.listAll(NewMessage.class, "id desc");
                updateDisplay(messageList);
            }
        });
        refreshLayout = (LinearLayout) view.findViewById(R.id.refresh_layout);

    }

    public void updateDisplay(final List<NewMessage> messageList) {
        if (messageList.size() > 0) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (refreshLayout.getVisibility() == View.VISIBLE) {
                refreshLayout.setVisibility(View.GONE);
            }

            no_notify.setVisibility(View.GONE);
            adapter = new MessageAdapter(getActivity(), messageList);
            adapter.setOnClickListener(new RecyclerView_OnClickListener.OnClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    Intent intent = new Intent(getActivity(), MessageActivity.class);
                    intent.putExtra(MessageActivity.MESSAGE, (Serializable) messageList.get(position));
                    getActivity().startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            no_notify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            messageList = savedInstanceState.getParcelableArrayList(MESSAGE_LIST);
            updateDisplay(messageList);
        } else {
            messageList = NewMessage.listAll(NewMessage.class, "id desc");
            updateDisplay(messageList);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.message_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            performRefresh();
            return true;
        }

        if (item.getItemId() == R.id.action_mark_read) {
            NewMessage.executeQuery("UPDATE NEW_MESSAGE SET read = ? ", "0");
            adapter.notifyDataSetChanged();
            adapter.setMessage(NewMessage.listAll(NewMessage.class, "id desc"));
            return true;
        }

        if (item.getItemId() == R.id.action_mark_unread) {
            NewMessage.executeQuery("UPDATE NEW_MESSAGE SET read = ? ", "1");
            adapter.notifyDataSetChanged();
            adapter.setMessage(NewMessage.listAll(NewMessage.class, "id desc"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performRefresh() {
        refreshLayout.setVisibility(View.VISIBLE);
        showView(refreshLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
                hideView(refreshLayout);
                messageList = NewMessage.listAll(NewMessage.class, "id desc");
                updateDisplay(messageList);
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showView(View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        float finalRadius = (float) Math.hypot(cx, cy);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            view.setVisibility(View.VISIBLE);
            animator.start();
        }
    }

    private void hideView(final View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        float initialRadius = (float) Math.hypot(cx, cy);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }
    }
}
