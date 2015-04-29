package nz.co.scuff.android.ui;

import android.os.Bundle;
import android.app.Fragment;

import nz.co.scuff.data.journey.Journey;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetainedFragment extends Fragment {

    // data object we want to retain
    private Journey data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Journey data) {
        this.data = data;
    }

    public Journey getData() {
        return data;
    }

}
