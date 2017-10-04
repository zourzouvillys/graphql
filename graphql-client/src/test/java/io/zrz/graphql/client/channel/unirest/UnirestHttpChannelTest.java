package io.zrz.graphql.client.channel.unirest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.zrz.graphql.client.channel.unirest.UnirestHttpChannelBuilder;
import io.zrz.graphql.client.runtime.GQLChannel;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;

public class UnirestHttpChannelTest
{

  private static class AllocatePort
  {

    public static int allocate()
    {
      try
      {
        ServerSocket sock = new ServerSocket(0);
        try
        {
          return sock.getLocalPort();
        }
        finally
        {
          sock.close();
        }
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }

  }

  @Rule
  // seriously wiremock, no random port number?
  public WireMockRule wireMockRule = new WireMockRule(AllocatePort.allocate());

  @Test
  public void test() throws InterruptedException
  {

    stubFor(post(urlEqualTo("/api/graphql"))
        .withHeader("Accept", equalTo("application/json"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{ \"data\" : { \"a\" : { \"id\" : \"1234\", \"__typename\": \"Tenant\" } } }")));

    // create the HTTP channel.
    GQLChannel channel = UnirestHttpChannelBuilder
        .forUrl("http://localhost:" + Integer.toString(wireMockRule.port()) + "/api/graphql")
        .withHeader("Authorization", "Bearer sxxx")
        .build();

    // the document we're using to query.
    GQLDocument doc = GQLParser.parseDocument("{ a: tenant { id, __typename }  }");

    // execute it - blocking.
    GQLObjectValue value = channel.execute(doc).get();

    verify(postRequestedFor(urlMatching("/api/graphql"))
        .withHeader("Authorization", matching("Bearer sxxx"))
        .withHeader("Content-Type", matching("application/json")));

    assertNotNull(value.entries().get("a"));
    assertEquals("1234", ((GQLStringValue) ((GQLObjectValue) value.entries().get("a")).entries().get("id")).value());

  }

}
