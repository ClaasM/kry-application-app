package com.example.kryservicechecker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * It's all pretty standard Android stuff, I didn't have time for anything fancy unfortunately, since learning Kotlin and Vert.x took most of the 10h
 */
public class MainActivity extends AppCompatActivity {

    final static String SERVER_URL = "http://10.0.2.2:8080/"; // This is the IP under which localhost can be reached from the emulator.
    final static String API_VERSION = "v1";
    final static String SERVICES_ENDPOINT = SERVER_URL + API_VERSION + "/api/services/";

    static RequestQueue queue;

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = findViewById(R.id.list);

        Button refreshBtn = (Button) findViewById(R.id.btn_refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServices();
            }
        });


        Button addBtn = (Button) findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter a URL. Don't forget the protocol.");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = input.getText().toString();
                        MainActivity.this.addService(url);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        getServices();
    }

    /**
     * Pretty self-explanatory. The url is added to the list of services to be monitored in the backend.
     * @param url
     */
    public void addService(String url) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("serviceURL", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    SERVICES_ENDPOINT,
                    payload,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Update the services. The object is also in the response, but this is good opportunity to update the rest as well.
                            // Plus I'm running out of time :)
                            getServices();
                            Toast.makeText(getApplicationContext(), "Service added", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all services and their status.
     */
    public void getServices() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, SERVICES_ENDPOINT, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Assign adapter to ListView
                        MainActivity.this.listview.setAdapter(new ServiceViewAdapter(MainActivity.this, response));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonArrayRequest);
    }

    /**
     * Deletes a service.
     * @param service
     */
    public void delete(JSONObject service) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.DELETE,
                    SERVICES_ENDPOINT + service.getString("id"),
                    service,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            getServices();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
