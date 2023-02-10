package grammitra2019.com.Model;

public class Tickets {

    private String userName;
    private String userEmail;
    private String userAge;
    private String userMobile;
    private String ticketKey;
    private String ticketDateTime;
    private String ticketCategory;
    private String ticketCity;
    private String ticketDescription;
    private String ticketImageUrl;
    private String ticketStatus;
    private String userUID;

    public Tickets(){}

    public Tickets(String userName, String userEmail, String userAge, String userMobile, String ticketKey,  String ticketDateTime , String ticketCategory, String ticketCity, String ticketDescription, String ticketImageUrl, String ticketStatus, String userUID) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAge = userAge;
        this.userMobile = userMobile;
        this.ticketKey = ticketKey;
        this.ticketDateTime = ticketDateTime;
        this.ticketCategory = ticketCategory;
        this.ticketCity = ticketCity;
        this.ticketDescription = ticketDescription;
        this.ticketImageUrl = ticketImageUrl;
        this.ticketStatus = ticketStatus;
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public String getTicketKey() {
        return ticketKey;
    }

    public String getTicketCity() {
        return ticketCity;
    }

    public String getTicketDateTime() {
        return ticketDateTime;
    }

    public String getTicketCategory() {
        return ticketCategory;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }

    public String getTicketImageUrl() {
        return ticketImageUrl;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public String getUserUID() {
        return userUID;
    }
}
