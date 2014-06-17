package org.mule.module.apikit.builder;

import static com.jayway.restassured.RestAssured.given;
import static org.mule.module.Apikit.api;
import static org.mule.module.Core.log;
import org.mule.api.MuleException;
import org.mule.module.core.Mule;

import com.jayway.restassured.RestAssured;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestRouterBuilderTest
{


    final int port = 8081;
    final Mule mule = new Mule();

    @Before
    public void setup() throws MuleException
    {

        RestAssured.port = port;
        startApp();
    }


    @After
    public void after()
    {

        try
        {
            mule.stop();
        }
        catch (MuleException e)
        {
            e.printStackTrace();
        }
    }


    @Test
    public void simple() throws MuleException
    {

        given().header("Accept", "*/*").body("hola")
                .expect()
                .response().body(CoreMatchers.containsString("hola"))
                .statusCode(200)
                .when().put("/api/forward");
    }


    private void startApp() throws MuleException
    {

        mule.declare(
                api("rover.raml").baseUri("http://localhost:" + port + "/api")
                        .onPut("/forward").then(log("#[payload]"))
        );
        mule.start();
    }

}
