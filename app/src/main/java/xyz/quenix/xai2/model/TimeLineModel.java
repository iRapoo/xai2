package xyz.quenix.xai2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeLineModel implements Parcelable {

    private String mMessage;
    private String mTeacher;
    private OrderStatus mStatus;

    public TimeLineModel() {
    }

    public TimeLineModel(String mMessage, String mTeacher, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mTeacher = mTeacher;
        this.mStatus = mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public void setDate(String date) {
        this.mTeacher = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mTeacher);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected TimeLineModel(Parcel in) {
        this.mMessage = in.readString();
        this.mTeacher = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}