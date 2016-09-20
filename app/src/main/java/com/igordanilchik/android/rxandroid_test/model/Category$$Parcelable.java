
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
public class Category$$Parcelable
    implements Parcelable, ParcelWrapper<com.igordanilchik.android.rxandroid_test.model.Category>
{

    private com.igordanilchik.android.rxandroid_test.model.Category category$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Category$$Parcelable.Creator$$0 CREATOR = new Category$$Parcelable.Creator$$0();

    public Category$$Parcelable(com.igordanilchik.android.rxandroid_test.model.Category category$$2) {
        category$$0 = category$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(category$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.igordanilchik.android.rxandroid_test.model.Category category$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(category$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(category$$1));
            parcel$$1 .writeString(category$$1 .pictureUrl);
            parcel$$1 .writeInt(category$$1 .id);
            parcel$$1 .writeString(category$$1 .title);
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.igordanilchik.android.rxandroid_test.model.Category getParcel() {
        return category$$0;
    }

    public static com.igordanilchik.android.rxandroid_test.model.Category read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.igordanilchik.android.rxandroid_test.model.Category category$$3;
            int reservation$$0 = identityMap$$1 .reserve();
            category$$3 = new com.igordanilchik.android.rxandroid_test.model.Category();
            identityMap$$1 .put(reservation$$0, category$$3);
            category$$3 .pictureUrl = parcel$$3 .readString();
            category$$3 .id = parcel$$3 .readInt();
            category$$3 .title = parcel$$3 .readString();
            return category$$3;
        }
    }

    public final static class Creator$$0
        implements Creator<Category$$Parcelable>
    {


        @Override
        public Category$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Category$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public Category$$Parcelable[] newArray(int size) {
            return new Category$$Parcelable[size] ;
        }

    }

}
