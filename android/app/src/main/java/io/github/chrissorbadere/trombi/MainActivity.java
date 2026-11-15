package io.github.chrissorbadere.trombi;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(VerrouPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
