package nz.co.scuff.android.ui;

import android.os.Bundle;
import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetainedFragment extends Fragment {

    // data object we want to retain
    private JourneyState data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(JourneyState data) {
        this.data = data;
    }

    public JourneyState getData() {
        return data;
    }

}
