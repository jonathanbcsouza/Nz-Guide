package com.tour.guide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class TabAbout extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView city;
    private TextView population;
    private TextView airport;
    private TextView transport;
    private TextView aboutTheCity;
    private ImageView photoCity;
    private Spinner spinner;
    private ArrayAdapter arrayAdapter;

    private static final String TAG = "MainActivity";
    public static final int RC_SIGN_IN = 1;
    private ChildEventListener mChildEventListener;

    //Firebase authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.about_layout, container, false);

        city = rootView.findViewById(R.id.city);
        population = rootView.findViewById(R.id.population);
        airport = rootView.findViewById(R.id.airport);
        transport = rootView.findViewById(R.id.transport);
        aboutTheCity = rootView.findViewById(R.id.description_city);
        photoCity = rootView.findViewById(R.id.city_photo);

        spinner = (Spinner) rootView.findViewById(R.id.spinner_sleeps);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_cities, R.layout.spinner_custom_sleep_tab);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName()); //user is signed in
                } else { //user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        return rootView;
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };

        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mChildEventListener = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String getValue = (String) parent.getItemAtPosition(position);

        switch (position) {
            case 0:
                createDescription(getValue,
                        getString(R.string.city_0),
                        getString(R.string.population_0),
                        getString(R.string.airports_0),
                        getString(R.string.transports_0),
                        getString(R.string.about_city_0),
                        R.mipmap.auckland);
                break;

            case 1: // Wellington
                createDescription(getValue,
                        getString(R.string.city_1),
                        getString(R.string.population_1),
                        getString(R.string.airports_1),
                        getString(R.string.transports_1),
                        getString(R.string.about_city_1),
                        R.drawable.wellington);
                break;

            case 2: // Christchurch
                createDescription(getValue,
                        getString(R.string.city_2),
                        getString(R.string.population_2),
                        getString(R.string.airports_2),
                        getString(R.string.transports_2),
                        getString(R.string.about_city_2),
                        R.drawable.christchurch);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void createDescription(String passValue, String passCity, String passPopulation, String passAirport,
                                  String passTransport, String passAboutCity, int passPhotoCity) {

        Snackbar.make(getView(), "Changed to: " + passValue, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        city.setText(passCity);
        population.setText(passPopulation);
        airport.setText(passAirport);
        transport.setText(passTransport);
        aboutTheCity.setText(passAboutCity);
        photoCity.setImageResource(passPhotoCity);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
    }

    private void onSignedInInitialize(String username) {
        attachDatabaseReadListener();
    }
}


