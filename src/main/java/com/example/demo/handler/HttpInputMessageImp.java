package com.example.demo.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
public class HttpInputMessageImp implements HttpInputMessage {

    private HttpHeaders headers;
    private InputStream body;

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

}
