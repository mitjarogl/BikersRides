package com.moods.bikersrides.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.moods.bikersrides.R;
import com.moods.bikersrides.common.AppController;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.utils.StopWatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class PlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
    public static final String REQUEST_AUTOCOMPLETE = "AUTOCOMPLETE";
    public static final String JSON_ARRAY_PREDICTIONS = "predictions";
    public static final String JSON_OBJ_DESCRIPTION = "description";
    private ArrayList<String> mPlaces;
    private LayoutInflater mLayoutInflater;
    private String mCurrentSequence;
    private boolean mLoading = false;
    private Context mContext;

    public PlacesAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }

    @Override
    public String getItem(int position) {
        if (!mPlaces.isEmpty())
            return mPlaces.get(position);
        else return "";
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        rowView = mLayoutInflater.inflate(R.layout.autocomplete_item, parent, false);
        holder.placeText = (TextView) rowView.findViewById(R.id.textView_autocomplete);
        if (!mPlaces.isEmpty()) {
            String place = mPlaces.get(position);
            String markedSequence = markCurrentSequence(place);
            holder.placeText.setText(Html.fromHtml(markedSequence));
        }

        return rowView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                StopWatch sw = new StopWatch();
                sw.start();
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    AppController.getInstance().cancelPendingRequests(REQUEST_AUTOCOMPLETE);
                    StopWatch s = new StopWatch();
                    s.start();
                    loadPlaceSuggestions(charSequence.toString());
                    s.stop();
                    Log.i(getClass().toString(), "RESPONSE-TIME-ALL: " + String.valueOf(s.getElapsedTime()));
                    s.stop();
                    while (mLoading) {
                    }

                    filterResults.values = mPlaces;
                    filterResults.count = mPlaces.size();
                    mCurrentSequence = charSequence.toString();
                }
                sw.stop();
                Log.i(getClass().toString(), "RESPONSE-TIME-FILTER: " + String.valueOf(sw.getElapsedTime()));
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private void loadPlaceSuggestions(String input) {
        mPlaces = new ArrayList<String>();

        final AppController appController = AppController.getInstance();

        String url = BaseGlobals.PLACES_BASE_URL;
        url += "?input=";

        try {
            url += URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += "&language=" + BaseGlobals.PLACES_LANGUAGE_SL;
        url += "&key=" + BaseGlobals.PLACES_API_KEY;
        //   Log.d("QUERY_AUTOCOMPLETE", url);
        mLoading = true;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                StopWatch sw = new StopWatch();
                sw.start();
                parsePlacesResponse(response);
                sw.stop();
                Log.i(getClass().toString(), "RESPONSE-TIME-PARSE: " + String.valueOf(sw.getElapsedTime()));
                Log.d("RESPONSE_AUTOCOMPLETE", response);
                mLoading = false;

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String errorStr;
                NetworkResponse response = error.networkResponse;
                if (response == null) {
                    errorStr = error.toString();
                } else {
                    errorStr = new String(response.data);
                }
                Log.d("ERROR", errorStr);
                mLoading = false;
            }

        });

        appController.addToRequestQueue(strReq, REQUEST_AUTOCOMPLETE);

    }

    private String markCurrentSequence(String place) {
        int idx = place.toLowerCase().indexOf(mCurrentSequence.toLowerCase());

        StringBuilder sb = new StringBuilder();
        sb.append(place);
        if (idx >= 0) {
            sb.insert(idx, "<b>");
            sb.insert(idx + mCurrentSequence.length() + 3, "</b>");
        }
        return sb.toString();
    }

    private void parsePlacesResponse(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jArray = jsonObject.getJSONArray(JSON_ARRAY_PREDICTIONS);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject actor = jArray.getJSONObject(i);
                String name = actor.getString(JSON_OBJ_DESCRIPTION);
                mPlaces.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder {
        TextView placeText;
    }

}
