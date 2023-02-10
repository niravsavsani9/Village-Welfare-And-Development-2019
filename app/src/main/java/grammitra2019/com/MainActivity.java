package grammitra2019.com;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import grammitra2019.com.Adapter.TicketAdapter;
import grammitra2019.com.Model.Tickets;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton mFabRegisterIssue;
    private TextView             mBlankContain;
    private ImageView            mBlankLogoContain;
    private RecyclerView         mTicketRecycler;
    public  Intent               mIntent;
    public  long                 mBackPressedTime;
    public  DatabaseReference    mFireBaseDatabase;
    private List<Tickets>        mTicketsList;
    private TicketAdapter        mTicketAdapter;
    private CoordinatorLayout    coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_user);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);

        MainActivityIDconfigue();

        mTicketsList = new ArrayList<>();

        mFireBaseDatabase = Utility.DATABASE_REFERENCE_ROOT_USER;
        mFabRegisterIssue.setOnClickListener(this);

        mFireBaseDatabase.orderByChild("userUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mTicketsList.clear();

                        for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                            Tickets tickets = ticketSnapshot.getValue(Tickets.class);
                            getSupportActionBar().setTitle("  " + tickets.getUserName());
                            mTicketsList.add(tickets);
                        }

                        mTicketAdapter = new TicketAdapter(MainActivity.this, mTicketsList, new TicketAdapter.UserTicketResponseEventLister() {
                            @Override
                            public void onTicketClickListener(Tickets tickets) {
                                String key  = tickets.getTicketKey();
                                Intent intent = new Intent(MainActivity.this,EditTicketActivity.class);
                                intent.putExtra("TICKETKEY", key);
                                startActivity(intent);
                            }
                        });
                        mTicketRecycler.setAdapter(mTicketAdapter);
                        mTicketRecycler.setHasFixedSize(true);
                        mTicketRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        mTicketAdapter.notifyDataSetChanged();

                        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                            @Override
                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                                final Tickets restoreItem  = mTicketsList.get(viewHolder.getAdapterPosition());
                                final int     deletedIndex = viewHolder.getAdapterPosition();

                                mTicketAdapter.deleteItem(viewHolder.getAdapterPosition());
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout," Complaint removed", Snackbar.LENGTH_LONG);
                                snackbar.setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //mTicketAdapter.restoreItem(restoreItem,deletedIndxex);
                                    }
                                });
                            }
                        }).attachToRecyclerView(mTicketRecycler);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void MainActivityIDconfigue() {
        mBlankContain = findViewById(R.id.blank_textview);
        mBlankLogoContain = findViewById(R.id.blank_view);
        mFabRegisterIssue = findViewById(R.id.main_fabRegisterIssue);
        mTicketRecycler = findViewById(R.id.item_user_recycleview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logoutUser();
                return true;

            case R.id.issue:
                registerIssue();
                return true;

            case R.id.profile:
                userProfile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void userProfile() {
        startActivity(new Intent(this, UserActivity.class));
    }

    private void registerIssue() {
        startActivity(new Intent(this, TicketActivity.class));
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fabRegisterIssue:
                registerIssue();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        mBackPressedTime = System.currentTimeMillis();

        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
            finish();
            super.onBackPressed();

        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
