package Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.ziac.wheelzcustomer.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import AdapterClass.DealersAdapter;
import ModelClasses.CommonClass;
import ModelClasses.Global;
import ModelClasses.zList;


public class DealersFragment extends Fragment {


    String Url;
    private zList statename;
    private CommonClass commonClass;
    private zList cityname;
    private Dialog zDialog;
    TextView Statetxt,Citytxt;
    LinearLayout Statedp,Citydp;
    String statecode,citycode,searchquery,currentLocationString,fullAddress,sublocality;
    RecyclerView DealerlistRV;
    DealersAdapter dealersAdapter;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    SearchView searchView;
    LinearLayout LinearSearch;
    LocationManager locationManager;

    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_LOCATION_PERMISSIONS = 2;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_dealers, container, false);
        Context context = requireActivity();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Statedp =view.findViewById(R.id.statedp);
        Citydp =view.findViewById(R.id.citydp);
        DealerlistRV=view.findViewById(R.id.dealerlist);
        progressBar=view.findViewById(R.id.progressBardealers);
        Statetxt=view.findViewById(R.id.statetext);
        Citytxt=view.findViewById(R.id.citytext);
        swipeRefreshLayout=view.findViewById(R.id.refreshprofile);

        int newWidthInPixels = 100;
        int newHeightInPixels = 100;
        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        params.width = newWidthInPixels;
        params.height = newHeightInPixels;
        progressBar.setLayoutParams(params);

        searchquery="";
        statecode="0";
        citycode="0";

        getstates();
        getDealerslist();
/*
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, get current location
            getCurrentLocation();
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getDealerslist();
            }
        });


        LinearSearch =view.findViewById(R.id.searchdealerslnr);
        searchView =view.findViewById(R.id.searchalldealers);
        LinearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setEnabled(false);
                searchView.setIconified(false);
                searchView.requestFocus();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                swipeRefreshLayout.setEnabled(false);
                performSearch(query);

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        DealerlistRV.setLayoutManager(linearLayoutManager);
        DealerlistRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        Statedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statespopup();
            }
        });

        Citydp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citiespopup();
            }
        });
        return view;
    }





    private void performSearch(String query) {
        searchquery=query;
        showLoading();
        RequestQueue queue= Volley.newRequestQueue(requireActivity());

        String Url = Global.searchalldealers+"searchtext="+searchquery+"&state_code="+statecode+"&city_code="+citycode;

        JsonArrayRequest jsonArrayrequest = new JsonArrayRequest(Request.Method.POST,Url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Global.dealersarraylist = new ArrayList<CommonClass>();
                commonClass = new CommonClass();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jsonObject;
                    try {

                        jsonObject = response.getJSONObject(i);
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    commonClass = new CommonClass();
                    try {

                        commonClass.setCom_name(jsonObject.getString("com_name"));
                        commonClass.setImage_path(jsonObject.getString("logo_image"));
                        commonClass.setCom_code(jsonObject.getString("com_code"));
                        commonClass.setState_name(jsonObject.getString("state_name"));
                        commonClass.setState_code(jsonObject.getString("state_code"));
                        commonClass.setCity_name(jsonObject.getString("city_name"));
                        commonClass.setCity_code(jsonObject.getString("city_code"));
                        commonClass.setCom_address(jsonObject.getString("com_address"));
                        commonClass.setCom_pin(jsonObject.getString("com_pin"));
                        commonClass.setCom_email(jsonObject.getString("com_email"));
                        commonClass.setCom_website(jsonObject.getString("com_website"));
                        commonClass.setCom_contact(jsonObject.getString("com_contact"));
                        commonClass.setCom_contact_mobno(jsonObject.getString("com_contact_mobno"));
                        commonClass.setCom_phone(jsonObject.getString("com_phone"));
                        commonClass.setCom_lng(jsonObject.getString("com_lng"));
                        commonClass.setCom_lat(jsonObject.getString("com_lat"));
                        commonClass.setCom_contact_email(jsonObject.getString("com_contact_email"));


                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    Global.dealersarraylist.add(commonClass);
                    swipeRefreshLayout.setRefreshing(false);
                }

                dealersAdapter = new DealersAdapter(Global.dealersarraylist,getContext());
                DealerlistRV.setAdapter(dealersAdapter);
                dealersAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                hideLoading();

            }
        },  error -> {

            if (error instanceof NoConnectionError) {
                hideLoading();
                if (error instanceof TimeoutError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Request Time-Out");
                } else if (error instanceof NoConnectionError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Internet connection unavailable");
                } else if (error instanceof ServerError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Server Error");
                } else if (error instanceof NetworkError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Network Error");
                } else if (error instanceof ParseError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Parse Error");
                }
                swipeRefreshLayout.setRefreshing(false);
            }
            // Global.customtoast(getApplicationContext(),getLayoutInflater(),"Technical error : Unable to get dashboard data !!" + error);

        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String accesstoken = Global.sharedPreferences.getString("access_token", null);
                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }


        };

        jsonArrayrequest.setRetryPolicy(new DefaultRetryPolicy(
                0, // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonArrayrequest);

    }


    private void getDealerslist() {
        showLoading();
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        Url = Global.searchalldealers+"searchtext="+searchquery+"&state_code="+statecode+"&city_code="+citycode;
        JsonArrayRequest jsonArrayrequest = new JsonArrayRequest(Request.Method.POST,Url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Global.dealersarraylist = new ArrayList<CommonClass>();
                commonClass = new CommonClass();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jsonObject;
                    try {

                        jsonObject = response.getJSONObject(i);
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    commonClass = new CommonClass();
                    try {

                        commonClass.setImage_path(jsonObject.getString("logo_image"));
                        commonClass.setCom_code(jsonObject.getString("com_code"));
                        commonClass.setState_name(jsonObject.getString("state_name"));
                        commonClass.setState_code(jsonObject.getString("state_code"));
                        commonClass.setCity_name(jsonObject.getString("city_name"));
                        commonClass.setCity_code(jsonObject.getString("city_code"));
                        commonClass.setCom_name(jsonObject.getString("com_name"));
                        commonClass.setCom_address(jsonObject.getString("com_address"));
                        commonClass.setCom_pin(jsonObject.getString("com_pin"));
                        commonClass.setCom_email(jsonObject.getString("com_email"));
                        commonClass.setCom_website(jsonObject.getString("com_website"));
                        commonClass.setCom_contact(jsonObject.getString("com_contact"));
                        commonClass.setCom_contact_mobno(jsonObject.getString("com_contact_mobno"));
                        commonClass.setCom_phone(jsonObject.getString("com_phone"));
                        commonClass.setCom_lng(jsonObject.getString("com_lng"));
                        commonClass.setCom_lat(jsonObject.getString("com_lat"));
                        commonClass.setCom_contact_email(jsonObject.getString("com_contact_email"));

                       /* String statename=jsonObject.getString("state_name");
                        Toast.makeText(requireActivity(), statename, Toast.LENGTH_SHORT).show();
*/
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    Global.dealersarraylist.add(commonClass);
                    swipeRefreshLayout.setRefreshing(false);
                }

                dealersAdapter = new DealersAdapter(Global.dealersarraylist,getContext());
                DealerlistRV.setAdapter(dealersAdapter);
                dealersAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                hideLoading();

            }
        },  error -> {

            if (error instanceof NoConnectionError) {
                hideLoading();
                if (error instanceof TimeoutError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Request Time-Out");
                } else if (error instanceof NoConnectionError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Internet connection unavailable");
                } else if (error instanceof ServerError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Server Error");
                } else if (error instanceof NetworkError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Network Error");
                } else if (error instanceof ParseError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Parse Error");
                }
                swipeRefreshLayout.setRefreshing(false);
            }
            // Global.customtoast(getApplicationContext(),getLayoutInflater(),"Technical error : Unable to get dashboard data !!" + error);

        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String accesstoken = Global.sharedPreferences.getString("access_token", null);
                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }


        };

        jsonArrayrequest.setRetryPolicy(new DefaultRetryPolicy(
                0, // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonArrayrequest);

    }


    private void getstates() {

        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        JsonArrayRequest jsonArrayrequest = new JsonArrayRequest(Request.Method.GET, Global.GetStates, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Global.statearraylist = new ArrayList<zList>();
                statename = new zList();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject e;
                    try {
                        // converting to json object
                        e = response.getJSONObject(i);
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    statename = new zList();
                    try {
                        // getting the state name from the object
                        statename.set_name(e.getString("state_name"));
                        statename.set_code(e.getString("state_code"));
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    Global.statearraylist.add(statename);
                }

            }
        },  error -> {

            if (error instanceof NoConnectionError) {
                if (error instanceof TimeoutError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Request Time-Out");
                } else if (error instanceof NoConnectionError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Internet connection unavailable");
                } else if (error instanceof ServerError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Server Error");
                } else if (error instanceof NetworkError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Network Error");
                } else if (error instanceof ParseError) {
                    Global.customtoast(requireActivity(), getLayoutInflater(), "Parse Error");
                }
            }
            // Global.customtoast(getApplicationContext(),getLayoutInflater(),"Technical error : Unable to get dashboard data !!" + error);

        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String accesstoken = Global.sharedPreferences.getString("access_token", null);
                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }


        };

        jsonArrayrequest.setRetryPolicy(new DefaultRetryPolicy(
                0, // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonArrayrequest);

    }

    public void statespopup() {

        zDialog = new Dialog(requireActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        zDialog.setContentView(R.layout.popup_list);

        ListView lvStates = zDialog.findViewById(R.id.lvstates);

        if (Global.statearraylist == null || Global.statearraylist.size() == 0) {
            // Toast.makeText(getBaseContext(), "States list not found !! Please try again !!", Toast.LENGTH_LONG).show();
            return;
        }
        final StateAdapter laStates = new StateAdapter(Global.statearraylist);
        lvStates.setAdapter(laStates);

        zDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        zDialog.show();

        SearchView svstate = zDialog.findViewById(R.id.svstate);
        svstate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                laStates.getFilter().filter(newText);
                return false;
            }
        });
    }

    private class StateAdapter extends BaseAdapter implements Filterable {
        private ArrayList<zList> mDataArrayList;

        public StateAdapter(ArrayList<zList> arrayList) {
            this.mDataArrayList = arrayList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    List<zList> mFilteredList = new ArrayList<>();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mFilteredList = Global.statearraylist;
                    } else {
                        for (zList dataList : Global.statearraylist) {
                            if (dataList.get_name().toLowerCase().contains(charString)) {
                                mFilteredList.add(dataList);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredList;
                    filterResults.count = mFilteredList.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mDataArrayList = (ArrayList<zList>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getCount() {
            return mDataArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return mDataArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View v = getLayoutInflater().inflate(R.layout.popup_listitems, null);
            final TextView tvstatenameitem = v.findViewById(R.id.tvsingleitem);
            RadioButton radioButton = v.findViewById(R.id.radio_button);
            statename = mDataArrayList.get(i);
            tvstatenameitem.setText(statename.get_name());

            radioButton.setOnClickListener(view1 -> {
                boolean isChecked = radioButton.isChecked();
                radioButton.setChecked(!isChecked);
                statename = mDataArrayList.get(i);
                statecode = statename.get_code();
                citycode = "";
                getDealerslist();
                zDialog.dismiss();
                getcity();
                Statetxt.setText(statename.get_name());
            });

            tvstatenameitem.setOnClickListener(view1 -> {
                statename = mDataArrayList.get(i);
                statecode = statename.get_code();
                citycode = "";
                getDealerslist();
                zDialog.dismiss();
                getcity();
                Statetxt.setText(statename.get_name());
            });

            return v;
        }


    }


    private void getcity() {
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        String url=Global.GetCity;
        url= url+"state_code="+statecode;
        StringRequest jsonArrayrequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String sresponse) {

                        JSONArray response;
                        try {
                            response = new JSONArray(sresponse);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Global.cityarraylist = new ArrayList<zList>();
                        cityname = new zList();
                       // tvCity.setText("");
                        for (int i = 0; i < response.length(); i++) {
                            final JSONObject e;
                            try {
                                // converting to json object
                                e = response.getJSONObject(i);
                            } catch (JSONException ex) {
                                throw new RuntimeException(ex);
                            }
                            cityname = new zList();
                            try {
                                // getting the city name from the object
                                cityname.set_name(e.getString("city_name"));
                                cityname.set_code(e.getString("city_code"));
                                citycode = cityname.get_code();

                              Citytxt.setText("City");

                            } catch (JSONException ex) {
                                throw new RuntimeException(ex);
                            }
                            Global.cityarraylist.add(cityname);
                        }


                    }
                },
                error -> {

                    if (error instanceof NoConnectionError) {
                        if (error instanceof TimeoutError) {
                            Global.customtoast(requireActivity(), getLayoutInflater(), "Request Time-Out");
                        } else if (error instanceof NoConnectionError) {
                            Global.customtoast(requireActivity(), getLayoutInflater(), "Internet connection unavailable");
                        } else if (error instanceof ServerError) {
                            Global.customtoast(requireActivity(), getLayoutInflater(), "Server Error");
                        } else if (error instanceof NetworkError) {
                            Global.customtoast(requireActivity(), getLayoutInflater(), "Network Error");
                        } else if (error instanceof ParseError) {
                            Global.customtoast(requireActivity(), getLayoutInflater(), "Parse Error");
                        }
                    }


                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String accesstoken = Global.sharedPreferences.getString("access_token", null);
                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }


        };

        jsonArrayrequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(0),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(jsonArrayrequest);

    }

    public void citiespopup() {

        zDialog = new Dialog(requireActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        //zDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        zDialog.setContentView(R.layout.popup_list);

        ListView lvStates = zDialog.findViewById(R.id.lvstates);

        if (Global.cityarraylist == null || Global.cityarraylist.size() == 0) {
            Global.customtoast(requireActivity(), getLayoutInflater(), "No cities found for selected state");

            return;
        }
        final CityAdapter laStates = new CityAdapter(Global.cityarraylist);
        lvStates.setAdapter(laStates);

        zDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        zDialog.show();

        SearchView svstate = zDialog.findViewById(R.id.svstate);
        svstate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                laStates.getFilter().filter(newText);
                return false;
            }
        });
    }

    private class CityAdapter extends BaseAdapter implements Filterable {
        private ArrayList<zList> mDataArrayList;

        public CityAdapter(ArrayList<zList> arrayList) {
            this.mDataArrayList = arrayList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    List<zList> mFilteredList = new ArrayList<>();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mFilteredList = Global.statearraylist;
                    } else {
                        for (zList dataList : Global.statearraylist) {
                            if (dataList.get_name().toLowerCase().contains(charString)) {
                                mFilteredList.add(dataList);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredList;
                    filterResults.count = mFilteredList.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mDataArrayList = (ArrayList<zList>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getCount() {
            return mDataArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return mDataArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.popup_listitems, null);
            final TextView tvstatenameitem = v.findViewById(R.id.tvsingleitem);
            RadioButton radioButton=v.findViewById(R.id.radio_button);
            cityname = mDataArrayList.get(i);
            tvstatenameitem.setText(cityname.get_name());


            tvstatenameitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cityname = mDataArrayList.get(i);
                   // Citytxt.setText(cityname.get_name());
                    citycode = cityname.get_code();
                    Citytxt.setText(cityname.get_name());
                    getDealerslist();
                    zDialog.dismiss();


                }
            });

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cityname = mDataArrayList.get(i);
                    // Citytxt.setText(cityname.get_name());
                    citycode = cityname.get_code();
                    Citytxt.setText(cityname.get_name());
                    getDealerslist();
                    zDialog.dismiss();


                }
            });
            return v;
        }
    }
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        DealerlistRV.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        DealerlistRV.setVisibility(View.VISIBLE);
    }


}

/*  @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Get current location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            currentLocationString = latitude + ", " + longitude;

                            // Use Geocoder to get address details
                            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    sublocality = address.getSubLocality();  // Area name
                                    fullAddress = address.getAddressLine(0); // Full address

                                    Global.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
                                    Global.editor = Global.sharedPreferences.edit();
                                    Global.editor.putString("currentLocationString", currentLocationString);
                                    Global.editor.putString("currentStreetName", sublocality);
                                    Global.editor.putString("currentFullAddress", fullAddress);
                                    Global.editor.apply();

                                } else {
                                    Log.e("DealersFragment", "Unable to get address");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("DealersFragment", "Geocoder service not available");
                            }
                        } else {
                            Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get current location
                getCurrentLocation();
            } else {
                // Permission denied, handle accordingly (e.g., show message or disable location features)
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/