package com.my.vibras.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ravindra Birla on 16,August,2022
 */
public class SuccessResMyJoinedEvents implements Serializable {
    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("message")
    @Expose
    public String message;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class EventDetails implements Serializable
    {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("event_cat")
        @Expose
        private String eventCat;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("date_time_event")
        @Expose
        private String dateTimeEvent;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("like")
        @Expose
        private String like;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("like_count")
        @Expose
        private String likeCount;
        @SerializedName("booking_amount")
        @Expose
        private String bookingAmount;
        @SerializedName("event_start_time")
        @Expose
        private String eventStartTime;
        @SerializedName("event_end_time")
        @Expose
        private String eventEndTime;
        @SerializedName("event_attend")
        @Expose
        private String eventAttend;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        private final static long serialVersionUID = 5084306743216877796L;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEventCat() {
            return eventCat;
        }

        public void setEventCat(String eventCat) {
            this.eventCat = eventCat;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDateTimeEvent() {
            return dateTimeEvent;
        }

        public void setDateTimeEvent(String dateTimeEvent) {
            this.dateTimeEvent = dateTimeEvent;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(String likeCount) {
            this.likeCount = likeCount;
        }

        public String getBookingAmount() {
            return bookingAmount;
        }

        public void setBookingAmount(String bookingAmount) {
            this.bookingAmount = bookingAmount;
        }

        public String getEventStartTime() {
            return eventStartTime;
        }

        public void setEventStartTime(String eventStartTime) {
            this.eventStartTime = eventStartTime;
        }

        public String getEventEndTime() {
            return eventEndTime;
        }

        public void setEventEndTime(String eventEndTime) {
            this.eventEndTime = eventEndTime;
        }

        public String getEventAttend() {
            return eventAttend;
        }

        public void setEventAttend(String eventAttend) {
            this.eventAttend = eventAttend;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

    }
    public class EventGallery {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("event_id")
        @Expose
        public String eventId;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("image_file")
        @Expose
        public String imageFile;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

    }

    public class Result implements Serializable
    {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("event_id")
        @Expose
        private String eventId;
        @SerializedName("event_user_id")
        @Expose
        private String eventUserId;
        @SerializedName("member_id")
        @Expose
        private String memberId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("total_comments")
        @Expose
        private Integer totalComments;
        @SerializedName("total_like")
        @Expose
        private Integer totalLike;
        @SerializedName("like_status")
        @Expose
        private String likeStatus;
        @SerializedName("Iammember")
        @Expose
        private String iammember;
        @SerializedName("event_details")
        @Expose
        private EventDetails eventDetails;
        @SerializedName("event_gallery")
        @Expose
        private List<EventGallery> eventGallery;
        private final static long serialVersionUID = -994489627238671249L;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEventUserId() {
            return eventUserId;
        }

        public void setEventUserId(String eventUserId) {
            this.eventUserId = eventUserId;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public Integer getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(Integer totalComments) {
            this.totalComments = totalComments;
        }

        public Integer getTotalLike() {
            return totalLike;
        }

        public void setTotalLike(Integer totalLike) {
            this.totalLike = totalLike;
        }

        public String getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(String likeStatus) {
            this.likeStatus = likeStatus;
        }

        public String getIammember() {
            return iammember;
        }

        public void setIammember(String iammember) {
            this.iammember = iammember;
        }

        public EventDetails getEventDetails() {
            return eventDetails;
        }

        public void setEventDetails(EventDetails eventDetails) {
            this.eventDetails = eventDetails;
        }

        public List<EventGallery> getEventGallery() {
            return eventGallery;
        }

        public void setEventGallery(List<EventGallery> eventGallery) {
            this.eventGallery = eventGallery;
        }

    }
}