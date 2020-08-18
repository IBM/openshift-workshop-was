# Application Management

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [IBM Application Navigator](#ibm-application-navigator) (Hands-on)
- [Application Monitoring](#application-monitoring) (Hands-on)
- [Day-2 Operations](#day-2-operations) (Hands-on)
- [Summary](#summary)

## Introduction

In the previous labs, you learned to containerize and deploy modernized applications to OpenShift. In this section, you'll learn about managing your running applications efficiently using various tools available to you as part of IBM Cloud Pak for Applications.

## Prerequisites

You must have completed these two labs, and left your applications running:
- [Operational Modernization](../OperationalModernization)
- [Runtime Modernization](../RuntimeModernization)


You'll need the web terminal (same as the one from previous lab). If it's not open, follow the instructions [here](https://github.com/IBM/openshift-workshop-was/tree/master/setup#access-the-web-terminal) to access the web terminal.

You also need to be logged into the OpenShift CLI (command-line interface) using web terminal. Follow the instructions in the [Login section](https://github.com/IBM/openshift-workshop-was/tree/master/labs/Openshift/IntroOpenshift#login) to login to OpenShift CLI.

Clone the GitHub repository with the lab artifacts, **only if you have not already done so**. Run the following commands on your web terminal:
```
git clone https://github.com/IBM/opneshift-workshop-was.git
```


Change to the lab's directory:
```
cd openshift-workshop-was
cd labs/Openshift/ApplicationManagement
ls
```


## IBM Application Navigator

IBM Application Navigator provides a single dashboard to manage your applications running on cloud as well as on-prem, so you don't leave legacy applications behind. 
It gives you an aplication centric view your environment.
Starting with applications, you can drill down to view their dependent resources, such as Deployment, Service, Route, and their state. 
When configured, it also allows you to view your legacy traditional WebSphere ND cells in the same console.

1. In OpenShift console, from the left-panel, select **Networking** > **Routes**.

1. From the _Project_ drop-down list, select `kappnav`. 

1. Click on the route URL (listed under the _Location_ column).

1. Click on `Log in with OpenShift`. Click on `Allow selected permissions`.

1. Notice the list of application(s) you deployed. Click on them to see the resources that are part of the applications.

As you modernize your applications, some workload would still be running on traditional environments. 
IBM Application Navigator, included as part of IBM Cloud Pak for Applications, supports adding WebSphere Network Deployment (ND) environments, so they can be managed from the same dashboard. 
Though out of the scope for this lab, you may import an existing WebSphere Application Server ND cell as a custom resource. 
Application Navigator automatically discovers enterprise applications that are deployed on the cell and creates custom resources to represent those WebSphere ND applications. 
Application Navigator periodically polls the cell to keep the state of the resources synchronized with the cell. 

Application Navigator also provides links to other dashboards that you already use and are familiar with. You can also define your own custom resources using the extension mechanism provided by Application Navigator.

## Application Logging

Pod processes running in OpenShift frequently produce logs. To effectively manage this log data and ensure no loss of log data occurs when a pod terminates, a log aggregation tool should be deployed on the cluster. Log aggregation tools help users persist, search, and visualize the log data that is gathered from the pods across the cluster. Let's look at application logging with log aggregation using EFK (Elasticsearch, Fluentd, and Kibana). Elasticsearch is a search and analytics engine. Fluentd receives, cleans and parses the log data. Kibana lets users visualize data with charts and graphs in Elasticsearch.

### Launch Kibana

1. In OpenShift console, from the left-panel, select **Networking** > **Routes**.

1. From the _Project_ drop-down list, select `openshift-logging`. 

1. Click on the route URL (listed under the _Location_ column).

1. Click on `Log in with OpenShift`. Click on `Allow selected permissions`.

1. The following steps are illustrated  in the screen recording at the end of this section. In Kibana console, from the left-panel, click on `Management`.

1. Click on `Index Patterns`. Click on `project.*`. 
    - This index contains only a set of default fields and does not include all of the fields from the deployed application’s JSON log object. Therefore, the index needs to be refreshed to have all the fields from the application’s log object available to Kibana. 

1. Click on the refresh icon and then click on `Refresh fields`.

    ![refresh index patterns](extras/images/refresh-index-patterns.gif)

### Import dashboards

The following steps to import dashboards into Kibana are illustrated  in the screen recording at the end of this section:

1. Download [zip file](dashboards/dashboards.zip) containing dashboards to your computer and unzip to a local directory. (Look for the `download` button on the page.)

1. Let's import dashboards for Liberty and WAS. From the left-panel, click on `Management`. Click on `Saved Objects` tab and then click on `Import`.

1. Navigate to the _kibana_ sub-directory and select `ibm-open-liberty-kibana5-problems-dashboard.json` file. When prompted, click `Yes, overwrite all` option. It'll take few seconds for the dashboard to show up on the list.

1. Repeat the steps to import `ibm-open-liberty-kibana5-traffic-dashboard.json` and `ibm-websphere-traditional-kibana5-dashboard.json`.

    ![import Kibana dashboards](extras/images/import-kibana-dashboards.gif)

### Explore dashboards

In Kibana console, from the left-panel, click on `Dashboard`. You'll see 3 dashboards on the list. The first 2 are for Liberty. The last one is for WAS traditional. Read the description next to each dashboard.

#### Liberty applications

The following steps to visualize problems with applications are illustrated  in the screen recording below:

1. Click on _Liberty-Problems-K5-20191122_. This dashboard visualizes message, trace and FFDC information from Liberty applications.

1. By default, data from the last 15 minutes are rendered. Adjust the time-range (from the top-right corner), so that it includes data from when you tried the Open Liberty application.

1. Once the data is rendered, you'll see some information about the namespace, pod, containers where events/problems occurred along with a count for each. 

1. Scroll down to `Liberty Potential Problem Count` section which lists the number of ERROR, FATA, SystemErr and WARNING events. You'll likely see some WARNING events.

1. Below that you'll see `Liberty Top Message IDs`. This helps to quickly identify most occurring events and their timeline.

1. Scroll-up and click on the number below WARNING. Dashboard will change other panels to show just the events for warnings. Using this, you can determine: whether the failures occurred on one particular pod/server or in multiple instances, whether they occurred around the same or different time.

1. Scroll-down to the actual warning messages. In this case some files from dojo were not found. Even though they are warnings, it'll be good to fix them by updating the application (we won't do that as part of this workshop).

    ![Liberty problems dashboard](extras/images/liberty-problems-dashboard.gif)

1. The following steps to visualize traffic to applications are illustrated  in the screen recording at the end of this section: Go back to the list of dashboards and click on _Liberty-Traffic-K5-20191122_. This dashboard helps to identify failing or slow HTTP requests on Liberty applications.

1. As before, adjust the time-range as necessary if no data is rendered.

1. You'll see some information about the namespace, pod, containers for the traffic along with a count for each. 

1. Scroll-down to `Liberty Error Response Code Count` section which lists the number of requests failed with HTTP response codes in 400s and 500s ranges.

1. Scroll-down to `Liberty Top URLs` which lists the most frequently accessed URLs
    - The _/health_ and _/metrics_ endpoints are running on the same server and are queried frequently for readiness/liveness probes and for scraping metrics information. It's possible to add a filter to include/exclude certain applications.

1. On the right-hand side, you'll see list of endpoints that had the slowest response times.

1. Scroll-up and click on the number listed below 400s. Dashboard will change other panels to show just the traffic with response code in 400s. You can see the timeline and the actual messages below. These are related to warnings from last dashboard about dojo files not being found (response code 404).

    ![Liberty traffic dashboard](extras/images/liberty-traffic-dashboard.gif)

#### Traditional WebSphere applications

1. Go back to the list of dashboards and click on _WAS-traditional-Problems-K5-20190609_. Similar to the first dashboard for Liberty, this dashboard visualizes message and trace information for WebSphere Application Server traditional.

1. As before, adjust the time-range as necessary if no data is rendered.

1. Explore the panels and filter through the events to see messages corresponding to just those events.

## Application Monitoring

Building observability into applications externalizes the internal status of a system to enable operations teams to monitor systems more effectively. It is important that applications are written to produce metrics. When the Customer Order Services application was modernized, we used MicroProfile Metrics and it provides a `/metrics` endpoint from where you can access all metrics emitted by the JVM, Open Liberty server and deployed applications. Operations teams can gather the metrics and store them in a database by using tools like Prometheus. The metrics data can then be visualized and analyzed in dashboards, such as Grafana.

### Grafana dashboard

1. Custom resource [GrafanaDashboard](dashboards/grafana/grafana-dashboard-cos.yaml) defines a set of dashboards for monitoring Customer Order Services application and Open Liberty. In web terminal, run the following command to create the dashboard resource:
    ```
    oc apply -f dashboards/grafana/grafana-dashboard-cos.yaml
    ```

1. The following steps to access the created dashboard are illustrated in the screen recording at the end of this section: In OpenShift console, from the left-panel, select **Networking** > **Routes**.

1. From the _Project_ drop-down list, select `app-monitoring`. 

1. Click on the route URL (listed under the _Location_ column).

1. Click on `Log in with OpenShift`. Click on `Allow selected permissions`.

1. In Grafana, from the left-panel, hover over the dashboard icon and click on `Manage`.

1. You should see `Liberty-Metrics-Dashboard` on the list. Click on it.

1. Explore the dashboards. The first 2 are for Customer Order Services application. The rest are for Liberty.

1. Click on `Customer Order Services - Shopping Cart`. By default, it'll show the data for the last 15 minutes. Adjust the time-range from the top-right as necessary. 

1. You should see the frequency of requests, number of requests, pod information, min/max request times.

1. Scroll-down to expand the `CPU` section. You'll see information about process CPU time, CPU system load for pods.

1. Scroll-down to expand the `Servlets` section. You'll see request count and response times for application servlet as well as health and metrics endpoints.

1. Explore the other sections.

    ![requesting server dump](extras/images/monitoring-dashboard.gif)

## Application Monitoring

Building observability into applications externalizes the internal status of a system to enable operations teams to monitor systems more effectively. It is important that applications are written to produce metrics. When the Customer Order Services application was modernized, we used MicroProfile Metrics and it provides a `/metrics` endpoint from where you can access all metrics emitted by the JVM, Open Liberty server and deployed applications. Operations teams can gather the metrics and store them in a database by using tools like Prometheus. The metrics data can then be visualized and analyzed in dashboards, such as Grafana.

### Grafana dashboard

1. Custom resource [GrafanaDashboard](https://github.com/IBM/teaching-your-monolith-to-dance/blob/liberty/deploy/grafana-dashboard-cos.yaml) defines a set of dashboards for monitoring Customer Order Services application and Open Liberty. In web terminal, run the following command to create the dashboard resource:
    ```
    oc apply -f https://raw.githubusercontent.com/IBM/teaching-your-monolith-to-dance/liberty/deploy/grafana-dashboard-cos.yaml
    ```

1. The following steps to access the created dashboard are illustrated in the screen recording at the end of this section: In OpenShift console, from the left-panel, select **Networking** > **Routes**.

1. From the _Project_ drop-down list, select `app-monitoring`. 

1. Click on the route URL (listed under the _Location_ column).

1. Click on `Log in with OpenShift`. Click on `Allow selected permissions`.

1. In Grafana, from the left-panel, hover over the dashboard icon and click on `Manage`.

1. You should see `Liberty-Metrics-Dashboard` on the list. Click on it.

1. Explore the dashboards. The first 2 are for Customer Order Services application. The rest are for Liberty.

1. Click on `Customer Order Services - Shopping Cart`. By default, it'll show the data for the last 15 minutes. Adjust the time-range from the top-right as necessary. 

1. You should see the frequency of requests, number of requests, pod information, min/max request times.

1. Scroll-down to expand the `CPU` section. You'll see information about process CPU time, CPU system load for pods.

1. Scroll-down to expand the `Servlets` section. You'll see request count and response times for application servlet as well as health and metrics endpoints.

1. Explore the other sections.

    ![requesting server dump](extras/images/monitoring-dashboard.gif)

## Day-2 Operations

You may need to gather server traces and/or dumps for analyzing some problems. Open Liberty Operator makes it easy to gather these on a server running inside a container.

A storage must be configured so the generated artifacts can persist, even after the Pod is deleted. This storage can be shared by all instances of the Open Liberty applications. RedHat OpenShift on IBM Cloud utilizes the storage capabilities provided by IBM Cloud. Let's create a request for storage.

### Request storage

1. In OpenShift console, from the left-panel, select **Storage** > **Persistent Volume Claims**.

1. From the _Project_ drop-down list, select `apps`. 

1. Click on `Create Persistent Volume Claim` button.

1. Ensure that `Storage Class` is `ibmc-block-gold`. If not, make the selection from the list.

1. Enter `liberty` for `Persistent Volume Claim Name` field.

1. Request 1 GiB by entering `1` in the text box for `Size`.

1. Click on `Create`.

1. Created Persistent Volume Claim will be displayed. The `Status` field would display `Pending`. Wait for it to change to `Bound`. It may take 1-2 minutes.

1. Once bound, you should see the volume displayed under `Persistent Volume` field.

### Enable serviceability

Enable serviceability option for the Customer Order Services application. In productions systems, it's recommended that you do this step with the initial deployment of the application - not when you encounter an issue and need to gather server traces or dumps. OpenShift cannot attach volumes to running Pods so it'll have to create a new Pod, attach the volume and then take down the old Pod. If the problem is intermittent or hard to reproduce, you may not be able to reproduce it on the new instance of server running in the new Pod. The volume can be shared by all Liberty applications that are in the same namespace and the volumes wouldn't be used unless you perform day-2 operation on a particular application - so that should make it easy to enable serviceability with initial deployment.

1. Specify the name of the storage request (Persistent Volume Claim) you made earlier to `spec.serviceability.volumeClaimName` parameter provided by `OpenLibertyApplication` custom resource. Open Liberty Operator will attach the volume bound to the claim to each instance of the server. In web terminal, run the following command:

    ```
    oc patch olapp cos -n apps --patch '{"spec":{"serviceability":{"volumeClaimName":"liberty"}}}' --type=merge
    ```

    Above command patches the definition of `olapp` (shortname for `OpenLibertyApplication`) instance `cos` in namespace `apps` (indicated by `-n` option). The `--patch` option specifies the content to patch with. In this case, we set the value of `spec.serviceability.volumeClaimName` field to `liberty`, which is the name of the Persistent Volume Claim you created earlier. The `--type=merge` option specifies to merge the previous content with newly specified field and its value.

1. Run the following command to get the status of `cos` application, to verify that the changes were reconciled and there is no error:

    ```
    oc get olapp cos -n apps -o wide
    ```
    The value under `RECONCILED` column should be _True_. 
    
    _Note:_ If it's _False_ then an error occurred. The `REASON` and `MESSAGE` columns will display the cause of the failure. A common mistake is creating the Persistent Volume Claim in another namespace. Ensure that it is created in the `apps` namespace.


1. In OpenShift console, from the left-panel, click on **Workloads** > **Pods**. Wait until there is only 1 pod on the list and its _Readiness_ column changed to _Ready_.

1. Pod's name is needed for requesting server dump and trace in the next sections. Click on the pod and copy the value under `Name` field.

    ![requesting server dump](extras/images/pod-name.png)

### Request server dump

You can request a snapshot of the server status including different types of server dumps, from an instance of Open Liberty server running inside a Pod, using Open Liberty Operator and `OpenLibertyDump` custom resource (CR). 

The following steps to request a server dump are illustrated in the screen recording below:

1. From the left-panel, click on **Operators** > **Installed Operators**.

1. From the `Open Liberty Operator` row, click on `Open Liberty Dump` (displayed under `Provided APIs` column).

1. Click on `Create OpenLibertyDump` button. 

1. Replace `Specify_Pod_Name_Here` with the pod name you copied earlier.

1. The `include` field specifies the type of server dumps to request. Heap and thread dumps are specified by default. Let's use the default values.

1. Click on `Create`.

1. Click on `example-dump` from the list.

1. Scroll-down to the `Conditions` section and you should see `Started` status with `True` value. Wait for the operator to complete the dump operation. You should see status `Completed` with `True` value.

    ![requesting server dump](extras/images/day2-dump-operation.gif)

### Request server traces

You can also request server traces, from an instance of Open Liberty server running inside a Pod, using `OpenLibertyTrace` custom resource (CR).

The following steps to request a server trace are illustrated in the screen recording below:

1. From the left-panel, click on **Operators** > **Installed Operators**.

1. From the `Open Liberty Operator` row, click on `Open Liberty Trace`.

1. Click on `Create OpenLibertyTrace` button.

1. Replace `Specify_Pod_Name_Here` with the pod name you copied earlier.

1. The `traceSpecification` field specifies the trace string to be used to selectively enable trace on Liberty server. Let's use the default value. 

1. Click on `Create`.

1. Click on `example-trace` from the list.

1. Scroll-down to the `Conditions` section and you should see `Enabled` status with `True` value. 
    - _Note:_ Once the trace has started, it can be stopped by setting the `disable` parameter to true. Deleting the CR will also stop the tracing. Changing the `podName` will first stop the tracing on the old Pod before enabling traces on the new Pod. Maximum trace file size (in MB) and the maximum number of files before rolling over can be specified using `maxFileSize` and `maxFiles` parameters.

    ![requesting server trace](extras/images/day2-trace-operation.gif)

### Accessing the generated files

The generated trace and dump files should now be in the persistent volume. You used storage from IBM Cloud and we have to go through a number of steps using a different tool to access those files. Since the volume is attached to the Pod, we can instead use Pod's terminal to easily verify that trace and dump files are present.

The following steps to access the files are illustrated in the screen recording below:

1. Remote shell to your pod via one of two ways:
  - From web terminal:
    ```
    oc rsh <pod-name>
    ```
  - From console: click on **Workloads** > **Pods**. Click on the pod and then click on `Terminal` tab. 

1. Enter `ls -R serviceability/apps` to list the files. The shared volume is mounted at `serviceability` folder. The sub-folder `apps` is the namespace of the Pod. You should see a zip file for dumps and trace log files. These are produced by the day-2 operations you performed.

    ![day-2 files](extras/images/day2-files.gif)


## Summary

Congratulations! You've completed **Application Management** lab! 


