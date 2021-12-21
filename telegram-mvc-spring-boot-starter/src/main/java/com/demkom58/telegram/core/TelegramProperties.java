package com.demkom58.telegram.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties("telegram")
@Data
public class TelegramProperties {
    private String externalUrl;
    private String internalUrl;
    private String keyStore;
    private String keyStorePassword;
    private String pathToCertificate;

    public boolean hasKeyStore() {
        return StringUtils.hasText(keyStore) || StringUtils.hasText(keyStorePassword);
    }

    public boolean hasInternalUrl() {
        return StringUtils.hasText(internalUrl);
    }
}
