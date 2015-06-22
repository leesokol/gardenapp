package de.sokol.lena.gardenapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.model.GardenFacade;
import de.sokol.lena.gardenapp.model.GardenListeners;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * Displays all vectorobject of the selected layer in a list
 *
 * Created by Lena on 22.02.2015.
 */
public class ObjectListFragment extends ListFragment implements GardenListeners.GardenListener {

    //=Member varibles names=
    private static final String GARDENLAYER = "GardenLayerName";
    //=Member variables=
    protected String gardenLayer;
    private GardenFacade gardenFacade;
    private OnObjectListFragmentInteractionListener listener;

    public ObjectListFragment() {
        super();
    }

    public static ObjectListFragment newInstance(String gardenLayer) {
        ObjectListFragment fragment = new ObjectListFragment();
        Bundle args = new Bundle();
        args.putString(GARDENLAYER, gardenLayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.gardenLayer = getArguments().getString(GARDENLAYER);
        }


        updateObjectList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_list, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final VectorObject object = (VectorObject) getListAdapter().getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.object_long_click, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                listener.onDeletedObject(object);
                                break;
                            case 1:
                                listener.onObjectMoveUp(object);
                                break;
                            case 2:
                                listener.onObjectMoveDown(object);
                                break;
                            case 3:
                                if (!(object instanceof TopologicalNode)) {
                                    listener.onChangeObject(object);
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        VectorObject obj = (VectorObject) getListAdapter().getItem(position);
        listener.onSelectedObject(obj);
    }

    public void setGarden(GardenFacade gardenFacade) {
        this.gardenFacade = gardenFacade;
        GardenListeners.getInstance().addGardenListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnObjectListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnObjectListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GardenListeners.getInstance().removeGardenListener(this);
    }

    private void updateObjectList() {
        ArrayList<VectorObject> objects = new ArrayList<>();
        VectorLayer layer = gardenFacade.getVectorLayer(gardenLayer);
        if (layer != null) {
            for (VectorObject obj : layer.getVectorObjects()) {
                objects.add(obj);
            }
        }
        ArrayAdapter<VectorObject> adapter = new ArrayAdapter<VectorObject>(this.getActivity(), android.R.layout.simple_list_item_1, objects);
        setListAdapter(adapter);
    }

    //=====Garden:GardenListener======
    @Override
    public void informAddedObject() {
        updateObjectList();
    }

    @Override
    public void informAddedLayer() {
        //No-Op
    }

    @Override
    public void informChangedVisibility() {
        //No-Op
    }

    @Override
    public void informDeletedLayer() {
        //No-Op
    }

    @Override
    public void informMovedLayer() {
        //No-Op
    }

    @Override
    public void informDeletedObject() {
        updateObjectList();
    }

    @Override
    public void informMovedObject() {
        updateObjectList();
    }

    @Override
    public void informChangedObject() {
        updateObjectList();
    }

    @Override
    public void informAddedBoundary() {
        //No-Op
    }

    //===Listener===
    public interface OnObjectListFragmentInteractionListener {
        public void onSelectedObject(VectorObject object);

        public void onDeletedObject(VectorObject object);

        public void onObjectMoveUp(VectorObject object);

        public void onObjectMoveDown(VectorObject object);

        public void onChangeObject(VectorObject object);
    }
}
