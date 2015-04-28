package nz.co.scuff.android.util;

import com.activeandroid.serializer.TypeSerializer;
//import com.activeandroid.util.SQLiteUtils;

import java.sql.Timestamp;

/**
 * Created by Callum on 28/04/2015.
 */
final public class TimestampSerializer extends TypeSerializer {

    public TimestampSerializer() {}

    @Override
    public Class<?> getDeserializedType() {
        return Timestamp.class;
    }

/*    @Override
    public TypeSerializer.SerializedType getSerializedType() {
        return SerializedType.LONG;
    }*/

/*    @Override
    public SQLiteType getSerializedType() {
        return SQLiteUtils.SQLiteType.INTEGER;
    }*/

    @Override
    public Class<?> getSerializedType() {
        return Long.class;
    }

    @Override
    public Long serialize(Object data) {
        if (data == null) {
            return null;
        }

        return ((Timestamp) data).getTime();
    }

    @Override
    public Timestamp deserialize(Object data) {
        if (data == null) {
            return null;
        }

        return new Timestamp((Long) data);
    }
}