
package com.igordanilchik.android.rxandroid_test.model;

import android.os.Parcelable;

import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2016-08-04T16:18+0300")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Offer$$Parcelable
    implements Parcelable, ParcelWrapper<com.igordanilchik.android.rxandroid_test.model.Offer>
{

    private com.igordanilchik.android.rxandroid_test.model.Offer offer$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Offer$$Parcelable.Creator$$1 CREATOR = new Offer$$Parcelable.Creator$$1();

    public Offer$$Parcelable(com.igordanilchik.android.rxandroid_test.model.Offer offer$$2) {
        offer$$0 = offer$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(offer$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.igordanilchik.android.rxandroid_test.model.Offer offer$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(offer$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(offer$$1));
            if (offer$$1 .param == null) {
                parcel$$1 .writeInt(-1);
            } else {
                parcel$$1 .writeInt(offer$$1 .param.size());
                for (java.util.Map.Entry<java.lang.String, java.lang.String> entry$$0 : ((java.util.HashMap<java.lang.String, java.lang.String> ) offer$$1 .param).entrySet()) {
                    parcel$$1 .writeString(entry$$0 .getKey());
                    parcel$$1 .writeString(entry$$0 .getValue());
                }
            }
            parcel$$1 .writeString(offer$$1 .price);
            parcel$$1 .writeString(offer$$1 .pictureUrl);
            parcel$$1 .writeString(offer$$1 .name);
            parcel$$1 .writeString(offer$$1 .description);
            parcel$$1 .writeInt(offer$$1 .id);
            parcel$$1 .writeString(offer$$1 .url);
            parcel$$1 .writeInt(offer$$1 .categoryId);
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.igordanilchik.android.rxandroid_test.model.Offer getParcel() {
        return offer$$0;
    }

    public static com.igordanilchik.android.rxandroid_test.model.Offer read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.igordanilchik.android.rxandroid_test.model.Offer offer$$3;
            int reservation$$0 = identityMap$$1 .reserve();
            offer$$3 = new com.igordanilchik.android.rxandroid_test.model.Offer();
            identityMap$$1 .put(reservation$$0, offer$$3);
            int int$$0 = parcel$$3 .readInt();
            java.util.HashMap<java.lang.String, java.lang.String> map$$0;
            if (int$$0 < 0) {
                map$$0 = null;
            } else {
                map$$0 = new java.util.HashMap<java.lang.String, java.lang.String>(int$$0);
                for (int int$$1 = 0; (int$$1 <int$$0); int$$1 ++) {
                    java.lang.String string$$0 = parcel$$3 .readString();
                    java.lang.String string$$1 = parcel$$3 .readString();
                    map$$0 .put(string$$0, string$$1);
                }
            }
            offer$$3 .param = map$$0;
            offer$$3 .price = parcel$$3 .readString();
            offer$$3 .pictureUrl = parcel$$3 .readString();
            offer$$3 .name = parcel$$3 .readString();
            offer$$3 .description = parcel$$3 .readString();
            offer$$3 .id = parcel$$3 .readInt();
            offer$$3 .url = parcel$$3 .readString();
            offer$$3 .categoryId = parcel$$3 .readInt();
            return offer$$3;
        }
    }

    public final static class Creator$$1
        implements Creator<Offer$$Parcelable>
    {


        @Override
        public Offer$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Offer$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public Offer$$Parcelable[] newArray(int size) {
            return new Offer$$Parcelable[size] ;
        }

    }

}
