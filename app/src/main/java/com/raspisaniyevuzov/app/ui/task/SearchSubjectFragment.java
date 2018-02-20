package com.raspisaniyevuzov.app.ui.task;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.SubjectDao;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.ui.BaseFragment;

import java.util.List;

/**
 * Created by SAPOZHKOV on 13.10.2015.
 */
public class SearchSubjectFragment extends BaseFragment {

    private View line_divider;
    private RelativeLayout view_search;
    private CardView card_search;
    private ImageView image_search_back, clearSearch;
    private EditText edit_text_search;
    private ListView listView;
    private SearchSubjectAdapter searchSubjectAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_subject, container, false);

        view_search = (RelativeLayout) view.findViewById(R.id.view_search);

        line_divider = view.findViewById(R.id.line_divider);
        edit_text_search = (EditText) view.findViewById(R.id.edit_text_search);
        card_search = (CardView) view.findViewById(R.id.card_search);
        image_search_back = (ImageView) view.findViewById(R.id.image_search_back);
        clearSearch = (ImageView) view.findViewById(R.id.clearSearch);
        listView = (ListView) view.findViewById(R.id.listView);

        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleToolBar();
            }
        });
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edit_text_search.getText().toString().trim().length() > 0) {
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                        listView.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });

        searchSubjectAdapter = new SearchSubjectAdapter(getActivity(), 0, SubjectDao.getInstance().getSubList());
        listView.setAdapter(searchSubjectAdapter);
        InitiateSearch();
        IsAdapterEmpty();

        return view;
    }

    private void InitiateSearch() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                IsAdapterEmpty();
                handleToolBar();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((EditTaskActivity) getActivity()).onSubjectSelect(searchSubjectAdapter.getItem(position), true);
                handleToolBar();
            }
        });
        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_text_search.getText().toString().length() == 0) {
                    searchSubjectAdapter.notifyDataSetChanged();
                    searchSubjectAdapter = new SearchSubjectAdapter(getActivity(), 0, SubjectDao.getInstance().getSubList());
                    listView.setAdapter(searchSubjectAdapter);
                    searchSubjectAdapter.notifyDataSetChanged();
                    IsAdapterEmpty();
                } else {
                    searchSubjectAdapter = new SearchSubjectAdapter(getActivity(), 0, SubjectDao.getInstance().getByQuery(edit_text_search.getText().toString()));
                    listView.setAdapter(searchSubjectAdapter);
                    searchSubjectAdapter.notifyDataSetChanged();
                    clearSearch.setImageResource(R.drawable.ic_close_grey600_24dp);
                    IsAdapterEmpty();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_search.getText().toString().length() == 0) {
                } else {
                    edit_text_search.setText("");
                    listView.setVisibility(View.VISIBLE);
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty();
                }
            }
        });
    }

    private void IsAdapterEmpty() {
        if (searchSubjectAdapter.getCount() == 0) {
            line_divider.setVisibility(View.GONE);
        } else {
            line_divider.setVisibility(View.VISIBLE);
        }
    }

    private void handleToolBar() {
        final Animation fade_in = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        final Animation fade_out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        if (card_search.getVisibility() == View.VISIBLE) {
            // hide
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(card_search,
                        card_search.getWidth() - (int) convertDpToPixel(56, getContext()),
                        (int) convertDpToPixel(23, getContext()),
                        (float) Math.hypot(card_search.getWidth(), card_search.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view_search.startAnimation(fade_out);
                        view_search.setVisibility(View.INVISIBLE);
                        card_search.setVisibility(View.GONE);
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view_search.getWindowToken(), 0);
                        listView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animatorHide.setDuration(300);
                animatorHide.start();
            } else {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view_search.getWindowToken(), 0);
                view_search.startAnimation(fade_out);
                view_search.setVisibility(View.INVISIBLE);
                card_search.setVisibility(View.GONE);
            }
            edit_text_search.setText("");
            card_search.setEnabled(false);

            ((EditTaskActivity) getActivity()).onSearchClose();
        } else {
            // show
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils.createCircularReveal(card_search,
                        card_search.getWidth() - (int) convertDpToPixel(56, getContext()),
                        (int) convertDpToPixel(23, getContext()),
                        0,
                        (float) Math.hypot(card_search.getWidth(), card_search.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view_search.setVisibility(View.VISIBLE);
                        view_search.startAnimation(fade_in);
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                card_search.setVisibility(View.VISIBLE);
                if (card_search.getVisibility() == View.VISIBLE) {
                    animator.setDuration(300);
                    animator.start();
                    card_search.setEnabled(true);
                }
                fade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        listView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            } else {
                card_search.setVisibility(View.VISIBLE);
                card_search.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            edit_text_search.requestFocus();
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    class SearchSubjectAdapter extends ArrayAdapter<Subject> {
        Context mContext;
        public List<Subject> mLogs;

        public SearchSubjectAdapter(Context context, int textViewResourceId, List<Subject> logs) {
            super(context, textViewResourceId);
            mContext = context;
            mLogs = logs;
        }

        public void add(Subject log) {
            mLogs.add(log);
        }

        public void remove(Subject log) {
            mLogs.remove(log);
        }

        public int getCount() {
            return mLogs.size();
        }

        public Subject getItem(int position) {
            return mLogs.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.search_subject_list_item, null);
            Subject log = getItem(position);
            ((TextView) view.findViewById(R.id.textView)).setText(log.getName());
            return view;
        }

    }

}
