package com.example.sangeeta.hw8;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FirstFragment extends Fragment {

    public FirstFragment(){    }

    public static FirstFragment newInstance(int fragmentNumber){

        FirstFragment myfragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("FrameNumber", fragmentNumber);
        myfragment.setArguments(args);
        return myfragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;
        int option = getArguments().getInt("FrameNumber");


        final OnButtonSelected mListener;
        try{
            mListener = (OnButtonSelected) getContext();
        }catch (ClassCastException e){
            throw new ClassCastException("Hosting activity of fragment forgot to implement OnButtonSelected");
        }

        switch (option){

            case 1:
                rootView = inflater.inflate(R.layout.activity_recyclerview, container, false);

                break;

            case 2:
                rootView = inflater.inflate(R.layout.aboutmefrag, container, false);
                break;
        }
        return rootView;
    }

    public interface OnButtonSelected {
        public void onClickButtonSelected(int param);
    }
}
