# Openshift Workshop for WebSphere Users

This information in this repository is designed to help with WebSphere user's journey in application and operations modernization,  to transition to Cloudpak for Applications and Openshift.

For administrators, the goals are:

- Present value of operations modernization from a WebSphere administrator's point of view.
- Explain new concepts and processes in Openshift
- Provide scenario-based guides that map scenarios in WebSphere to similar scenarios in Openshift.
- Provide corresponding scenario based hands-on labs to help WebSphere administrator become proficient in Openshift.

For application developers, the goals are:

- Present the value of application modernization.
- Introduce the user to microservices and new programming models such microprofile.
- Introduce the user to the Liberty and Open Liberty as the runtime for Java microservices.
- Provide scenario based guides on development and containerization with Liberty.

When available, you may use the material here for different venues:

- 1 - 2 hours presentation + short lab
- Half day workshop
- Full day workshop
- Potentially 2-day workshop

The administrator sections are:
- [Why Operations Modernization](Intro.md)
- [Options to Move WebSphere Workload to Cloud](WebSphereCloud.md)
- [Openshift Concepts for WebSphere Administrators](OpenshiftConcepts.md)
- Migrating Applications
- Application and Configuration Management
- Problem Determination and Monitoring
- Security
- High Availability and Scalability
- Disaster Recovery
- Public and Hybrid Cloud
- Devops and CI/CD


## Labs

### Containzerization Labs

- [Introduction to Containerization](labs/Openshift/HelloContainer). Requires an environment with `podman` installed.

### Openshift Labs

These labs require that you have access to a Openshift 4.x environment.

- [Introduction to Container Orchestration using Openshift](labs/Openshift/IntroOpenshift). 


### Liberty Labs

These labs enable you to get started on Liberty quickly. 
The hardware requirement is a laptop or desktop computer.
First, start with these two labs to familiarize yourself with installing and navigating Liberty:

- [Setup Liberty](labs/Liberty/gettingStarted/0_setup)
- [Discover Liberty](labs/Liberty/gettingStarted/1_discover)

Next, pick any lab for further exploration:
- [Simple Development Lab with JSP and JEE Security](labs/Liberty/development/0_SimpleDevelopment) 
- [Migration Toolkit](labs/Liberty/development/1_LibertyMigrationToolkit)
- [JDBC](labs/Liberty/development/2_JDBC)
- [JMS](labs/Liberty/development/3_JMS)
- [Web Cache](labs/Liberty/development/5_WebCache)
- [Web Socket](labs/Liberty/development/6_WebSocket)
- [JAXWS](labs/Liberty/development/7_JAXWS)
- [Java Batch](labs/Liberty/development/8_JavaBatch)
- [Open ID Connect and JWT](labs/Liberty/development/10_OIDC_JWT)
- [Monitoring and Debugging](labs/Liberty/development/11_Monitoring): With Jconsole and other tools.
- [Packaging and Customizing Liberty](labs/Liberty/development/12_Customize): Options to package and run Liberty as a zip file, as a runnable JAR, or customize for containers.

Finally, explore these additional labs:
- [Micro Profile](https://github.com/OpenLiberty/tutorial-microprofile)
- [Open Liberty Guides](https://openliberty.io/guides/)