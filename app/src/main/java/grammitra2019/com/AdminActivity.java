package grammitra2019.com;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import grammitra2019.com.Adapter.AdminAdapter;
import grammitra2019.com.Model.Tickets;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView mAdminRecyclerView;

    public  DatabaseReference mDatabaseReference;
    private List<Tickets>     mTicketsList;
    private AdminAdapter      mAdminAdapter;
    private String            uniqueKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_admin);

        mAdminRecyclerView = findViewById(R.id.item_tickets_recycle);

        mTicketsList = new ArrayList<>();
        mDatabaseReference = Utility.DATABASE_REFERENCE_ROOT_USER;

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                mTicketsList.clear();

                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    Tickets tickets = ticketSnapshot.getValue(Tickets.class);
                    mTicketsList.add(tickets);
                }

                mAdminAdapter = new AdminAdapter(AdminActivity.this, mTicketsList, new AdminAdapter.UserTicketResponseEventLister() {
                    @Override
                    public void btnComplete(Tickets tickets) {
                        uniqueKey = tickets.getTicketKey();
                        mDatabaseReference.child(uniqueKey).child("ticketStatus").setValue("Status : Completed");
                    }

                    @Override
                    public void btnInProgress(Tickets tickets) {
                        uniqueKey = tickets.getTicketKey();
                        mDatabaseReference.child(uniqueKey).child("ticketStatus").setValue("Status : In Progress");
                    }

                    @Override
                    public void btnReject(Tickets tickets) {
                        uniqueKey = tickets.getTicketKey();
                        mDatabaseReference.child(uniqueKey).child("ticketStatus").setValue("Status : Rejected");
                    }
                });

                mAdminRecyclerView.setAdapter(mAdminAdapter);

                mAdminRecyclerView.setHasFixedSize(true);
                mAdminRecyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
                mAdminAdapter.notifyDataSetChanged();

                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminActivity.this);
                        alertDialog.setMessage("Alert")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mAdminAdapter.deleteItem(viewHolder.getAdapterPosition());
                                            Toast.makeText(AdminActivity.this, "Complaint is Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                        alertDialog.create().show();
                    }
                }).attachToRecyclerView(mAdminRecyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutUser() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
