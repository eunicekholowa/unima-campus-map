package tech.nextgen.unimacampusmap;

import java.io.Serializable;


/**
 * Class which serves as the model in an MVC architecture for setting and getting information
 * related to MapPreviews
 */
class MapPreview implements Serializable {

    private int mMapNum;
    private String mTitle;
    private boolean mTransportNetwork = false;
    private boolean mGeocoding = false;
    private String mDesc;
    private byte[] mThumbnailByteStream;

    public int getMapNum() {
        return mMapNum;
    }

    public void setMapNum(int mapNum) {
        mMapNum = mapNum;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mapTitle) {
        mTitle = mapTitle;
    }

    public boolean hasTransportNetwork() {
        return mTransportNetwork;
    }

    public void setTransportNetwork(boolean transportNetwork) {
        mTransportNetwork = transportNetwork;
    }

    public boolean hasGeocoding() {
        return mGeocoding;
    }

    public void setGeocoding(boolean geocoding) {
        mGeocoding = geocoding;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mapInfo) {
        mDesc = mapInfo;
    }

    public byte[] getThumbnailByteStream() {
        return mThumbnailByteStream;
    }

    public void setThumbnailByteStream(byte[] thumbnailByteStream) {
        mThumbnailByteStream = thumbnailByteStream;
    }
}
