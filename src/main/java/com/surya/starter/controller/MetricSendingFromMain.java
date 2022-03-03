package com.surya.starter.controller;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/micrometer/statsd")
public class MetricSendingFromMain {

    // Does not work
    public static void main(String[] args) {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);

        Metrics.addRegistry(statsdMeterRegistry);
        Metrics.counter("micrometer.metrics.main").increment();;
        Metrics.globalRegistry.close();
        try {
            System.out.println("sleeping now");
            Thread.sleep(20000);
        } catch (Exception e) {
            System.out.println("Sleep interrupted");
        }
    }

    private static StatsdConfig getStatsdConfig() {
        return new StatsdConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String host() {
                return "localhost";
            }

            @Override
            public StatsdFlavor flavor() {
                return StatsdFlavor.DATADOG;
            }
        };
    }
}
