package com.example.MyBookShopApp.data.api;

public class VoteRequest {

    private Long reviewid;
    private Integer value;

    public Long getReviewid() {
        return reviewid;
    }

    public void setReviewid(Long reviewid) {
        this.reviewid = reviewid;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
