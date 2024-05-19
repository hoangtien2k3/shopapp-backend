package com.hoangtien2k3.shopappbackend.components;

import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class TranslateMessages {

    @Autowired
    private LocalizationUtils localizationUtils;

    // translate message
    protected String translate(String message) {
        return localizationUtils.getLocalizedMessage(message);
    }

    // translate many message
    protected String translate(String message, Object... listMessage) {
        return localizationUtils.getLocalizedMessage(message, listMessage);
    }
}
