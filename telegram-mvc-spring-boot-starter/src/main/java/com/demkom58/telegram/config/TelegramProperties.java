package com.demkom58.telegram.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties("telegram")
@Data
public class TelegramProperties {
    @Nullable
    private String externalUrl;
    @Nullable
    private String internalUrl;
    @Nullable
    private String keyStore;
    @Nullable
    private String keyStorePassword;
    @Nullable
    private String pathToCertificate;

    public boolean hasKeyStore() {
        return StringUtils.hasText(keyStore) || StringUtils.hasText(keyStorePassword);
    }

    public boolean hasInternalUrl() {
        return StringUtils.hasText(internalUrl);
    }
}
