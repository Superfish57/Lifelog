package com.suckow.lifelog.presenter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.suckow.lifelog.model.logbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Future;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class LogbookManager {

    final String TAG = "LogbookManager()";

    FirebaseUser user;
    FirebaseFirestore db;

    public LogbookManager(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();


    }

    public io.reactivex.Observable<List<logbook>> subscribeLogs() {

            List<logbook> logList = new ArrayList<logbook>();

            io.reactivex.Observable<List<logbook>> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<List<logbook>>() {
                @Override
                public void subscribe(ObservableEmitter<List<logbook>> emitter) throws Exception {
                    final CollectionReference docRef =  db.collection("users/" + user.getUid() + "/logbooks");
                    docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                List logs = queryDocumentSnapshots.getDocuments();
                                List logList = new ArrayList();
                                for (Object doc: logs) {
                                    QueryDocumentSnapshot mDoc = (QueryDocumentSnapshot) doc;
                                    logList.add(mDoc.toObject(logbook.class));
                                }
                                emitter.onNext(logList);
                            } else {
                                Log.d(TAG, "Current data: null");
                                emitter.onNext(new ArrayList<>());
                            }

                        }
                    });
                    /*
                    db.collection("users/" + user.getUid() + "/logbooks").get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Found " + task.getResult().size() + " logs");
                                    for (DocumentSnapshot document : task.getResult()) {
                                        logList.add(document.toObject(logbook.class));
                                    }
                                    emitter.onNext(logList);
                                }

                            });
                    */
                }

            });


            return observable;
    }



    public void addLog(logbook log) {
        db.collection("users/" + user.getUid() + "/logbooks").add(log);
    }

    public void updateUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}
