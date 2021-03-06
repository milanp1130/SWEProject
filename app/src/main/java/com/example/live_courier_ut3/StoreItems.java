package com.example.live_courier_ut3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StoreItems extends AppCompatActivity {




    //Target.class refers to the class that handles the scroll view of the items in ANY STORE.

    private static final String TAG = "StoreItemsViewActivity";
    LocationManager locationManager;
    LocationListener locationListener;


    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mImagePrices = new ArrayList<>();
    private ArrayList<String> mItemQuantity = new ArrayList<>();
    public String storeLookup = "store";
   // public String store;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Intent intent = getIntent();
        String store;
        store = intent.getStringExtra(storeLookup);
       // store = "Taco Bell";
        Log.d(TAG, "onCreate: " + store);

        initImageBitmaps(store);
   //     Toast.makeText(this,"made it to target activity", Toast.LENGTH_LONG).show();

    }

    private void initImageBitmaps(String store) {

        String stored = store;

        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");


        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("stores/" + stored + "/itemInfo/items");
        Log.d(TAG, "initImageBitmaps:          " + stored);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ArrayList<Map<String, String>> itemMap = new ArrayList<>();
                    itemMap = (ArrayList<Map<String, String>>) documentSnapshot.get("itemList");
                    for (int i = 0; i < itemMap.size(); i++) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap = (HashMap<String, String>) itemMap.get(i);
                        Iterator<String> mapItr = hashMap.keySet().iterator();
                        while (mapItr.hasNext()) {
                            String key = mapItr.next();
                            mNames.add(key);
                            mImageUrls.add(hashMap.get(key));

                        }
                        Log.d(TAG, "onSuccess: mNames " + mNames.toString());
                        Log.d(TAG, "onSuccess:  " + mImageUrls.toString());

                    }

                }
            }

        });

        DocumentReference mDocR = FirebaseFirestore.getInstance().document("stores/" + stored + "/itemInfo/items");
        Log.d(TAG, "initImageBitmaps:          " + stored);
        mDocR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ArrayList<String> transfer = (ArrayList<String>) documentSnapshot.get("prices");
                    assert transfer != null;
                    mImagePrices.addAll(transfer);
                    Log.d(TAG, "prices " + transfer.toString());

                }

            }
        });


        DocumentReference mDocRe = FirebaseFirestore.getInstance().document("stores/" + stored + "/itemInfo/items");
        Log.d(TAG, "initImageBitmaps:          " + stored);
        mDocRe.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ArrayList<String> trans = (ArrayList<String>) documentSnapshot.get("itemNumber");
                    assert trans != null;
                    mItemQuantity.addAll(trans);
                    Log.d(TAG, "number of items " + trans.toString());


                }

            }
        });


//        mImageUrls.add("https://www.pngpix.com/wp-content/uploads/2016/11/PNGPIX-COM-Target-PNG-Transparent-Image-1-500x500.png");
//        mNames.add("Target (do not click)");
//        mItemQuantity.add("This is a placeholder item to initialize the list");
//        mImagePrices.add("-");

        Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                initRecyclerView();
            }
        }, 1000);




    }

    private void initRecyclerView(){
        Intent intent = getIntent();
        String store;
        store = intent.getStringExtra(storeLookup);
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        // create a layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

     //   recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls,mImagePrices, mItemQuantity, store);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startLoginActivity();
                                } else {
                                    System.out.println("Failed");
                                }
                            }
                        });
                return true;
            case R.id.cart:

                Toast.makeText(this, "Cart",Toast.LENGTH_SHORT).show();
             //   String storeLookup = "store";
                Intent toCart = new Intent(this, CartSelection.class);
               // toCart.putExtra(storeLookup, "Target");
                startActivity(toCart);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }



    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        this.finish();
    }


 }