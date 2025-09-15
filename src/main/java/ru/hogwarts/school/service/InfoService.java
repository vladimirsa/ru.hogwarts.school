package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

@Service
public class InfoService {
    @Value("${server.port}")
    private int port;

    public int getPort() {
        return port;
    }

    public int getSum() {
        return Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, (a, b) -> a + b);
    }
}