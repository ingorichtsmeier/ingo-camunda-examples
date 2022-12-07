multiinstance-call-activity
=========================

How to use it?
--------------

Two examples for multiinstance sub processes are included here: 
1. Handling a list of items return a new result list.
2. Handling a package containing nested lists without returning a value.

Test with spring beans in `SpringProcessTest`. 

Test for the nested multiinstance is `NestedPackagesTest`.

Both tests are Spring-Boot integration tests and start a Camunda engine with default configuration and Job executor enabled.

Environment Restrictions
------------------------

Built and tested against Camunda BPM version 7.18.0-ee.

tested with a postgresql database.


