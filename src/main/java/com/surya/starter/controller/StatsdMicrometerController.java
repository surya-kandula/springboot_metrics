package com.surya.starter.controller;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/micrometer/statsd")
public class StatsdMicrometerController {

    @GetMapping("/registry")
    public String sendMetricUsingStatsdMeterRegistry() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);
        statsdMeterRegistry.counter("statsdMeterRegistry.counter").increment();
        return "sent metric using MeterRegistry.counter()";
    }

    @GetMapping("/counter")
    public String sendMetricUsingMicrometerCounter() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);

        Counter counter = Counter
                .builder("micrometer.counter")
                .register(statsdMeterRegistry);
        counter.increment();

        return "sent metric using Micrometer.Counter()";
    }

    @GetMapping("/metrics")
    public String sendMetricUsingMicrometerMettrics() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);

        Metrics.addRegistry(statsdMeterRegistry);
        Metrics.counter("micrometer.metrics").increment();;

        return "sent metric using MeterRegistry.Metrics()";
    }

    @GetMapping("/composite")
    public String sendMetricUsingCompositeMeterRegistry() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry().add(statsdMeterRegistry);

        compositeRegistry.counter("compositeRegistry.counter").increment();;

        return "sent metric using CompositeMeterRegistry.Counter()";
    }

    private StatsdConfig getStatsdConfig() {
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
