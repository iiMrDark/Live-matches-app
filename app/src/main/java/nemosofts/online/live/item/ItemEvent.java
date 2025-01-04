package nemosofts.online.live.item;


import java.io.Serializable;

public class ItemEvent implements Serializable {

    private final String id;
    private final String postID;
    private final String title;
    private final String time;
    private final String date;
    private final String titleOne;
    private final String thumbOne;
    private final String titleTwo;
    private final String thumbTwo;

    private final String eventCheckLive;
    private final String commentator;
    private final String category;
    private final String channel;

    public ItemEvent(String id, String postID, String title, String time, String date,
                     String titleOne, String thumbOne, String titleTwo, String thumbTwo, String eventCheckLive,
                     String commentator, String category, String channel) {
        this.id = id;
        this.postID = postID;
        this.title = title;
        this.time = time;
        this.date = date;
        this.titleOne = titleOne;
        this.thumbOne = thumbOne;
        this.titleTwo = titleTwo;
        this.thumbTwo = thumbTwo;
        this.eventCheckLive = eventCheckLive;
        this.commentator = commentator;
        this.category = category;
        this.channel = channel;

    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postID;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getcategory() {
        return category;
    }
    public String getcommentator() {
        return commentator;
    }
    public String getchannel() {
        return channel;
    }
    public String geteventCheckLive() {
        return eventCheckLive;
    }

    public String getDate() {
        return date;
    }

    public String getTitleOne() {
        return titleOne;
    }

    public String getThumbOne() {
        return thumbOne;
    }

    public String getTitleTwo() {
        return titleTwo;
    }

    public String getThumbTwo() {
        return thumbTwo;
    }
}