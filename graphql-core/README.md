# jGQL - Java GraphQL Toolkit

By Theo Zourzouvillys <theo@zrz.io>

Pronounced 'jay-gee-quil'

This toolkit contains a lightweight dependency-free [http://graphql.org/|GraphQL] model, type & value system along with a framework for defining schemas (and their execution counterparts) in POJOs, a schema generator, and client binding generation tools.

## Schemas

Schemas can either be written by hand, or more preferabbly by introspecting code

jql-core provides tooling to generate a GraphQL schema from a set of java classes.  The jgql tool has an 'extract' subcommand which takes a classpath and set of class names, and will generate a schema from it.

The schema can be extracted at build time using a gradle or maven plugin, or running jgql manually.

## Client Generation

One of the goals of the JGQL infrastructure is to decouple the consumers from the producers.  Current REST/JSON models are brittle, and requiring a different JSON view for each client usage is horrible.  Having a single JSON model with the kitchen sink loaded in sucks just as much.  Commically, SQL actually makes a pretty nice interface from a theoretical point of view.  It's actually not so bad for a bunch of reader clients, but problems exist with the tooling and the fact we don't always want to query data stored in SQL - most of our real time infrastruture is stored in memory only.

Ideally, clients only fetch the data they need.  The usages should be typesafe, and any problems with the schema should be detected at both compile time and run time.  jGQL does this.  You pass jgql a schema and a set of queries, and it validates them and generates your client stubs.  The stubs are type safe.

On startup, you bind a stub to a transport, and it connects to the server and validates the queries are still valid.

Currently, only Java bindings are generated.  However, it should be trivial to add support for other languages to the generator.

## Transports

Target implementations:

- HTTP
- gRPC
- WebSocket

### Adding other transports

it should be pretty easy to add other transports.  Things that need be considered:

- Authentication
- Security
- Prepared queries
- Subscriptions

## Encodings

Currently, only JSON is supported.  Future work may add protobufs, avro, or other encodings.

# Example


```


@GQLType
public class FarmQueryRoot
{

  @GQLField
  public int getCounter()
  {
    return 1234;
  }

  @GQLField
  public @GQLNonNull List<@GQLNonNull Animal> getAnimals()
  {
    return Lists.newArrayList(
        new Animal("cow", "cows", "moo"),
        new Animal("pig", "pigs", "oink"),
        new Animal("sheep", "sheep", "baah"),
        new Animal("horse", "horses", "neigh"),
        new Animal("fox", "foxes", "Ring-ding-ding-ding-dingeringeding!"));
  }

}



@GQLType
public class Animal
{

  private String singular;
  private String plural;
  private String sound;

  public Animal(String singular, String plural, String sound)
  {
    this.singular = singular;
    this.plural = plural;
    this.sound = sound;
  }

  
  @GQLField
  public @GQLNonNull String getName(@GQLArg("plural") @GQLDefaultValue("false") boolean plural)
  {
    return (plural) ? this.plural : this.singular;
  }

  @GQLField
  public @GQLNonNull String getSound()
  {
    return this.sound;
  }

}
```

This automatically generates a GraphQL schema:

```
type Animal {
  name(plural: Boolean! = false): String!
  sound: String!
}

type FarmQueryRoot {
  animals: [Animal!]!
  counter: Int!
}
```

and querying it:

```
GQLValue value = BindingExecution.query(
	new FarmQueryRoot(),
	"{ counter: animals { name(plural: false), sound }}");

System.out.println(JsonValueWriter.toJSONString(value));
```

results in the instance passed in being queried:

```
{
  "counter" : 1234,
  "animals" : [ {
    "name" : "cow",
    "sound" : "moo"
  }, {
    "name" : "pig",
    "sound" : "oink"
  }, {
    "name" : "sheep",
    "sound" : "baah"
  }, {
    "name" : "horse",
    "sound" : "neigh"
  }, {
    "name" : "fox",
    "sound" : "Ring-ding-ding-ding-dingeringeding!"
  } ]
}
```


## Execution / Runtime

This library limits it's scope to the parser, model representation, and type/value system defined by GraphQL along with an execution framework.  It does not provide any concrete implementations - that's up to the consumer to implement the logic in POJOs.


## Deviations from GraphQL Specification

- single quotes are accepted for string values.


