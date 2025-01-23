//package com.example.demo.config;
//
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//
//public class CamundaConfig implements ConnectorConfigurator<HttpConnector> {
//    public Class<HttpConnector> getConnectorClass() {
//        return HttpConnector.class;
//    }
//
//    public void configure(HttpConnector connector) {
//        CloseableHttpClient client = HttpClients.custom()
//                .setMaxConnPerRoute(10)
//                .setMaxConnTotal(200)
//                .build();
//        ((AbstractHttpConnector) connector).setHttpClient(client);
//    }
//}
//
