package com.app.afrifarm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SelectionSheetFragment extends BottomSheetDialogFragment {

    public SelectionSheetFragment() {
        // Required empty public constructor
    }

    public static SelectionSheetFragment newInstance() {
        SelectionSheetFragment fragment = new SelectionSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selection_sheet, container, false);
    }
}