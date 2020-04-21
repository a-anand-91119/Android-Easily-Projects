package com.a3solutions.e_lection;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anand on 25-02-2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<ListItem> items;
    private Context context;
    public CardAdapter(String electionName[],String[] id, String[] candid_id, String[] candid_name, String[] election_id, String[] party, String[] symbol, Context context){
        super();
        this.context=context;
        items = new ArrayList<ListItem>();
        for(int i =0; i<id.length; i++){
            ListItem item = new ListItem();
            item.setId(id[i]);
            item.setCandidId(candid_id[i]);
            item.setCandidName(candid_name[i]);
            item.setElectionId(election_id[i]);
            item.setParty(party[i]);
            item.setSymbol(symbol[i]);
            item.setElectionName(electionName[i]);
            items.add(item);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_list_voters, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItem list = items.get(position);
        holder.candidatename_card.setText(list.getCandidName());
        holder.party_card.setText(list.getParty());
        holder.networkImageView.setImageUrl(list.getSymbol(), CandidateSingleton.getInstance(context.getApplicationContext()).getImageLoader());
        holder.electionname_card.setText(list.getElectionName());
        holder.rb_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               VoterActivity.confirmVote(list.getCandidName(),list.getParty());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView candidatename_card, party_card,electionname_card;
        NetworkImageView networkImageView;
        Button rb_vote;

        public ViewHolder(View itemView) {
            super(itemView);
            candidatename_card = (TextView) itemView.findViewById(R.id.candidatename_card);
            party_card = (TextView) itemView.findViewById(R.id.party_card);
            networkImageView=(NetworkImageView)itemView.findViewById(R.id.nwv_candidate);
            electionname_card=(TextView)itemView.findViewById(R.id.electionname_card);
            rb_vote=(Button)itemView.findViewById(R.id.rb_vote);
        }
    }
}