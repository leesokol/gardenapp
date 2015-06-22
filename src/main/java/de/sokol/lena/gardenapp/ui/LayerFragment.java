package de.sokol.lena.gardenapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.GardenListeners;

/**
 * Displays the Layers of the Garden and allows to change/select the layers.
 *
 * Created by Lena on 20.02.2015.
 */
public class LayerFragment extends ListFragment implements GardenListeners.GardenListener {


    Garden garden;
    OnLayerFragmentInteractionListener listener;

    public LayerFragment() {
        super();
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
        GardenListeners.getInstance().addGardenListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<GardenLayer> layers = new ArrayList<GardenLayer>();
        for (GardenLayer layer : garden.getLayers()) {
            layers.add(layer);
        }
        ArrayAdapter<GardenLayer> adapter = new LayerAdapter(this.getActivity(), layers);
        setListAdapter(adapter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layer_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setSelection(getListAdapter().getCount() - 1);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final GardenLayer layer = (GardenLayer) getListAdapter().getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.layer_long_click, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                listener.onDeleteLayer(layer);
                                break;
                            case 1:
                                listener.onMoveLayerUp(layer);
                                break;
                            case 2:
                                listener.onMoveLayerDown(layer);
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
    public void setSelection(int position) {
        if (position >= 0) {
            super.setSelection(position);
            listener.onSelectedLayer((GardenLayer) getListAdapter().getItem(position));
        }else {
            listener.onSelectedLayer(null);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.onSelectedLayer((GardenLayer) getListAdapter().getItem(position));
    }

    @Override
    public void informAddedObject() {
        //NO-OP
    }

    @Override
    public void informAddedLayer() {
        ArrayList<GardenLayer> layers = new ArrayList<GardenLayer>();
        for (GardenLayer layer : garden.getLayers()) {
            layers.add(layer);
        }
        ArrayAdapter<GardenLayer> adapter = new LayerAdapter(this.getActivity(), layers);
        setListAdapter(adapter);
    }

    @Override
    public void informChangedVisibility() {
        //No-Op
    }

    @Override
    public void informDeletedLayer() {
        ArrayList<GardenLayer> layers = new ArrayList<GardenLayer>();
        for (GardenLayer layer : garden.getLayers()) {
            layers.add(layer);
        }
        ArrayAdapter<GardenLayer> adapter = new LayerAdapter(this.getActivity(), layers);
        setListAdapter(adapter);
        if (layers.size() > 0) {
            setSelection(0);
        } else {
            listener.onSelectedLayer(null);
        }
    }

    @Override
    public void informMovedLayer() {
        ArrayList<GardenLayer> layers = new ArrayList<GardenLayer>();
        for (GardenLayer layer : garden.getLayers()) {
            layers.add(layer);
        }
        ArrayAdapter<GardenLayer> adapter = new LayerAdapter(this.getActivity(), layers);
        setListAdapter(adapter);
    }

    @Override
    public void informDeletedObject() {
        //NO-Op
    }

    @Override
    public void informMovedObject() {
        //NO-Op
    }

    @Override
    public void informChangedObject() {
        //NO-Op
    }

    @Override
    public void informAddedBoundary() {
        //No-Op
    }

    public interface OnLayerFragmentInteractionListener {
        public void onSelectedLayer(GardenLayer layer);

        public void onDeleteLayer(GardenLayer layer);

        public void onMoveLayerUp(GardenLayer layer);

        public void onMoveLayerDown(GardenLayer layer);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnLayerFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLayerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        GardenListeners.getInstance().removeGardenListener(this);
    }

    private class LayerAdapter extends ArrayAdapter<GardenLayer> {

        public LayerAdapter(Context context, List<GardenLayer> objects) {
            super(context, R.layout.layer_display_item, R.id.layer_textview, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final GardenLayer layer = (GardenLayer) this.getItem(position);

            CheckBox checkBox;
            TextView textView;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(this.getContext());
                convertView = inflater.inflate(R.layout.layer_display_item, null);
            }

            textView = (TextView) convertView.findViewById(R.id.layer_textview);
            checkBox = (CheckBox) convertView.findViewById(R.id.visible_checkbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    layer.setVisible(cb.isChecked());
                    Log.d("CHECKBOX", "setting visible " + cb.isChecked());
                }
            });

            checkBox.setChecked(layer.isVisible());
            textView.setText(layer.getName());

            return convertView;
        }
    }
}
