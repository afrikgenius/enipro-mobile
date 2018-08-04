package com.enipro.parcelable;

import android.os.Parcel;

import com.enipro.data.remote.model.Date;
import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.data.remote.model.ObjectId;
import com.enipro.data.remote.model.PremiumDetails;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.SessionSchedule;
import com.enipro.data.remote.model.SessionTiming;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
public class Parcelable {

    SessionTiming timing = new SessionTiming(new ArrayList<>(), "2PM", "5PM");

    @Test
    public void testUserParcelable() {
        User details = new User();
        details.setActive(true);

        UserConnection con = new UserConnection("8w487245hhsdf8");
        List<UserConnection> chats = new ArrayList<>();
        chats.add(con);
        details.setChats(chats);
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        User fromParcel = User.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getChats().get(0).getUserId(), is("8w487245hhsdf8"));
    }

    @Test
    public void testFeedParcelable() {
        Feed details = new Feed();
        details.setModerated(true);

        FeedComment comment = new FeedComment();
        comment.setComment("THis is a freaking comment");

        List<FeedComment> commentList = new ArrayList<>();
        commentList.add(comment);
        details.setComments(commentList);
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Feed fromParcel = Feed.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getComments().size(), is(1));
    }

    @Test
    public void testUserConnectionParceleable() {
        UserConnection details = new UserConnection("5ab4da50d2c76b76328d3f01");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        UserConnection fromParcel = UserConnection.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getUserId(), is("5ab4da50d2c76b76328d3f01"));
    }

    @Test
    public void testDateParcelable() {
        Date details = new Date("2098712328");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Date fromParcel = Date.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getDate(), is("2098712328"));
    }

    @Test
    public void testPremiumDetailsParcelable() {
        PremiumDetails details = new PremiumDetails("2098712328", "UBA", 2000);
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        PremiumDetails fromParcel = PremiumDetails.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getBank(), is("UBA"));
        assertThat(fromParcel.getAccountNumber(), is("2098712328"));
        assertThat(fromParcel.getPayment_amount(), is(2000));
    }


    @Test
    public void testObjectIdParcelable() {
        ObjectId details = new ObjectId("5ab4da50d2c76b76328d3f01");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        ObjectId fromParcel = ObjectId.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getOid(), is("5ab4da50d2c76b76328d3f01"));
    }


    @Test
    public void testDocumentParcelable() {
        Document details = new Document("document", "https://filename", 90, ".pdf");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Document fromParcel = Document.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getSize(), is(90));
    }


    @Test
    public void testEducationParcelable() {
        Education details = new Education("Unilag", "Engineering", "BSc", "2011", "2016");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Education fromParcel = Education.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getSchool(), is("Unilag"));
    }

    @Test
    public void testExperienceParcelable() {
        Experience details = new Experience("Manufacturing", "Nestle", "Manager", "2016", "Present");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Experience fromParcel = Experience.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getOrganisation(), is("Nestle"));

    }


    @Test
    public void testFeedCommentParcelable() {
        FeedComment details = new FeedComment();
        details.setComment("Comment");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        FeedComment fromParcel = FeedComment.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getComment(), is("Comment"));
    }

    @Test
    public void testFeedContentParcelable() {
        FeedContent details = new FeedContent();
        details.setText("Content");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        FeedContent fromParcel = FeedContent.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getText(), is("Content"));
    }


    @Test
    public void testRequestParcelable() {
        Request details = new Request();
        details.setStatus("Pending");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        Request fromParcel = Request.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getStatus(), is("Pending"));
    }

    @Test
    public void testSavedFeedParcelable() {
        SavedFeed details = new SavedFeed("5ab4da50d2c76b76328d3f01");
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        SavedFeed fromParcel = SavedFeed.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getFeedId(), is("5ab4da50d2c76b76328d3f01"));

    }

    @Test
    public void testSessionTiming() {
        Parcel parcel = Parcel.obtain();
        timing.writeToParcel(parcel, timing.describeContents());
        parcel.setDataPosition(0);

        SessionTiming fromParcel = SessionTiming.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getDays(), is(new ArrayList<>()));
    }

    @Test
    public void testSessionSchedule() {
        SessionSchedule details = new SessionSchedule("Manufacturing", "Nestle", timing);
        Parcel parcel = Parcel.obtain();
        details.writeToParcel(parcel, details.describeContents());
        parcel.setDataPosition(0);

        SessionSchedule fromParcel = SessionSchedule.CREATOR.createFromParcel(parcel);
        assertThat(fromParcel.getEnd_period(), is("Nestle"));
    }

}
