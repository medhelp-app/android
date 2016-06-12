package com.medhelp.medhelp.fragments.patient;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.model.FeedItem;
import com.medhelp.medhelp.views.adapters.FeedListAdapter;

import java.util.ArrayList;
import java.util.List;


public class PatientSocialFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    public PatientSocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_social, container, false);

        listView = (ListView) view.findViewById(R.id.list);

        feedItems = new ArrayList<>();

        FeedItem feedItem = new FeedItem("1", "Carlos Rodrigo", "1", "2016-21-12", "Quais as horas que são mais indicadas para tomar banho de sol sem prejudicar a pele?", "question");
        FeedItem feedItem1 = new FeedItem("2", "Luiz Pereira", "2", "2016-21-12", "", "question");
        FeedItem feedItem2 = new FeedItem("3", "Pedro Silva", "3", "2016-21-12", "Medico fazendo uma postagem", "post");
        feedItems.add(feedItem);
        feedItems.add(feedItem1);
        feedItems.add(feedItem2);

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
