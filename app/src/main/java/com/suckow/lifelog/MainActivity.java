package com.suckow.lifelog;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.suckow.lifelog.model.logbook;
import com.suckow.lifelog.presenter.LogbookManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView userText;

    RecyclerView logbookRecyclerView;
    logbookAdapter logAdapter;

    Context context;

    LogbookManager logbookManager;

    FirebaseUser user;
    FirebaseAuth mAuth;

    FloatingActionButton addLogFAB;

    List<logbook> logDatabase;

    final String TAG = "MainActivity()";

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logbookManager = new LogbookManager();

        context = this;


        logbookRecyclerView = findViewById(R.id.logbookCardRecyclerView);
        addLogFAB = findViewById(R.id.newLogbookFab);
        addLogFAB.setOnClickListener(view -> newLogbook());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        logbookRecyclerView.setLayoutManager(mLayoutManager);

        logDatabase = new ArrayList<>();


        logAdapter = new logbookAdapter(logDatabase);
        logbookRecyclerView.setAdapter(logAdapter);
        logbookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

       if(user == null) {
           Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
           signIn();
       } else {
           subscribeLogs();
       }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();

                logbookManager.updateUser();
                subscribeLogs();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subscribeLogs() {
        logbookManager.subscribeLogs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logbooks -> {
                    logDatabase = logbooks;
                    updateUi();
                });
    }


    private void signIn() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @SuppressLint("CheckResult")
    private void updateUi() {

        Log.d(TAG, "Logs: " + logDatabase.toString());
        logAdapter.updateData(logDatabase);


    }



    private void newLogbook() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_book, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.create_book_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.ui_create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nameText = dialogView.findViewById(R.id.create_book_name);
                        String text = nameText.getText().toString();

                        String currentTime = Calendar.getInstance().getTime().toString();

                        logbookManager.addLog(new logbook(text, currentTime));

                    }
                })
                .setNegativeButton(R.string.ui_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        builder.show();

    }


}



class logbookAdapter extends RecyclerView.Adapter<logbookAdapter.ViewHolder> {
    private List<logbook> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.bookTitleText);
            date = v.findViewById(R.id.updateValueText);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public logbookAdapter(List<logbook> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public logbookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logbook_item_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.title.setText(mDataset.get(position).getBookTitle());
        holder.date.setText(mDataset.get(position).getLastUpdate());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateData(List data) {
        this.mDataset = data;
        notifyDataSetChanged();
    }
}
