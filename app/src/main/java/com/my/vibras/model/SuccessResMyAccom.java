package com.my.vibras.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class SuccessResMyAccom implements Serializable {


    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("accommodation_name")
        @Expose
        private String accommodationName;
        @SerializedName("accommodation_category")
        @Expose
        private String accommodationCategory;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("res_count_like")
        @Expose
        private String resCountLike;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("imageuser")
        @Expose
        private String imageuser;
        @SerializedName("time_ago")
        @Expose
        private String timeAgo;
        @SerializedName("like_status")
        @Expose
        private String likeStatus;
        @SerializedName("save_post")
        @Expose
        private String savePost;
        @SerializedName("restaurant_gallery")
        @Expose
        private List<RestaurantGallery> restaurantGallery;
        private final static long serialVersionUID = -4297836985562065132L;

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAccommodationName() {
            return accommodationName;
        }

        public void setAccommodationName(String accommodationName) {
            this.accommodationName = accommodationName;
        }

        public String getAccommodationCategory() {
            return accommodationCategory;
        }

        public void setAccommodationCategory(String accommodationCategory) {
            this.accommodationCategory = accommodationCategory;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getResCountLike() {
            return resCountLike;
        }

        public void setResCountLike(String resCountLike) {
            this.resCountLike = resCountLike;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getImageuser() {
            return imageuser;
        }

        public void setImageuser(String imageuser) {
            this.imageuser = imageuser;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }

        public String getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(String likeStatus) {
            this.likeStatus = likeStatus;
        }

        public String getSavePost() {
            return savePost;
        }

        public void setSavePost(String savePost) {
            this.savePost = savePost;
        }

        public List<RestaurantGallery> getRestaurantGallery() {
            return restaurantGallery;
        }

        public void setRestaurantGallery(List<RestaurantGallery> restaurantGallery) {
            this.restaurantGallery = restaurantGallery;
        }

        public class RestaurantGallery implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("accommodation_id")
            @Expose
            private String accommodationId;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("date_time")
            @Expose
            private String dateTime;
            private final static long serialVersionUID = 4956746976632478677L;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAccommodationId() {
                return accommodationId;
            }

            public void setAccommodationId(String accommodationId) {
                this.accommodationId = accommodationId;
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

        }
    }


    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = -6299875069489777455L;

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

}
