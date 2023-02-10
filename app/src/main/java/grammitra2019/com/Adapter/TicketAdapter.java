package grammitra2019.com.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;
import grammitra2019.com.Model.Tickets;
import grammitra2019.com.R;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyTicketViewHolder> {

    private Context           context;
    private List<Tickets>     ticketsList;
    private DatabaseReference databaseReference;
    private UserTicketResponseEventLister userTicketResponseEventLister;

    public TicketAdapter(Context context, List<Tickets> ticketsList, UserTicketResponseEventLister userTicketResponseEventLister) {
        this.context = context;
        this.ticketsList = ticketsList;
        this.userTicketResponseEventLister = userTicketResponseEventLister;
    }

    @NonNull
    @Override
    public MyTicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_user, viewGroup, false);
        return new MyTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTicketViewHolder myTicketViewHolder, int i) {

        //here we are setting up position and display data from user side
        final Tickets tickets = ticketsList.get(i);

        myTicketViewHolder.ticketCategory.setText(tickets.getTicketCategory());
        myTicketViewHolder.ticketCity.setText(tickets.getTicketCity());
        myTicketViewHolder.ticketDateTime.setText(tickets.getTicketDateTime());
        myTicketViewHolder.ticketDescription.setText(tickets.getTicketDescription());
        Picasso.get()
                .load(tickets.getTicketImageUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(myTicketViewHolder.ticketImageUrl);
        myTicketViewHolder.ticketStatus.setText(tickets.getTicketStatus());
        myTicketViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (userTicketResponseEventLister != null) {
                    userTicketResponseEventLister.onTicketClickListener(tickets);
                    return true;
                }
                else {
                    return false;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

    public class MyTicketViewHolder extends RecyclerView.ViewHolder{

        private TextView ticketCategory, ticketCity, ticketDescription, ticketStatus, ticketDateTime;
        private ImageView ticketImageUrl;
        private CardView cardView;

        private MyTicketViewHolder(@NonNull View itemView) {
            super(itemView);

            ticketCategory = itemView.findViewById(R.id.item_ticket_category);
            ticketCity = itemView.findViewById(R.id.item_ticket_city);
            ticketDescription = itemView.findViewById(R.id.item_ticket_description);
            ticketImageUrl = itemView.findViewById(R.id.item_ticket_imageview);
            ticketStatus = itemView.findViewById(R.id.item_ticket_status);
            ticketDateTime = itemView.findViewById(R.id.item_ticket_dateTime);
            cardView = itemView.findViewById(R.id.root_cardview);
        }
    }

    public void deleteItem(int position) {

        //here we are defining the path where we want to delete the ticket
        databaseReference = FirebaseDatabase.getInstance().getReference("Tickets");
        String path = String.valueOf(databaseReference.child(ticketsList.get(position).getTicketKey()).removeValue());
    }

    /*public void restoreItem(Tickets tickets, int position){
        ticketsList.add(tickets,position);
        *//*databaseReference = FirebaseDatabase.getInstance().getReference("Tickets");
        String path = String.valueOf(databaseReference.child(ticketsList.get(position).getTicketKey()));*//*
    }*/

    public interface UserTicketResponseEventLister {

        void onTicketClickListener(Tickets tickets);
    }
}
