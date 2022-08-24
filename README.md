## Summary

TUI DX Backend technical Test v2

This project is implementation of pilotes orders REST api.

It allows the user to:
<ul>
    <li>Create a pilotes order, choosing between 5, 10 or 15 pilotes.</li>
    <li>Update a pilotes order. During the 5 minutes following the creation of the order it will be
  allowed to update the order data; after that time it will not be possible to modify any data of
  the order because Miquel will be occupied cooking thepilotes</li>
    <li>Search orders by customer data. Allow partial searches: e.g., all orders of customers whose
  name contains an “a” in their name.</li>
</ul>

Search feature requires authentication, mocked user credentials are:
username: 'user'
password: 'pass123'

There are two type of test in the project Integration test (that are slower and are checking the system components cooperation) and unit tests that serve as design feedback and maintenance utility.

Java version required to run the project is 11, the distribution used was Amazon Correto 11.0.5.
<br>
Application will run by default on http://localhost:8080, you can access the swagger under http://localhost:8080/swagger-ui/index.html#
and h2 emmbeded db  console at the: http://localhost:8080/h2-console/, the username is 'sa' and the password '123'.

There are three layers of the application where the business domain is implemented in 'orders' package, it communicates with the db through PersistanceAdapter and exposes the data through 'web' layer by REST api controllers.

<img src='architecture.drawio.png'>
