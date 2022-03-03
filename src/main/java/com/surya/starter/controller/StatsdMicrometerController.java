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

    // Does not work
    // TODO: Why is it not working ?
    @GetMapping("/registry")
    public String sendMetricUsingStatsdMeterRegistry() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);
        // Doesn't send any metrics to Statsd
        statsdMeterRegistry.counter("statsdMeterRegistry.counter").increment();
        return "sent metric using MeterRegistry.counter()";
    }

    // Does not work
    @GetMapping("/counter")
    public String sendMetricUsingMicrometerCounter() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);

        Counter counter = Counter
                .builder("micrometer.counter")
                .register(statsdMeterRegistry);
        // Doesn't send any metrics to Statsd
        counter.increment();

        return "sent metric using Micrometer.Counter()";
    }

    // From Metrics documentation, they suggest us to use Metrics in places where we cant inject MeterRegistry
    // TODO: How is Metrics able to push metrics from StatsdMeterRegistry to Statsd
    // This does not work in case we are running inside a main method (without a server) event with 20s sleep
    // Somehow Micrometer is able to push metrics while a server is running
    @GetMapping("/metrics")
    public String sendMetricUsingMicrometerMettrics() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);

        Metrics.addRegistry(statsdMeterRegistry);
        Metrics.counter("micrometer.metrics").increment();;

        return "sent metric using MeterRegistry.Metrics()";
    }

    // Does not work.
    @GetMapping("/composite")
    public String sendMetricUsingCompositeMeterRegistry() {
        MeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(getStatsdConfig(), Clock.SYSTEM);
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry().add(statsdMeterRegistry);

        // Doesn't send any metrics to Statsd
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
