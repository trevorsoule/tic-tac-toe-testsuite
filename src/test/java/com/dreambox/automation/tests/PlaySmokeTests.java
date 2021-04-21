package com.dreambox.automation.tests;

import com.dreambox.automation.resources.Constants;
import com.dreambox.automation.service.TicTacToeAPI;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import org.apache.commons.lang3.ObjectUtils;
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
        response = ticTacToeAPI.gameMarkSquareAPI(requestSpec, sessionId, token, initialValue);
        String[][] boardLayout = response.as(String[][].class);
        Assert.assertTrue(validateMove(boardLayout, initialValue));

        response = ticTacToeAPI.gameGetState(requestSpec, sessionId, token);
        Assert.assertEquals(response.jsonPath().get("status"), Constants.IN_PROGRESS);
    }

    // This test will occasionally fail due to the Draw bug in the API
    @Description("Make moves to complete a game of tic-tac-toe and assert results to assert the correct move and status")
    @Test(groups = { "smoke" })
    public void testCompletedTicTacToeGame() {
        String testUser = "test@test.com";

        response = ticTacToeAPI.gameLoginAPI(requestSpec, testUser);
        String sessionId = response.jsonPath().get("session_id");

        response = ticTacToeAPI.gameCreateAPI(requestSpec, sessionId);
        String token = response.jsonPath().get("token");

        Random r = new Random();
        String[][] boardLayout = {};
        int initialValue = r.nextInt(9);
        for (int i=0; i<9; i++) {
            response = ticTacToeAPI.gameMarkSquareAPI(requestSpec, sessionId, token, initialValue);
            boardLayout = response.as(String[][].class);
            Assert.assertTrue(validateMove(boardLayout, initialValue), "expected " + initialValue);
            initialValue = findNewMove(boardLayout);
            if (checkGameState(sessionId, token)) {
                break;
            }
        }

        response = ticTacToeAPI.gameGetState(requestSpec, sessionId, token);
        Assert.assertNotEquals(response.jsonPath().get("status"), Constants.IN_PROGRESS);
    }

    public boolean checkGameState(String sessionId, String token) {
        boolean gameState = false;
        response = ticTacToeAPI.gameGetState(requestSpec, sessionId, token);
        response.prettyPrint();
        if (!response.jsonPath().get("status").equals(Constants.IN_PROGRESS)) {
            gameState = true;
        }
        return gameState;
    }

    public int findNewMove(String[][] array) {
        int moveCount = 0;
        outerloop:
        for (String[] n : array) {
            for (String q : n) {
                if (ObjectUtils.compare(q, null) == 0) {
                    break outerloop;
                }
                moveCount++;
            }
        }
        return moveCount;
    }

    public boolean validateMove(String[][] array, int moveMade) {
        boolean value = false;
        int moveCount = 0;
        outerloop:
        for (String[] n : array) {
            for (String q : n) {
                if (ObjectUtils.compare(q, null) == 1 && ObjectUtils.compare(q.toLowerCase(), "x") == 0) {
                    if (moveCount == moveMade) {
                        value = true;
                        break outerloop;
                    }
                }
                moveCount++;
            }
        }
        return value;
    }
}