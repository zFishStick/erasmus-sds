// package com.sds2.amadeus.dto;

// import java.util.List;

// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// @JsonIgnoreProperties(ignoreUnknown = true)
// public class HotelModels {
//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class GeoCode { public Double latitude; public Double longitude; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Address {
//         public List<String> lines;
//         public String cityName;
//         public String countryCode;
//     }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Contact { public String phone; public String email; public String website; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class HotelSummary {
//         public String hotelId;
//         public String name;
//         public GeoCode geoCode;
//         public Address address;
//         public String rating;
//         public Contact contact;
//         public java.util.List<String> amenities;
//         // Some payloads use different shapes for distance; ignored by default
//     }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class HotelContainer { public List<HotelSummary> data; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class HotelByIdContainer { public List<HotelSummary> data; }

//     // Offers
//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class HotelRef { public String hotelId; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Price { public String total; public String currency; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Guests { public Integer adults; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Room { public String type; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class Offer { public String id; public Price price; public Guests guests; public Room room; public String boardType; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class OffersItem { public HotelRef hotel; public List<Offer> offers; }

//     @JsonIgnoreProperties(ignoreUnknown = true)
//     public static class OffersContainer { public List<OffersItem> data; }
// }
