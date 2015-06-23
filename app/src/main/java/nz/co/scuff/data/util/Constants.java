package nz.co.scuff.data.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Callum on 5/06/2015.
 */
public interface Constants {

    long JOURNEY_PRE_CREATED_KEY = -1L;
    long WAYPOINT_PRE_CREATED_KEY = -1L;

    long LONG_VALUE_SET_TO_NULL = -1L;

    long LONG_COLLECTION_NOT_RETRIEVED = -1L;
    Set<Long> LONG_COLLECTION_NOT_RETRIEVED_PLACEHOLDER = new HashSet<>(Arrays.asList(new Long[] { LONG_COLLECTION_NOT_RETRIEVED }));

}
