package com.adocat.adocat_api.config;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.sid}")
    private String verifyServiceSid;

    public void sendVerificationCode(String phoneNumber) {
        Twilio.init(accountSid, authToken);
        Verification.creator(verifyServiceSid, phoneNumber, "sms").create();
    }

    public boolean verifyCode(String phoneNumber, String code) {
        Twilio.init(accountSid, authToken);
        VerificationCheck verification = VerificationCheck.creator(verifyServiceSid)
                .setTo(phoneNumber)
                .setCode(code)
                .create();

        return "approved".equals(verification.getStatus());
    }
}
