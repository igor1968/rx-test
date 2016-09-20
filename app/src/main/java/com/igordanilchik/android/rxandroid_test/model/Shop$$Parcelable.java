
package com.igordanilchik.android.rxandroid_test.model;

import android.os.Parcelable;

import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

import java.util.ArrayList;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2016-08-04T16:18+0300")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Shop$$Parcelable
    implements Parcelable, ParcelWrapper<com.igordanilchik.android.rxandroid_test.model.Shop>
{

    private com.igordanilchik.android.rxandroid_test.model.Shop shop$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Shop$$Parcelable.Creator$$2 CREATOR = new Shop$$Parcelable.Creator$$2();

    public Shop$$Parcelable(com.igordanilchik.android.rxandroid_test.model.Shop shop$$2) {
        shop$$0 = shop$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(shop$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.igordanilchik.android.rxandroid_test.model.Shop shop$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(shop$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(shop$$1));
            if (shop$$1 .offers == null) {
                parcel$$1 .writeInt(-1);
            } else {
                parcel$$1 .writeInt(shop$$1 .offers.size());
                for (com.igordanilchik.android.rxandroid_test.model.Offer offer$$0 : ((java.util.List<com.igordanilchik.android.rxandroid_test.model.Offer> ) shop$$1 .offers)) {
                    com.igordanilchik.android.rxandroid_test.model.Offer$$Parcelable.write(offer$$0, parcel$$1, flags$$0, identityMap$$0);
                }
            }
            if (shop$$1 .categories == null) {
                parcel$$1 .writeInt(-1);
            } else {
                parcel$$1 .writeInt(shop$$1 .categories.size());
                for (com.igordanilchik.android.rxandroid_test.model.Category category$$0 : ((java.util.List<com.igordanilchik.android.rxandroid_test.model.Category> ) shop$$1 .categories)) {
                    com.igordanilchik.android.rxandroid_test.model.Category$$Parcelable.write(category$$0, parcel$$1, flags$$0, identityMap$$0);
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.igordanilchik.android.rxandroid_test.model.Shop getParcel() {
        return shop$$0;
    }

    public static com.igordanilchik.android.rxandroid_test.model.Shop read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.igordanilchik.android.rxandroid_test.model.Shop shop$$3;
            int reservation$$0 = identityMap$$1 .reserve();
            shop$$3 = new com.igordanilchik.android.rxandroid_test.model.Shop();
            identityMap$$1 .put(reservation$$0, shop$$3);
            int int$$0 = parcel$$3 .readInt();
            ArrayList<com.igordanilchik.android.rxandroid_test.model.Offer> list$$0;
            if (int$$0 < 0) {
                list$$0 = null;
            } else {
                list$$0 = new ArrayList<com.igordanilchik.android.rxandroid_test.model.Offer>(int$$0);
                for (int int$$1 = 0; (int$$1 <int$$0); int$$1 ++) {
                    com.igordanilchik.android.rxandroid_test.model.Offer offer$$1 = com.igordanilchik.android.rxandroid_test.model.Offer$$Parcelable.read(parcel$$3, identityMap$$1);
                    list$$0 .add(offer$$1);
                }
            }
            shop$$3 .offers = list$$0;
            int int$$2 = parcel$$3 .readInt();
            ArrayList<com.igordanilchik.android.rxandroid_test.model.Category> list$$1;
            if (int$$2 < 0) {
                list$$1 = null;
            } else {
                list$$1 = new ArrayList<com.igordanilchik.android.rxandroid_test.model.Category>(int$$2);
                for (int int$$3 = 0; (int$$3 <int$$2); int$$3 ++) {
                    com.igordanilchik.android.rxandroid_test.model.Category category$$1 = com.igordanilchik.android.rxandroid_test.model.Category$$Parcelable.read(parcel$$3, identityMap$$1);
                    list$$1 .add(category$$1);
                }
            }
            shop$$3 .categories = list$$1;
            return shop$$3;
        }
    }

    public final static class Creator$$2
        implements Creator<Shop$$Parcelable>
    {


        @Override
        public Shop$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Shop$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public Shop$$Parcelable[] newArray(int size) {
            return new Shop$$Parcelable[size] ;
        }

    }

}
