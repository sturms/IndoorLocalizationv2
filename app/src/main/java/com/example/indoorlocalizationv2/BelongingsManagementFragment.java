package com.example.indoorlocalizationv2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.indoorlocalizationv2.models.BelongingsOperationType;

public class BelongingsManagementFragment extends Fragment implements View.OnClickListener {

    /**
     * The type operation which will be performed with belongings item.
     * Fore example- ADD, EDIT (BelongingsOperationType)
     */
    private String _operationType;
    private String _beaconName;
    private String _searchCriteria;
    private int _itemId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            _operationType = getArguments().getString("operationType");
            _beaconName = getArguments().getString("beaconName");
            _searchCriteria = getArguments().getString("searchCriteria");
            _itemId = getArguments().getInt("itemId");
        }

        return inflater.inflate(R.layout.fragment_belongings_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
            Button deleteButton = getView().findViewById(R.id.btn_belongings_manage_item_delete);
            deleteButton.setVisibility(View.GONE);
        }
        else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
            // The delete button will be visible only in edit mode
            Button btn_delete = getView().findViewById(R.id.btn_belongings_manage_item_delete);
            btn_delete.setOnClickListener(this);

            // TODO: get the data from database by belongings '_itemId'
        }

        Button btn_save = getView().findViewById(R.id.btn_belongings_manage_item_save);
        Button btn_back = getView().findViewById(R.id.btn_belongings_manage_item_back);
        btn_save.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View btnView) {
        switch (btnView.getId()) {
            case R.id.btn_belongings_manage_item_save:
                if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
                    // TODO: add new record

                    // Show success message and navigate to home
                    Toast.makeText(getActivity(), "The item was added successfully", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();
                }
                else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
                    // TODO: update the data

                    // Show success message
                    Toast.makeText(getActivity(), "The item was updated successfully", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_belongings_manage_item_delete:
                break;
            case R.id.btn_belongings_manage_item_back:
                if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
                    // Navigate back to home
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();
                }
                else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("searchCriteria", _searchCriteria);

                    // If the dropdown's value was updated here, we leave the old one
                    bundle.putString("beaconName", _beaconName);

                    SearchBelongingsFragment belongingsSearchFragm = new SearchBelongingsFragment();
                    belongingsSearchFragm.setArguments(bundle);

                    // Navigate back to search
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, belongingsSearchFragm).commit();
                }
                break;
        }
    }
}
