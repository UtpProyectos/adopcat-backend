package com.adocat.adocat_api.config;
import com.adocat.adocat_api.domain.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenCleanupService {

    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void eliminarTokensExpiradosYUsados() {
        LocalDateTime ahora = LocalDateTime.now();
        int eliminados = tokenRepository.deleteByUsedTrueOrExpirationBefore(ahora);
        if (eliminados > 0) {
            System.out.println("🔁 Tokens eliminados: " + eliminados);
        }
    }
}
