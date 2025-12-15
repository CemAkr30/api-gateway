package tr.gov.gib.ebyn.api.gateway.setup;


import static tr.gov.gib.ebyn.api.gateway.constants.AuthConstant.X_API_KEY;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.SecurityProperty;

public abstract class MockServerSetup {

    private ClientAndServer mockServer;

    @Autowired
    protected SecurityProperty securityProperty;

    protected void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    protected void stopMockServer() {
        if(checkMockServer()) {
            mockServer.stop();
        }
    }

    protected void setupMockServer(String path, int statusCode,String method, String apiKey,String body) {
        if (checkMockServer()) {
            mockServer.when(
                    HttpRequest.request()
                            .withMethod(method)
                            .withPath(path)
                            .withHeader(X_API_KEY, apiKey)
                            .withBody(body)
            ).respond(
                    HttpResponse.response()
                            .withStatusCode(statusCode)
            );
        }
    }

    private boolean checkMockServer(){
        return mockServer!=null ? true : false;
    }
}

