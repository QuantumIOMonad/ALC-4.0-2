package me.gfred.travelmantics;

import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FirebaseUtil {

    static final int RC_SIGN_IN = 123;
    static DatabaseReference mDatabaseReference;
    static StorageReference mStorageReference;
    static ArrayList<TravelDeal> mDeals;
    static boolean isAdmin;
    static FirebaseDatabase mFirebaseDatabase;
    private static FirebaseUtil firebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    static FirebaseStorage mFirebaseStorage;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static ListActivity caller;

    private FirebaseUtil() {
    }

    static void openFbReference(final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    FirebaseUtil.signIn();
                } else {
                    Toast.makeText(caller.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }
        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");

    }

    private static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    private static void connectStorage() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_pictures");
    }
}
