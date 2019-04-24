/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.examples.quickstart.se;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.common.configurable.Resource;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.security.Principal;
import io.helidon.security.SecurityContext;
import io.helidon.security.Subject;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.JwkKeys;
import io.helidon.security.jwt.jwk.JwkOctet;
import io.helidon.security.providers.httpauth.UserStore.User;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 * A simple service to greet you. Examples:
 *
 * Get default greeting message: curl -X GET http://localhost:8080/greet
 *
 * Get greeting message for Joe: curl -X GET http://localhost:8080/greet/Joe
 *
 * Change greeting curl -X PUT -H "Content-Type: application/json" -d
 * '{"greeting" : "Howdy"}' http://localhost:8080/greet/greeting
 *
 * The message is returned as a JSON object
 */

public class GreetService implements Service {

    /**
     * The config value for the key {@code greeting}.
     */
    private String greeting;

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    GreetService(Config config) {
        this.greeting = config.get("app.greeting").asString().orElse("Ciao");
    }

    /**
     * A service registers itself by updating the routine rules.
     * 
     * @param rules the routing rules.
     */
    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", WebSecurity.authenticator("userauth"), this::getDefaultMessageHandler)
            .get("/new", this::newGreet)
            .get("/{name}", WebSecurity.authenticator("jwt"), this::getMessageHandler)
            // .get("/{name}", this::getMessageHandler)
            .put("/greeting", this::updateGreetingHandler);
    }

    private void newGreet(ServerRequest request, ServerResponse response) {
        String audience = "Customer2";
        String subject = "54564645646465";
        String username = "adk";
        String issuer = "helidon";
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);
        Instant notBefore = now.minus(2, ChronoUnit.SECONDS);

        Jwt jwt = Jwt.builder()
            .jwtId(UUID.randomUUID().toString())
            .keyId("JWT")
            .addScope("link")
            .addScope("lank")
            .addScope("lunk")
            .subject(subject)
            .algorithm(JwkOctet.ALG_HS256)
            .audience(audience)
            .issuer(issuer)
            // time info
            .issueTime(now)
            .expirationTime(expiration)
            .notBefore(notBefore)
            .build();

        System.out.println(jwt.headerJson());
        System.out.println(jwt.payloadJson());

        JwkKeys customKeys = JwkKeys.builder()
            .resource(Resource.create("helidon-jwk.json"))
            .build();
        SignedJwt signed = SignedJwt.sign(jwt, customKeys);

        String token = signed.tokenContent();

        JsonObject returnObject = JSON.createObjectBuilder()
                .add("token", token)
                .build();
        response.send(returnObject);
    }

    /**
     * Return a wordly greeting message.
     * @param request the server request
     * @param response the server response
     */
    private void getDefaultMessageHandler(ServerRequest request, ServerResponse response) {
        Optional<SecurityContext> securityContext = request.context().get(SecurityContext.class);
        sendResponse(response, securityContext.get().userName() + " World");
    }

    /**
     * Return a greeting message using the name that was provided.
     * @param request the server request
     * @param response the server response
     */
    private void getMessageHandler(ServerRequest request, ServerResponse response) {
        Optional<SecurityContext> securityContext = request.context().get(SecurityContext.class);
        System.out.println("sub: " + securityContext.get().userName());
        System.out.println("iss: " + securityContext.get().userPrincipal().get().abacAttribute("iss").get());
        System.out.println("user: " + securityContext.get().user().get());
        // MyUser myUser = (MyUser) securityContext
        // .map(SecurityContext::user).orElseThrow()
        // .flatMap(subj -> subj.privateCredential(User.class)).orElseThrow();
        // System.out.println("tenant: " + myUser.tenant());

        // String name = "Hello, you are: \n" + securityContext
        //     .map(ctx -> ctx.user().orElse(SecurityContext.ANONYMOUS).toString())
        //     .orElse("Security context is null");
        String name = request.path().param("name");
        sendResponse(response, name);
    }

    private void sendResponse(ServerResponse response, String name) {
        String msg = String.format("%s %s!", greeting, name);

        JsonObject returnObject = JSON.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }

    private void updateGreetingFromJson(JsonObject jo, ServerResponse response) {

        if (!jo.containsKey("greeting")) {
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                    .add("error", "No greeting provided")
                    .build();
            response.status(Http.Status.BAD_REQUEST_400)
                    .send(jsonErrorObject);
            return;
        }

        greeting = jo.getString("greeting");
        response.status(Http.Status.NO_CONTENT_204).send();
    }

    /**
     * Set the greeting to use in future messages.
     * @param request the server request
     * @param response the server response
     */
    private void updateGreetingHandler(ServerRequest request,
                                       ServerResponse response) {
        request.content().as(JsonObject.class).thenAccept(jo -> updateGreetingFromJson(jo, response));
    }

}
