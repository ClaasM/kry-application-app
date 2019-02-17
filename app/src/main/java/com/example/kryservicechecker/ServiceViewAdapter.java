package com.example.kryservicechecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ServiceViewAdapter extends BaseAdapter {

    MainActivity parentActivity;
    JSONArray data;
    private static LayoutInflater inflater = null;

    /**
     * This is the ArrayAdapter for the list of services.
     * @param parent
     * @param data
     */
    public ServiceViewAdapter(MainActivity parent, JSONArray data) {
        this.parentActivity = parent;
        this.data = data;
        inflater = (LayoutInflater) parent.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

        try {
            final JSONObject obj = (JSONObject) data.get(position);
            TextView status = (TextView) vi.findViewById(R.id.status);
            status.setText((String) obj.get("status"));
            TextView url = (TextView) vi.findViewById(R.id.url);
            url.setText((String) obj.get("serviceURL"));
            Button delete = (Button) vi.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentActivity.delete(obj);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return vi;
    }
}