package de.sokol.lena.gardenapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.model.GardenListeners;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.VectorObject;
import de.sokol.lena.gardenapp.ui.Wrapper.IVectorobjectWrapper;

/**
 * Shows the detail of a vector object an allows for modification of the object
 *
 * Created by Lena on 23.02.2015.
 */
public class ObjectDetailFragment extends ListFragment implements GardenListeners.GardenListener {

    //Listener
    private OnObjectDetailFragmentListener listener;
    private IVectorobjectWrapper wrapper;
    private VectorLayer layer;

    public ObjectDetailFragment() {
    }

    public static ObjectDetailFragment newInstance() {
        ObjectDetailFragment fragment = new ObjectDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setObject(VectorObject object) {
        this.wrapper = WrapperFactory.createWrapper(object);
    }

    public void setLayer(VectorLayer layer) {
        this.layer = layer;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.remove_object_details, menu);
        if (wrapper.hasTimeLine()) {
            inflater.inflate(R.menu.add_time_value, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addTimeValue:
                if (wrapper.hasRemoteSensor()) {
                    wrapper.callRemoteSensor();
                    return true;
                }
                createAddTimeValueDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAddTimeValueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.object_detail, null);
        ListView listview = (ListView) view.findViewById(android.R.id.list);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (Attribute att : wrapper.getTimeValueAttributes()) {
            attributes.add(att);
        }
        listview.setAdapter(
                new ObjectViewAdapter(getActivity(), attributes));
        builder.setView(view);
        builder.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!wrapper.saveTimeValue()) {
                    Toast.makeText(getActivity(), "Could not save an empty value", Toast.LENGTH_SHORT).show();
                }
                updateObjectsDetails();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

    }

    private void updateObjectsDetails() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (Attribute att : wrapper.getAttributes()) {
            attributes.add(att);
        }
        if (wrapper.hasTimeLine()) {
            attributes.add(wrapper.getLastTimeValueAsString());
        }
        ArrayAdapter<Attribute> adapter = new ObjectViewAdapter(this.getActivity(), attributes);
        setListAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Retrieving the values for the member variables with the Argument names
        }
        updateObjectsDetails();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_detail, container, false);
        return view;
    }

    @Override
    public void informAddedObject() {

    }

    @Override
    public void informAddedLayer() {

    }

    @Override
    public void informChangedVisibility() {

    }

    @Override
    public void informDeletedLayer() {

    }

    @Override
    public void informMovedLayer() {

    }

    @Override
    public void informDeletedObject() {

    }

    @Override
    public void informMovedObject() {

    }

    @Override
    public void informChangedObject() {
        updateObjectsDetails();
    }

    @Override
    public void informAddedBoundary() {

    }

    //============
    //=Listener=

    public interface OnObjectDetailFragmentListener {

    }

    public class ObjectViewAdapter extends ArrayAdapter<Attribute> {

        int[] layoutvalues = {
                android.R.layout.simple_list_item_1,
                R.layout.string_text_edit_item,
                R.layout.spinner_select_item,
                R.layout.autocompletetextview_item};


        public ObjectViewAdapter(Context context, List<Attribute> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }

        @Override
        public int getItemViewType(int position) {
            final Attribute att = (Attribute) this.getItem(position);
            if (att.isFinal()) {
                return 0;//android.R.layout.simple_list_item_1;
            } else if (att.getAttributType() == Attribute.STRING_TYPE ||
                    att.getAttributType() == Attribute.FLOAT_TYPE ||
                    att.getAttributType() == Attribute.DATE ||
                    att.getAttributType() == Attribute.INT_TYPE) {
                return 1;//R.layout.string_text_edit_item;
            } else if (att.getAttributType() == Attribute.NODE_TYPE || att.getAttributType() == Attribute.GROUNDTYPE) {
                return 2;//R.layout.spinner_select_item
            } else if (att.getAttributType() == Attribute.PLANTTYPE_TYPE) {
                return 3;//R.layout.autocompletetextview_item
            }
            return 0;//android.R.layout.simple_list_item_1;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Attribute att = (Attribute) this.getItem(position);

            int type = layoutvalues[getItemViewType(position)];
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(this.getContext());
                convertView = inflater.inflate(type, null);
            }

            switch (type) {
                case android.R.layout.simple_list_item_1:
                    TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
                    textView.setText(att.toString());
                    break;
                case R.layout.string_text_edit_item:
                    TextView textView1 = (TextView) convertView.findViewById(R.id.attribute_text);
                    textView1.setText(att.getAttributeName());
                    EditText editText = (EditText) convertView.findViewById(R.id.attribute_text_edit);
                    switch (att.getAttributType()) {
                        case Attribute.STRING_TYPE:
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            break;
                        case Attribute.FLOAT_TYPE:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            break;
                        case Attribute.INT_TYPE:
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        case Attribute.DATE:
                            editText.setFocusable(false);
                            editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Date date = (Date) att.getValue();
                                    createDateTimePickerDialog(date, att);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                    if (att.getValue() != null) {
                        editText.setText(att.getValue().toString());
                    }
                    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            String changedText = v.getText().toString();
                            att.setValue(changedText);
                            wrapper.changeAttribute(att);
                            return false;
                        }
                    });
                    break;
                case R.layout.spinner_select_item:
                    TextView textView2 = (TextView) convertView.findViewById(R.id.attribute_text);
                    textView2.setText(att.getAttributeName());
                    Spinner spinner = (Spinner) convertView.findViewById(R.id.attribut_spinner);
                    if (att.getAttributType() == Attribute.NODE_TYPE) {
                        initializeNodeSpinner(spinner, att);
                    } else if (att.getAttributType() == Attribute.GROUNDTYPE) {
                        initializeGroundTypeSpinner(spinner, att);
                    }
                    break;
                case R.layout.autocompletetextview_item:
                    TextView textView3 = (TextView) convertView.findViewById(R.id.attribute_text);
                    textView3.setText(att.getAttributeName());
                    AutoCompleteTextView textView4 = (AutoCompleteTextView) convertView.findViewById(R.id.attribute_autocomplete);
                    if (att.getValue() != null) {
                        textView4.setText(att.getValue().toString());
                    }
                    initializePlanttypeSpinner(textView4, att);
                    break;
            }
            return convertView;
        }

        private void initializeGroundTypeSpinner(Spinner spinner, final Attribute att) {
            EnumSet set = EnumSet.allOf(Ground.GroundType.class);
            ArrayList<Ground.GroundType> groundTypes = new ArrayList<Ground.GroundType>(set);
            ArrayAdapter<Ground.GroundType> adapter = new ArrayAdapter<Ground.GroundType>(getContext(), android.R.layout.simple_spinner_item, groundTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (att.getValue() != null && att.getValue() instanceof Ground.GroundType) {
                Ground.GroundType groundType = (Ground.GroundType) att.getValue();
                spinner.setSelection(adapter.getPosition(groundType));
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Ground.GroundType groundType = (Ground.GroundType) parent.getItemAtPosition(position);
                    att.setValue(groundType);
                    wrapper.changeAttribute(att);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //No-Op
                }
            });
        }

        private void initializeNodeSpinner(Spinner spinner, final Attribute att) {
            ArrayList<TopologicalNode> nodes = new ArrayList<>();
            for (VectorObject object : layer.getVectorObjects()) {
                if (object instanceof TopologicalNode) {
                    nodes.add((TopologicalNode) object);
                }
            }
            ArrayAdapter<TopologicalNode> adapter = new ArrayAdapter<TopologicalNode>(getContext(), android.R.layout.simple_spinner_item, nodes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (att.getValue() != null && att.getValue() instanceof TopologicalNode) {
                TopologicalNode selectedNode = (TopologicalNode) att.getValue();
                spinner.setSelection(adapter.getPosition(selectedNode));
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TopologicalNode node = (TopologicalNode) parent.getItemAtPosition(position);
                    att.setValue(node);
                    wrapper.changeAttribute(att);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //No-Op
                }
            });
        }

        private void initializePlanttypeSpinner(AutoCompleteTextView textView, final Attribute att) {
            String[] planttypes = getContext().getResources().getStringArray(R.array.plant_types);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, planttypes);
            textView.setAdapter(adapter);
            textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String planttype = (String) parent.getItemAtPosition(position);
                    att.setValue(planttype);
                    wrapper.changeAttribute(att);
                }
            });
            textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String planttype = (String) v.getText().toString();
                    att.setValue(planttype);
                    wrapper.changeAttribute(att);

                    return false;
                }
            });
        }

        private void createDateTimePickerDialog(final Date date, final Attribute att) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            View view = inflater.inflate(R.layout.datetimepicker, null);
            final DatePicker datepicker = (DatePicker) view.findViewById(R.id.datepicker);
            final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                datepicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                datepicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            }
            builder.setView(view);
            builder.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentHour());
                    att.setValue(calendar.getTime());
                    wrapper.changeAttribute(att);
                }
            });
            builder.create().show();
        }
    }

}
