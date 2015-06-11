package nz.co.scuff.data.base;

import nz.co.scuff.data.base.snapshot.Snapshot;

/**
 * Created by Callum on 4/06/2015.
 */
public interface Snapshotable {

    Snapshot toSnapshot();

}
