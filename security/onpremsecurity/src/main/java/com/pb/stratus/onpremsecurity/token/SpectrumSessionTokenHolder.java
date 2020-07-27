package com.pb.stratus.onpremsecurity.token;

/**
 * Created by gu003du on 22-Oct-18.
 */
public class SpectrumSessionTokenHolder {
    private SpectrumToken spectrumToken;

    public SpectrumSessionTokenHolder(SpectrumToken spectrumToken) {
        this.spectrumToken = spectrumToken;
    }

    public SpectrumToken getToken() {
        return this.spectrumToken;
    }
}
