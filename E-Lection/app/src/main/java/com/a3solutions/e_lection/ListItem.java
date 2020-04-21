package com.a3solutions.e_lection;

/**
 * Created by Anand on 25-02-2017.
 */

public class ListItem {

    private String id, candid_id, candid_name, election_id, party, symbol, election_name;

    public void setId(String id) {
        this.id = id;
    }

    public void setCandidId(String candid_id) {
        this.candid_id = candid_id;
    }

    public void setCandidName(String candid_name) {
        this.candid_name = candid_name;
    }

    public void setElectionId(String election_id) {
        this.election_id = election_id;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setElectionName(String electionName) {
        this.election_name = electionName;
    }

    public String getId() {
        return id;
    }

    public String getCandidId() {
        return candid_id;
    }

    public String getCandidName() {
        return candid_name;
    }

    public String getElectionId() {
        return election_id;
    }

    public String getParty() {
        return party;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getElectionName() {
        return election_name;
    }
}