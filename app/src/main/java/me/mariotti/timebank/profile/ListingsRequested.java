package me.mariotti.timebank.profile;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import me.mariotti.timebank.ListingDetailActivity;
import me.mariotti.timebank.R;
import me.mariotti.timebank.classes.ListingAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link ListingsRequested#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingsRequested extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private ListingAdapter mRequestedListingAdapter;
    private ListView mListView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment ListingsRequested.
     */
    public static ListingsRequested newInstance(String param1) {
        ListingsRequested fragment = new ListingsRequested();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ListingsRequested() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listings_requested, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            mListView = (ListView) getView().findViewById(R.id.requested_listings_list_view);
            mRequestedListingAdapter = ((ProfileActivity) getActivity()).mRequestedListingsAdapter;
            mListView.setAdapter(mRequestedListingAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(parent.getContext(), ListingDetailActivity.class);
                    intent.putExtra(ListingDetailActivity.LISTING_OBJECT, mRequestedListingAdapter.getItem(position));
                    startActivity(intent);
                }
            });
        }
    }


}
