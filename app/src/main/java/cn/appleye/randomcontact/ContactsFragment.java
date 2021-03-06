package cn.appleye.randomcontact;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cn.appleye.randomcontact.common.ListAdapter;

/**
 * Created by iSpace on 2016/3/19.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mSearchView;
    private ListView mListView;
    private ListAdapter mAdapter;
    private View mNoContactsView;
    private View mContainerView;

    private TextView mListHeaderView;

    private String mLastQueryString = "";
    private static final int LOADER_ID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflateView(inflater, container);
    }

    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.content_random, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();

        mSearchView = (EditText)rootView.findViewById(R.id.search_view);
        mSearchView.setBackgroundColor(0xffffffff);

        mAdapter = new ListAdapter(getActivity());
        mListView = (ListView)rootView.findViewById(R.id.list_view);
        mNoContactsView = rootView.findViewById(R.id.no_contacts_view);
        mContainerView = rootView.findViewById(R.id.container_view);
        mListView.setEmptyView(mNoContactsView);

        setupSearchView();
        setupListView();

        startLoading();
    }

    private void setupSearchView() {
        mSearchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                startQuery();
            }
        });
    }

    private void setupListView() {
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = mListView.getHeaderViewsCount();
                int realPosition = position - headerViewsCount;
                if (realPosition < 0) {
                    return;
                }

                Uri uri = (Uri)mAdapter.getItem(realPosition);

                if (uri != null) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        addHeaderView();
    }

    private void addHeaderView() {
        if (mListHeaderView == null) {
            mListHeaderView = new TextView(getActivity());
            mListHeaderView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mListHeaderView.setPadding((int)getResources().getDimension(R.dimen.list_header_padding_left),
                    (int)getResources().getDimension(R.dimen.list_header_padding_top),
                    0, (int)getResources().getDimension(R.dimen.list_header_padding_top));
            mListHeaderView.setBackgroundColor(0xffffffff);
            mListHeaderView.setTextColor(0xFFFFB200);

            mListView.addHeaderView(mListHeaderView);
        }
    }

    private void startLoading() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void startQuery() {
        String queryString = mSearchView.getText().toString();
        mAdapter.setQueryString(queryString);

        if ((mLastQueryString==null && queryString == mLastQueryString) || queryString.equals(mLastQueryString)) {

        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        mLastQueryString = queryString;
    }

    private CursorLoader createCursorLoader() {
        return new CursorLoader(getActivity(), null, null, null, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        CursorLoader cursorLoader = createCursorLoader();
        mAdapter.configureLoader(cursorLoader);

        return cursorLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mAdapter.changeCursor(data);

        if (!mAdapter.isSearchMode() && (data ==null || data.getCount() == 0)) {
            mContainerView.setVisibility(View.GONE);
        } else {
            mContainerView.setVisibility(View.VISIBLE);
        }

        if (mListHeaderView != null) {
            if (data ==null || data.getCount() == 0) {
                mListHeaderView.setVisibility(View.GONE);
            } else {
                String numberOfContacts = String.format(getString(R.string.number_of_contacts), data.getCount());
                mListHeaderView.setText(numberOfContacts);
                mListHeaderView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
