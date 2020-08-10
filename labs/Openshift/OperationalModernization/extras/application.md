# Application Overview

The **Customer Order Services** application is a simple store-front shopping application, built during the early days of the Web 2.0 movement. Users interact directly with a browser-based interface and manage their cart to submit orders. This application is built using the traditional 3-Tier Architecture model, with an HTTP server, an application server, and a supporting database.

![Phase 0 Application Architecture](https://github.com/ibm-cloud-architecture/refarch-jee/raw/master/static/imgs/apparch-pc-phase0-customerorderservices.png)

There are several components of the overall application architecture:
- Starting with the database, the application leverages two SQL-based databases running on IBM DB2.

- The application exposes its data model through an Enterprise JavaBean layer, named **CustomerOrderServices**. This component leverages the Java Persistence API to expose the backend data model to calling services with minimal coding effort.

  - This build of the application uses JavaEE6 features for EJBs and JPA.

- The next tier of the application, named **CustomerOrderServicesWeb**, exposes the necessary business APIs via REST-based web services. This component leverages the JAX-RS libraries for creating Java-based REST services with minimal coding effort.

  - This build of the application is using **JAX-RS 1.1** version of the respective capability.

- The application's user interface is exposed through the **CustomerOrderServicesWeb** component as well, in the form of a Dojo Toolkit-based JavaScript application. Delivering the user interface and business APIs in the same component is one major inhibitor our migration strategy will help to alleviate in the long-term.

- Finally, there is an additional integration testing component, named **CustomerOrderServicesTest** that is built to quickly validate an application's build and deployment to a given application server. This test component contains both **JPA** and **JAX-RS**-based tests.