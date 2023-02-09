package com.codizcdp.test_multi_user_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RetrieveInBatchActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private LinearLayout llData;

    private static final String TAG = "RetrieveInBatchActivity";

    private void initialize() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        llData = findViewById(R.id.llData);
    }

    private void init() {
//        for (int i = 0; i < 100; i++) {
//            insertData();
//        }
        retrieveInBatch();
    }

    private String createUserID() {
        return UUID.randomUUID().toString();
    }

    private void retrieveInBatch() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("RetrieveInBatch");
        Query query = collectionReference.orderBy("timestamp").limit(30);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    for (DocumentSnapshot x : documents) {
                        addData(x.get("id").toString(), x.get("timestamp").toString());
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addData(String id, String timeStamp) {
        View data_layout = getLayoutInflater().inflate(R.layout.data_layout, null, false);

        TextView tvID = data_layout.findViewById(R.id.tvID);
        TextView tvTimeStamp = data_layout.findViewById(R.id.tvTimeStamp);
        LinearLayout llDataLayout = data_layout.findViewById(R.id.llDataLayout);

        tvID.setText("ID : "+id);
        tvTimeStamp.setText("TimeStamp : "+timeStamp);

        llData.addView(data_layout);
    }

    private void insertData() {
        String id = createUserID();
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("timestamp", System.currentTimeMillis());

        firebaseFirestore.collection("RetrieveInBatch").document(id).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: " + id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_in_batch);

        initialize();

        init();
    }
}