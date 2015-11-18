package io.joss.graphql.client.binder;

import static org.junit.Assert.*;

import org.junit.Test;

import io.joss.graphql.client.binder.MyTestClient.ClientStub;
import io.joss.graphql.client.binder.MyTestClient.GetUserResult;
import io.joss.graphql.client.channel.loopback.LoopbackChannelBuilder;
import io.joss.graphql.client.channel.unirest.UnirestHttpChannelBuilder;
import io.joss.graphql.client.runtime.RuntimeQuery;

public class GQLClientBinderBuilderTest
{

  @Test
  public void test()
  {

    // create our test server stub.
    ClientStub client = GQLClientBinderBuilder
        .forStub(MyTestClient.ClientStub.class)
        .withChannel(LoopbackChannelBuilder.forInstance(new TestServerRoot()).build())
        .build();
    
    // client implementation should return a bound instance.
    assertNotNull(client);
    
    GetUserResult res = client.getUser("theo", "e164").execute();
    
    // make sure the reuslt doesn't bork.
    assertNotNull(res);
    
    res.user().phoneNumbers().edges().forEach(e -> System.err.println(e.node()));
    
    assertNotNull(res.user());
    
  }

}
