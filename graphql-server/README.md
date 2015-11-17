# jGQL Execution Engine

IMplements an asynchronous selection and execution engine that binds to a Java object model.

## Internal Details


### Pre-Planning

The initial GQL query is prepared by passing the type registry along with a query to DataContexts.  This creates a hierarchical model that represents the query itself, and validates it to ensure all required fields, leafs, etc are selected and the query doesn't try to select something that doesn't exist, or requires specific parameters which aren't declared on the query.

The DataContext doesn't represent any specific way of executing the data - it is just a representation of the query itself that has attached all of it's metadata.

Note that the preparation at this point only relies on the GQLDocument, not any parameter values themselves.  This means that the above  can be cached for all requests.

## Execution Planning

Next, we build an execution plan.  This involves taking an incoming DataContext and generating an instance of PreparedQuery.

Exactly how we do this depends on the planner implementation.  The "basic" one executes in serial, without any up-front calculations.  More advanced and efficient execution planners should be added to handle different runtime needs.

## Object Model

Rather than directly bind against java classes, an Object Model is used which initially has a Java binding implementation - where each class represents a single GraphQL type.

However, some tools may wish to generate their own models at runtime - it would be annoying to have to generate classes dynamically.  Instead, they may implement the OutputXXX interfaces for modeling their own implementations.  This may, for exmaple, represent tables within a database.

### Invocation API

The invocation API doesn't directly use java methods.  Methods may be defined in different ways, so we abstract the input/output behaviour of a field to InputObserver and OutputObserver.

These initially odd looking interfaces are to allow field handlers to have a single invocation for all child nodes within a specific selection in a query.  It allows (for example) a single SQL query to be generated for all instances of a selection.

Imagine a query which fetches friends of friends:

  {
    myfriends: friends {
      fof: friends {
        id
      }
    } 
  } 

If we have a simple execution model where the Friends java implementation has a getFriends() call, this would be called 10 times if i have 10 friends for the "myfriends" field.  Then, for each of my friends, "fof" will be called.  if each of my friends has 10 friends, this would result in 100 calls.

instead, with Input and Output observers, the Friends implementation can have a (static) method which takes all of the previous values, and returns the next set of values.  So, Friends.getFriends() would only be called once - with the list of Friend instances.

The invocation API hides the different java implementations - some may accept a list and return a list.  others may accept a list and an OutputObserver.  Others may accept just an InputObserver.  Some may not be static and need to be called one at a time.  The execution planners don't need to know about this, and just rely on OutputFieldHandler.invoke() which always uses the Input/OutputObserver style.






