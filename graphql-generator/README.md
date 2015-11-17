3 GraphQL Generators

This tool takes a GraphQL schema, and generates "stuff" from it.  "Stuff" generally is client bindings, but could be Java interfaces, javascript, JSON, etc.

As a example, it's used to read a GraphQL query and generate a type-safe client that has verified the data shape from the schema.  Annotations within the query define the class names and method names.  The tool can be used to generate source code via a gradle plugin, library, or command line.  A Jive Bebop plugin is available for generateing a full client (API + implementaion) from a single GraphQL query and schema location (which may be a live server or file).

The relay spec is used for generation of paging of datasets. It identifies Relay connections and does "the right thing" - The client fetching strategy can be defined on a per connection basis - do you want to fetch a limited number, all of them at once? Provide async methods to fetch them on demand?  Cache them on the client side?  The client knows which fields have been fetched and which haven't, so fetching new data can be minimised where possible.

## Theory


The idea is to have each client responsible for generating it's own bindings.  The contract between the server and the client is the GrpahQL schema, which may be fetched directly from a server, or included in some other tooling (such as git).

Rather than define explicit version numbers on the API, the client is able to detect if there are any API inconsistencies which can't be resolved at runtime (e.g, moving from returning a string instead of an integer).  This allows for validation of dependencies without the overhead of sharing java models in jars between loosely coupled endpoints (although API providers are free to provide clients if they really wish).

Further work could introduce an internal "consumer registry", where internal projects can register their queries against a server for developers to know who to talk to before breaking stuff (or, just log the GQL queries that are received ...). 


## Usage

There are tow options: writing java interfaces and scalars directly (verbose, boring, a waste of time, and error prone), or the preferred way of writing a GraphQL query and letting the code generator create your interfaces for you:

First, define your queries in a file:

```[java]

query fetchUsers ($userSearch: String, $numberFormat : NumberDialStringFormat) {

  users(q: $userSearch) {

    edges {
      node {
        ... User
      }
    }
  
  }

}

fragment User {
  id,
  fullName: name {
    firstName,
    lastName
  }
  age,
  phoneNumbers {
    totalCount,
    edge {
       ... UserPhoneNumber
    }
  }
}

fragment UserPhoneNumber {
  id,
  dialstring(format: $numberFormat) {
    number,
    name
  }
  ... on NanpaNumber {
    callerName
  }
}

```


To create a client API which includes only the types and fields specified in the query: 

  jgraphql-gen -type java-client -schema path/to/graphql.schema my/query/file.gql -clientName com.jive.myproject.clients.Users
  
  
This results in something looking like this:


```

interface Users {

  Collection<User> fetchUsers(String userSearch, NumberDialStringFormat numberFormat);

}

class UsersScalars {

  public static final interface UserFullName {
    String getFirstName();
    String getLastName();
  }
  
  public static enum NumberDialStringFormat {
    E164, NANPA, MX, GB, BR, IN
  }

}

interface UserPhoneNumber {
  String getNumber();
  String getName();
}

interface NanpaUserPhoneNumber extends UserPhoneNumber {
  String getCallerName();
}

interface User {
  String getId();
  UsersFullName getFullName();
  int getAge();
  Collection<PhoneNumber> getPhoneNumbers();
}

```


Such a model is then bound to a client transport, which performs the actual execution.  Note the seperation is to allow evolving clients without needing to re-generate client interfaces.


## TODO

- Better (dynamic) paging support.
- Play around with some interesting optimisations - data shape awareness on both sides means we could do some pretty effective comrpession and parsing optimisations.
