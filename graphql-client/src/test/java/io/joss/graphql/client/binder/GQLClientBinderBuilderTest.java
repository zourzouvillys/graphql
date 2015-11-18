package io.joss.graphql.client.binder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.joss.graphql.client.channel.loopback.LoopbackChannelBuilder;

public class GQLClientBinderBuilderTest
{

  @Test
  public void test()
  {

    // create our test server stub.
    MyTestClient.ClientStub client = GQLClientBinderBuilder
        .forStub(MyTestClient.ClientStub.class)
        .withChannel(LoopbackChannelBuilder.forInstance(new TestServerRoot()).build())
        .build();
    
    // client implementation should return a bound instance.
    assertNotNull(client);
    
    MyTestClient.GetUserResult res = client.getUser("theo", "e164").execute();
    
    // make sure the reuslt doesn't bork.
    assertNotNull(res);
    
    res.user().phoneNumbers().edges().forEach(e -> System.err.println(e.node()));
    
    assertNotNull(res.user());
    
  }

}
