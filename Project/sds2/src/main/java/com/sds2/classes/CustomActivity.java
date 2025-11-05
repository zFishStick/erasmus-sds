package com.sds2.classes;
import java.util.List;


public class CustomActivity {

    private String name;
    private String description;
    private Price price;
    private List<String> pictures;
    private String bookingLink;
    private String minimumDuration;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Price getPrice() { return price; }
    public List<String> getPictures() { return pictures; }
    public String getBookingLink() { return bookingLink; }
    public String getMinimumDuration() { return minimumDuration; }

    public static class Price {
        private String amount;

        public String getAmount() { return amount; }
    }
}

