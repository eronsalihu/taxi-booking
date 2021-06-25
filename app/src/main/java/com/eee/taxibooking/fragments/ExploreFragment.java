package com.eee.taxibooking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.eee.taxibooking.R;
import com.eee.taxibooking.adapters.TaxiAdapter;
import com.eee.taxibooking.models.Taxi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private  static String JSON_URL = "https://raw.githubusercontent.com/eronsalihu/eronsalihu.github.io/main/taxi";
    private RecyclerView recyclerView;
    private List<Taxi> taxiList;
    TaxiAdapter adapter;


    public ExploreFragment() {
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
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        taxiList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recycleViewer);

        extract();


//        GetData getData = new GetData();
//        getData.execute();

        return v;
    }

    private void extract(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Taxi taxi = new Taxi();
                        taxi.setPhoto(jsonObject.getString("photo"));
                        taxi.setName(jsonObject.getString("name"));
                        taxi.setNumber1(jsonObject.getString("number_1"));
                        taxi.setNumber2(jsonObject.getString("number_2"));
                        taxi.setNoCallPayment(jsonObject.getString("noCallPayment"));
                        taxiList.add(taxi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ;
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                adapter = new TaxiAdapter(getActivity().getApplicationContext(),taxiList);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ResponseError",error.getMessage());
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

}