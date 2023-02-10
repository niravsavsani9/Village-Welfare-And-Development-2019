package grammitra2019.com.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import grammitra2019.com.Model.Tickets;
import grammitra2019.com.R;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.MyTicketViewHolder> {

    private Context                       mContext;
    private List<Tickets>                 mTicketsList;
    public  UserTicketResponseEventLister mOnClickListener;
    private DatabaseReference             databaseReference;
    private int                           counterClick = 0;

    public AdminAdapter(Context mContext, List<Tickets> mTicketsList, UserTicketResponseEventLister lister) {
        this.mContext = mContext;
        this.mTicketsList = mTicketsList;
        this.mOnClickListener = lister;
    }

    @NonNull
    @Override
    public MyTicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ticket_admin, viewGroup, false);
        view.findViewById(R.id.item_description).setVisibility(View.GONE);
        view.findViewById(R.id.item_age).setVisibility(View.GONE);
        view.findViewById(R.id.item_email).setVisibility(View.GONE);
        view.findViewById(R.id.item_mobileno).setVisibility(View.GONE);
        view.findViewById(R.id.item_admin_complete).setVisibility(View.GONE);
        view.findViewById(R.id.item_admin_inprogress).setVisibility(View.GONE);
        view.findViewById(R.id.item_admin_reject).setVisibility(View.GONE);
        view.findViewById(R.id.item_admin_city).setVisibility(View.GONE);
        view.findViewById(R.id.item_city).setVisibility(View.GONE);
        return new MyTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyTicketViewHolder myTicketViewHolder, int i) {

        final Tickets tickets = mTicketsList.get(i);

        // setting up ticket number which is not coming from pojo class we setting up when adapter setting up the tickets
        myTicketViewHolder.ticketNo.setText(String.valueOf(i + 1));
        myTicketViewHolder.ticketDateTime.setText(tickets.getTicketDateTime());

        //here we are setting up all the details of tickets where coming up from pojo class
        myTicketViewHolder.ticketCity.setText(tickets.getTicketCity());
        myTicketViewHolder.ticketCategory.setText(tickets.getTicketCategory());
        myTicketViewHolder.ticketDescription.setText(tickets.getTicketDescription());
        Picasso.get()
                .load(tickets.getTicketImageUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerInside()
                .into(myTicketViewHolder.ticketImageUrl);
        myTicketViewHolder.ticketStatus.setText(tickets.getTicketStatus());

        myTicketViewHolder.ticketUsername.setText(tickets.getUserName());
        myTicketViewHolder.ticketUserAge.setText(tickets.getUserAge());
        myTicketViewHolder.ticketUserEmail.setText(tickets.getUserEmail());
        myTicketViewHolder.ticketUserMobileNo.setText(tickets.getUserMobile());

        myTicketViewHolder.ticketImageUrl.setVisibility(View.GONE);
        myTicketViewHolder.ticketDescription.setVisibility(View.GONE);
        myTicketViewHolder.ticketUserAge.setVisibility(View.GONE);
        myTicketViewHolder.ticketUserEmail.setVisibility(View.GONE);
        myTicketViewHolder.ticketUserMobileNo.setVisibility(View.GONE);


        // complete button which working for each tickets
        myTicketViewHolder.mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.btnComplete(tickets);
                }
            }
        });

        // In progress button which working for each tickets
        myTicketViewHolder.mInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.btnInProgress(tickets);
                }
            }
        });

        // Reject button which working for each tickets
        myTicketViewHolder.mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.btnReject(tickets);
                }
            }
        });

        myTicketViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterClick==0)
                {
                    view.findViewById(R.id.item_admin_imgview).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_description).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_description).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_age).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_age).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_email).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_city).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_city).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_email).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_mobileno).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_mobileno).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_complete).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_inprogress).setVisibility(view.getVisibility());
                    view.findViewById(R.id.item_admin_reject).setVisibility(view.getVisibility());

                    counterClick = counterClick+1;
                }
                else if(counterClick==1)
                {
                    view.findViewById(R.id.item_admin_imgview).setVisibility(View.GONE);
                    view.findViewById(R.id.item_description).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_description).setVisibility(View.GONE);
                    view.findViewById(R.id.item_age).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_age).setVisibility(View.GONE);
                    view.findViewById(R.id.item_email).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_email).setVisibility(View.GONE);
                    view.findViewById(R.id.item_city).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_city).setVisibility(View.GONE);
                    view.findViewById(R.id.item_mobileno).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_mobileno).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_complete).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_inprogress).setVisibility(View.GONE);
                    view.findViewById(R.id.item_admin_reject).setVisibility(View.GONE);

                    counterClick = counterClick-1;
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        // defining pojo class size;
        return mTicketsList.size();
    }

    public class MyTicketViewHolder extends RecyclerView.ViewHolder {

        // here we are taking all the properties which coming from layout which we are inflating in admin activity
        TextView ticketCategory, ticketCity, ticketDescription, ticketStatus, ticketDateTime, ticketUsername, ticketUserAge, ticketUserEmail, ticketUserMobileNo, ticketNo;
        ImageView ticketImageUrl;
        Button    mComplete, mInProgress, mReject;
        CardView cardView;

        public MyTicketViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.item_admin_cardview);
            ticketNo = itemView.findViewById(R.id.item_admin_number);
            ticketDateTime = itemView.findViewById(R.id.item_admin_dateTime);

            ticketCity = itemView.findViewById(R.id.item_admin_city);
            ticketCategory = itemView.findViewById(R.id.item_admin_category);
            ticketDescription = itemView.findViewById(R.id.item_admin_description);
            ticketImageUrl = itemView.findViewById(R.id.item_admin_imgview);
            ticketStatus = itemView.findViewById(R.id.item_admin_status);

            ticketUsername = itemView.findViewById(R.id.item_admin_username);
            ticketUserAge = itemView.findViewById(R.id.item_admin_age);
            ticketUserEmail = itemView.findViewById(R.id.item_admin_email);
            ticketUserMobileNo = itemView.findViewById(R.id.item_admin_mobileno);

            mComplete = itemView.findViewById(R.id.item_admin_complete);
            mInProgress = itemView.findViewById(R.id.item_admin_inprogress);
            mReject = itemView.findViewById(R.id.item_admin_reject);
        }
    }

    /* deleting item when admin swiping the ticket & it effects on user also when admin delete the ticket than ticket
        will also deleted from user side*/

    public void deleteItem(int position) {

        //here we are defining the path where wew want to delete the ticket
        databaseReference = FirebaseDatabase.getInstance().getReference("Tickets");
        String path = String.valueOf(databaseReference.child(mTicketsList.get(position).getTicketKey()).removeValue());
    }

    // button listener for three different button
    public interface UserTicketResponseEventLister {

        void btnComplete(Tickets tickets);

        void btnInProgress(Tickets tickets);

        void btnReject(Tickets tickets);
    }
}