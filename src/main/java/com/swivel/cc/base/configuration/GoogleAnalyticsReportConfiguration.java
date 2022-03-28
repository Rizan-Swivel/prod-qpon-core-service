package com.swivel.cc.base.configuration;


import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.swivel.cc.base.exception.QponCoreException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleAnalyticsReportConfiguration {

    /**
     * Initializes an Analytics data api v1.
     *
     * @return analytics data client.
     */
    @Bean
    public BetaAnalyticsDataClient createAnalyticsConnection() {
        try {
            return BetaAnalyticsDataClient.create();
        } catch (IOException e) {
            throw new QponCoreException("Initializing google analytics failed", e);
        }
    }
}
