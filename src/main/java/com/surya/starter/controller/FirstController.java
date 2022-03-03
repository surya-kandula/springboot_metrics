package com.surya.starter.controller;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/first")
public class FirstController {

    @GetMapping("/first")
    public String firstMethod() {
        StatsdConfig config = new StatsdConfig() {
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
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(config, Clock.SYSTEM);
        Counter counter = Counter
                .builder("counter_example")
                .register(statsdMeterRegistry);
        counter.increment();

        Metrics.addRegistry(statsdMeterRegistry);
        Metrics.counter("via_metrics").increment();;

        statsdMeterRegistry.counter("testINg_from_spring").increment();

        try {
            throw new IOException("Io Exception");
        } catch(Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getClass().getName());
            return e.getClass().getName();
        }
//        return "I am first method from first controller";
    }
}
