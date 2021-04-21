package com.dreambox.automation.tests;

import com.dreambox.automation.resources.Constants;
import com.dreambox.automation.service.TicTacToeAPI;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

public class PlaySmokeTests {
    RequestSpecBuilder requestSpecBuilder;
    RequestSpecification requestSpec;
    private Response response;
    TicTacToeAPI ticTacToeAPI;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        ticTacToeAPI = new TicTacToeAPI();
        PreemptiveBasicAuthScheme auth = new PreemptiveBasicAuthScheme();
        auth.setUserName(System.getenv("USERNAME"));
        auth.setPassword(System.getenv("PASSWORD"));
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpec =
                requestSpecBuilder
                        .setAuth(auth)
                        .setUrlEncodingEnabled(false)
                        .build()
                        .log()
                        .all();
    }

    @Description("Make one move in tic-tac-toe and assert results to assert the correct move and status")
    @Test(groups = { "smoke" })
    public void testInProgressTicTacToeGame() {
        String testUser = "test@test.com";

        response = ticTacToeAPI.gameLoginAPI(requestSpec, testUser);
        String sessionId = response.jsonPath().get("session_id");

        response = ticTacToeAPI.gameCreateAPI(requestSpec, sessionId);
        String token = response.jsonPath().get("token");

        Random r = new Random();
        int initialValue = r.nextInt(9);
        System.out.println(initialValue);
        response = ticTacToeAPI.gameMarkSquareAPI(requestSpec, sessionId, token, initialValue);
        JSONArray array = new JSONArray(response.asString());
        Assert.assertTrue(validateMove(array, initialValue));

        response = ticTacToeAPI.gameGetState(requestSpec, sessionId, token);
        Assert.assertEquals(response.jsonPath().get("status"), Constants.IN_PROGRESS);
    }

    public boolean validateMove(JSONArray array, int moveMade) {
        boolean value = false;
        int moveCount = 0;
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (array.getJSONArray(i).get(j).equals("x")) {
                    if (moveCount == moveMade) {
                        value = true;
                        break;
                    }
                }
                moveCount++;
            }
        }
        return value;
    }
}