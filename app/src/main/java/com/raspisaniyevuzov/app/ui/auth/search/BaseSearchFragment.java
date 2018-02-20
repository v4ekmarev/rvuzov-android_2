package com.raspisaniyevuzov.app.ui.auth.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.dto.SuggestDto;
import com.raspisaniyevuzov.app.api.messages.suggest.FacultySuggestRequestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.GroupSuggestRequestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.UniversitySuggestRequestMessage;
import com.raspisaniyevuzov.app.event.ErrorEvent;
import com.raspisaniyevuzov.app.event.FacultySuggestResponseReceivedEvent;
import com.raspisaniyevuzov.app.event.GroupSuggestResultReceivedEvent;
import com.raspisaniyevuzov.app.event.UniversitySuggestResponseReceivedEvent;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.auth.AddNewDataActivity;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by SAPOZHKOV on 13.10.2015.
 */
public class BaseSearchFragment extends BaseFragment {

    public interface Searchable {

        void onUniversitySelect(SuggestDto suggestDto);

        void onFacultySelect(SuggestDto suggestDto);

        void onGroupSelect(SuggestDto suggestDto);

        void onSearchClose(boolean isSelected);
    }

    public static final String EXTRA_SEARCH_TYPE = "extra_search_type";
    public static final String EXTRA_UNIVERSITY = "extra_university";
    public static final String EXTRA_ENTITY_ID = "extra_entity_id";
    private View line_divider;
    private RelativeLayout view_search;
    private CardView card_search;
    private ImageView clearSearch;
    private EditText edit_text_search;
    private ListView listView;
    private ArrayAdapter<SuggestDto> searchAdapter;
    private Button addNewDataButton;
    private TextView tvNoResults;
    private LinearLayout llResults;
    private ProgressBar progress;
    /**
     * university, faculty, group
     */
    private String mSearchType, mEntityId;

    public enum SearchType {

        UNIVERSITY("university"), FACULTY("faculty"), GROUP("group");

        public final String type;

        SearchType(String type) {
            this.type = type;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        view_search = (RelativeLayout) view.findViewById(R.id.view_search);

        addNewDataButton = (Button) view.findViewById(R.id.add_new_data_button);

        progress = (ProgressBar) view.findViewById(R.id.marker_progress);

        mSearchType = getArguments().getString(EXTRA_SEARCH_TYPE);

        mEntityId = getArguments().getString(EXTRA_ENTITY_ID);

        final String mUniversity = getArguments().getString(EXTRA_UNIVERSITY);

        String addNewDataBtnText = "";
        String edtHint = "";

        if (SearchType.UNIVERSITY.type.equals(mSearchType)) {
            addNewDataBtnText = getString(R.string.add_university);
            edtHint = getString(R.string.university_search_hint);
        } else if (SearchType.FACULTY.type.equals(mSearchType)) {
            addNewDataBtnText = getString(R.string.add_faculty);
            edtHint = getString(R.string.faculty_search_hint);
        } else if (SearchType.GROUP.type.equals(mSearchType)) {
            addNewDataBtnText = getString(R.string.add_group);
            edtHint = getString(R.string.group_search_hint);
        }

        edit_text_search = (EditText) view.findViewById(R.id.edit_text_search);
        edit_text_search.setHint(edtHint);
        addNewDataButton.setText(addNewDataBtnText);
        addNewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleToolBar(false);
                Intent intent = new Intent(getActivity(), AddNewDataActivity.class);
                intent.putExtra(EXTRA_SEARCH_TYPE, mSearchType);
                intent.putExtra(EXTRA_UNIVERSITY, mUniversity);
                startActivity(intent);
            }
        });

        tvNoResults = (TextView) view.findViewById(R.id.tvNoResults);
        llResults = (LinearLayout) view.findViewById(R.id.llResults);

        line_divider = view.findViewById(R.id.line_divider);

        card_search = (CardView) view.findViewById(R.id.card_search);
        ImageView image_search_back = (ImageView) view.findViewById(R.id.image_search_back);
        clearSearch = (ImageView) view.findViewById(R.id.clearSearch);
        listView = (ListView) view.findViewById(R.id.listView);

        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleToolBar(false);
            }
        });
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = edit_text_search.getText().toString().trim();
                    if (edit_text_search.getText().toString().trim().length() > 0) {
                        search(query);
                    }
                    return true;
                }
                return false;
            }
        });

        searchAdapter = new SearchAdapter(getActivity(), 0, new ArrayList<SuggestDto>());
        listView.setAdapter(searchAdapter);

        EventBus.getDefault().register(this);

        search("");

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitiateSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void InitiateSearch() {
        IsAdapterEmpty("");
        handleToolBar(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (SearchType.UNIVERSITY.type.equals(mSearchType)) {
                    ((Searchable) (getTargetFragment() != null ? getTargetFragment() : getContext())).
                    onUniversitySelect(searchAdapter.getItem(position));
                } else if (SearchType.FACULTY.type.equals(mSearchType)) {
                    ((Searchable) (getTargetFragment() != null ? getTargetFragment() : getContext())).onFacultySelect(searchAdapter.getItem(position));
                } else if (SearchType.GROUP.type.equals(mSearchType)) {
                    ((Searchable) (getTargetFragment() != null ? getTargetFragment() : getContext())).onGroupSelect(searchAdapter.getItem(position));
                }

                handleToolBar(true);
            }
        });
        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = edit_text_search.getText().toString();
                if (query.length() == 0) {
                    clearSearch.setVisibility(View.GONE);
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty("");
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                }
                search(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_search.getText().toString().length() > 0) {
                    edit_text_search.setText("");
                    listView.setVisibility(View.VISIBLE);
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty("");
                    search("");
                }
            }
        });
    }

    private void search(String query) {
        progress.setVisibility(View.VISIBLE);
        if (SearchType.UNIVERSITY.type.equals(mSearchType))
            Api.sendMessage(new UniversitySuggestRequestMessage(query));
        else if (SearchType.FACULTY.type.equals(mSearchType))
            Api.sendMessage(new FacultySuggestRequestMessage(query, mEntityId));
        else if (SearchType.GROUP.type.equals(mSearchType))
            Api.sendMessage(new GroupSuggestRequestMessage(query, mEntityId));

        llResults.setVisibility(View.INVISIBLE);
        searchAdapter = new SearchAdapter(getContext(), 0, new ArrayList<SuggestDto>());
        listView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
        line_divider.setVisibility(View.VISIBLE);
    }

    private void updateListWithSuggestResult(final List<SuggestDto> suggestList, final String lastQuery) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (suggestList.size() == 0 && lastQuery.length() > 0)
                    addNewDataButton.setVisibility(View.VISIBLE);
                else addNewDataButton.setVisibility(View.GONE);
                updateList(suggestList, lastQuery);
            }
        });

    }

    public void onEvent(final UniversitySuggestResponseReceivedEvent event) {
        if (SearchType.UNIVERSITY.type.equals(mSearchType))
            updateListWithSuggestResult(event.suggestList, event.query);
    }

    public void onEvent(final GroupSuggestResultReceivedEvent event) {
        if (SearchType.GROUP.type.equals(mSearchType))
            updateListWithSuggestResult(event.suggestList, event.query);
    }

    public void onEvent(final FacultySuggestResponseReceivedEvent event) {
        if (SearchType.FACULTY.type.equals(mSearchType))
            updateListWithSuggestResult(event.suggestList, event.query);
    }

    public void onEvent(final ErrorEvent event) {
        updateListWithSuggestResult(new ArrayList<SuggestDto>(), "");
    }

    private void updateList(final List<SuggestDto> list, final String query) {
        if (getActivity() != null) {
            progress.setVisibility(View.GONE);
            searchAdapter = new SearchAdapter(getActivity(), 0, list);
            listView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
            IsAdapterEmpty(query);
        }
    }

    private void IsAdapterEmpty(String query) {
        if (searchAdapter.getCount() == 0) {
            if (query.length() > 0) {
                line_divider.setVisibility(View.VISIBLE);
                llResults.setVisibility(View.VISIBLE);
                tvNoResults.setText(query);
                addNewDataButton.setVisibility(View.VISIBLE);
            } else {
                line_divider.setVisibility(View.GONE);
                llResults.setVisibility(View.GONE);
                addNewDataButton.setVisibility(View.GONE);
            }
        } else {
            line_divider.setVisibility(View.VISIBLE);
            llResults.setVisibility(View.GONE);
        }
    }

    public void handleToolBar(boolean isSelected) {
        if (getContext() != null) {
            final Animation fade_in = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            final Animation fade_out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
            if (card_search.getVisibility() == View.VISIBLE) {
                // hide
                final SupportAnimator animatorHide = ViewAnimationUtils.createCircularReveal(card_search,
                        card_search.getWidth() - (int) convertDpToPixel(56, getContext()),
                        (int) convertDpToPixel(23, getContext()),
                        (float) Math.hypot(card_search.getWidth(), card_search.getHeight()),
                        0);
                animatorHide.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        view_search.startAnimation(fade_out);
                        view_search.setVisibility(View.INVISIBLE);
                        card_search.setVisibility(View.GONE);
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view_search.getWindowToken(), 0);
                        listView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animatorHide.setDuration(300);
                animatorHide.start();
                edit_text_search.setText("");
                card_search.setEnabled(false);

                addNewDataButton.setVisibility(View.GONE);
                ((Searchable) (getTargetFragment() != null ? getTargetFragment() : getContext())).onSearchClose(isSelected);
            } else {
                // show
                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(card_search, card_search.getWidth() - (int) convertDpToPixel(56, getContext()), (int) convertDpToPixel(23, getContext()), 0, (float) Math.hypot(card_search.getWidth(), card_search.getHeight()));
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        view_search.setVisibility(View.VISIBLE);
                        view_search.startAnimation(fade_in);
                        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

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
                edit_text_search.requestFocus();
            }
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    class SearchAdapter extends ArrayAdapter<SuggestDto> {
        Context mContext;
        public List<SuggestDto> mSuggestDtos;

        public SearchAdapter(Context context, int textViewResourceId, List<SuggestDto> suggestDtos) {
            super(context, textViewResourceId);
            mContext = context;
            mSuggestDtos = suggestDtos;
        }

        public void add(SuggestDto dto) {
            mSuggestDtos.add(dto);
        }

        public void remove(SuggestDto dto) {
            mSuggestDtos.remove(dto);
        }

        public int getCount() {
            return mSuggestDtos.size();
        }

        public SuggestDto getItem(int position) {
            return mSuggestDtos.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SearchRow view = (SearchRow) convertView;
            if (view == null)
                view = new SearchRow(mContext, mSearchType);
            SuggestDto dto = getItem(position);
            view.setLog(dto);
            return view;
        }

    }

}
