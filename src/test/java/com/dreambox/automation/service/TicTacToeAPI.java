package com.dreambox.automation.service;

import com.dreambox.automation.utils.UrlBuilder;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TicTacToeAPI {

    public Response gameLoginAPI(RequestSpecification requestSpec, String testUser) {
        Response response = RestAssured.given(requestSpec)
                .param("username", testUser)
                .post(UrlBuilder.gameLogin());
        assert response.statusCode() == 200 : "Game Login failed with response code " + response.statusCode();
        return response;
    }

    public Response gameCreateAPI(RequestSpecification requestSpec, String sessionId) {
        Response response = RestAssured.given(requestSpec)
                .get(UrlBuilder.gameCreate(sessionId));
        assert response.statusCode() == 200 : "Game Create failed with response code " + response.statusCode();
        return response;
    }

    public Response gameMarkSquareAPI(RequestSpecification requestSpec, String sessionId, String token, Integer index) {
        Response response = RestAssured.given(requestSpec)
                .put(UrlBuilder.gameMarkSquare(sessionId, token, index));
        assert response.statusCode() == 200 : "Game Market Square failed with response code " + response.statusCode();
        return response;
    }

    public Response gameGetState(RequestSpecification requestSpec, String sessionId, String token) {
        Response response = RestAssured.given(requestSpec)
                .get(UrlBuilder.gameGetState(sessionId, token));
        assert response.statusCode() == 200 : "Game Get State failed with response code " + response.statusCode();
        return response;
    }
}