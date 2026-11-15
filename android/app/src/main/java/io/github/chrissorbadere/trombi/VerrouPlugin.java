package io.github.chrissorbadere.trombi;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/** Verrouillage de l'appli par empreinte (ou code / schéma du téléphone en secours). */
@CapacitorPlugin(name = "Verrou")
public class VerrouPlugin extends Plugin {

    private static final int AUTH =
            BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    @PluginMethod
    public void disponible(PluginCall call) {
        int r = BiometricManager.from(getContext()).canAuthenticate(AUTH);
        JSObject o = new JSObject();
        o.put("ok", r == BiometricManager.BIOMETRIC_SUCCESS);
        o.put("code", r);
        call.resolve(o);
    }

    @PluginMethod
    public void authentifier(PluginCall call) {
        final FragmentActivity act = (FragmentActivity) getActivity();
        act.runOnUiThread(() -> {
            final boolean[] fini = {false};
            BiometricPrompt prompt = new BiometricPrompt(act, ContextCompat.getMainExecutor(getContext()),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            if (fini[0]) return;
                            fini[0] = true;
                            JSObject o = new JSObject();
                            o.put("ok", true);
                            call.resolve(o);
                        }

                        @Override
                        public void onAuthenticationError(int code, CharSequence msg) {
                            if (fini[0]) return;
                            fini[0] = true;
                            JSObject o = new JSObject();
                            o.put("ok", false);
                            o.put("code", code);
                            o.put("message", msg == null ? "" : msg.toString());
                            call.resolve(o);
                        }
                    });
            BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Trombi")
                    .setSubtitle("Déverrouille pour accéder aux informations sur tes élèves")
                    .setAllowedAuthenticators(AUTH)
                    .build();
            try {
                prompt.authenticate(info);
            } catch (Exception e) {
                if (!fini[0]) {
                    fini[0] = true;
                    JSObject o = new JSObject();
                    o.put("ok", false);
                    o.put("code", -1);
                    o.put("message", e.getMessage());
                    call.resolve(o);
                }
            }
        });
    }
}
