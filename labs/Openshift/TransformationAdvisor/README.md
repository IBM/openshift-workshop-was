IBM Cloud Transformation Advisor (Transformation Advisor) is one of the tools included in the Cloud Pak for Applications (CP4Apps), a developer tool helps you quickly evaluate on-premises Java EE applications and to prepare the apps for a rapid cloud deployment. The tool identifies the Java EE programming models in the application, help determine its complexity by listing a high-level inventory of the content and structure of the application, highlight Java EE programming models and WebSphere API differences between the WebSphere profile type and show any Java EE specification implementation differences that could affect the app. You can use this tool in your move in the cloud journey to quickly analyze on-premise WebSphere apps without accessing source code and to prepare the apps for a rapid cloud deployment.

This lab showcases Transformation Advisor as a tool to help you analyze your existing applications and begin the re-platform process for applications you select. Re-platform uses Lift, Modify and Shift approach to move an existing application the cloud. In this lab, you learn how to package a selected candidate Java application to Liberty container without any application code change and to deploy it to an IBM Red Hat OpenShift Kubernetes Service (ROKS) cluster in IBM Cloud using the Transformation Advisor Migration Bundle tool.

### 1.	Business Scenario

As shown in the image below, your company has a web application called Mod Resorts, a WebSphere application showing the weather in various locations. Your company wants to move this application from on-premises to the cloud.

![](images/mod-resorts-app-home-page.png)

You will analyze this application and assess the difficulty of re-platforming it to the cloud. Based on the analysis you will discover that you can move this application from the traditional WebSphere Server environment to a light-weighted Liberty server environment without any code change.  Then you will use the Transformation Advisor migration plan to create the migration bundle, to containerize the application on Liberty, and to deploy the Docker container to an OpenShift Kubernetes cluster environment.

### 2.	Objective

The objectives of this lab are to:

*	learn how to collect Java application and configuration data using the Transformation Advisor Data Collector tool. 
*	learn how to use the Transformation Advisor to evaluate the move to cloud efforts and to identify the good candidate for migration.
* learn how Transformation Advisor can accelerate application migration to cloud process, minimize errors and risks and reduce time to market
* learn how to deploy the application to an OpenShift cluster environment using the migration bundle created by Transformation Advisor

### 3.	Prerequisites

The following prerequisites must be completed prior to beginning this lab:

* Familiarity with basic Linux commands
* Have internet access
* Have an IBM Cloud account

### 4.	What is Already Completed

In your lab environment, WebSphere Application Server has already been set up with the applications you will evaluate.


### 5.	Lab Tasks

During this lab, you complete the following tasks:

* Review the on-premises WebSphere application
* Access Transformation Advisor and download the data collector
* Scan and upload your application data into Transformation Advisor
* Review the migration advice generated for your application
* Create Migration Bundle
* Update the Migration Bundle
* Test the Migration Bundle Locally (Optional)
* Containerize Liberty Application
* Deploy the Application to OpenShift

### 6.	Execute Lab Tasks

#### 6.1 Log in to the Workstation VM and get started (skip this step if you are using your own workstation)
Open the IBM Cloud dashboard. Ensure the account name displayed in the top right is 2044184 - CP4AWorkshops. Find your cluster in the resource list and click it, then click the link for **OpenShift web console** in the top right. You should now be in the OpenShift console.

In the left-hand navigation bar, choose **Networking** and then **Routes**. Make sure the **Project** field is set to "lab" and find the **Location** for "vnc-route". Click this link to enter the lab environment. Click conenct and enter the password given to you by the lab instructor (do we want to give the password here?) You should now see a desktop environment.

#### 6.2 Review the on-premises WebSphere application (skip this step if you are using your own workstation)

In this task, you take a look at Mod Resorts application deployed to the local WebSphere Application Server (WAS) environment. You are going to move this application to the cloud using Open Liberty Operator later.

1. Start WebSphere Application Server

  In the workstation VM, you have a local WebSphere Application Server V9 which hosts the Mod Resorts application. 

  To start the WAS server:

  a.	Open a terminal window by clicking **Applications**>**Terminal** or using the shortcut on the desktop.
 
  ![](images/vm-terminal-link.png)

  b.	In the terminal window, issue the command below to start the WAS server.

  ```
/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startServer.sh server1
  ```
    
  Within a few minutes the WAS server is ready.

  c.	Access the WAS Admin Console to view the application deployed by clicking **Applications**>**Firefox** to open a browser window.
 
  ![](images/vm-browser-link.png)

  d.	From the web browser window enter the following URL to view the console:

  ```
http://localhost:9060/ibm/console
  ```

  e.	In the WAS Admin Console login page, click **Login**. 

  f.	On the WAS Console page, click **Applications** -> **Application Types** -> **WebSphere enterprise applications** to view the apps deployed.
 
  ![](images/was-enterprise-apps-link.png)

  In the Enterprise Applications list, you can see all applications deployed. The Mod Resorts application is in the list, currently it is running.
 
  ![](images/was-enterprise-apps.png)

2.	View the Mod Resorts application

  a. From the web browser window, click new Tab to open a new browser window. Type the Mod Resorts application URL: **http://localhost:9080/resorts/** and press **Enter**.

  The Mod Resorts application home page is displayed.

  ![](images/mod-resorts-app-home-page.png)

  b. Click **WHERE TO?** dropdown menu to see the city list.

  ![](images/mod-resorts-app-where-to.png)
 
  c. Click **PARIS, FRANCE** from the list, it shows the weather of the city.

  ![](images/mod-resorts-app-paris.png)    
 
  Now you have reviewed the application, next you use Transformation Advisor to analyze this application to see if it is good candidate to be moved to the cloud.

#### 6.3	Access Transformation Advisor

Transformation Advisor is installed on ROKS in IBM Cloud.  In this lab, you use the following steps to access it.

1. From the OpenShift web console you opened earlier, navigate to **Networking**>**Routes** if you have not already and choose "ta" from the **Project** drop-down. Look for the route ending in "ui-route" and right click the **Location** URL to copy the link to the clipboard. You are going to send this URL to the clipboard in the lab environment.

2. In the tab with your lab environment, locate the arrow on the left side of the screen and click it. This will reveal the noVNC toolbar. Click the second button from the top to reveal the Clipboard palette. This palette contains a textbox which reflects the current contents of the lab environment clipboard. Paste the URL you copied earlier into the textbox. Then, in the lab environment, paste the URL into the address bar using CTRL-V (even on macOS). You can use this technique to send any data you need to into the lab environment.
 	
3. The Transformation Advisor home page is displayed.
    ![](images/ta-home-page.png)
 
#### 6.4	Upload Application Data into Transformation Advisor

  The Transformation Advisor can evaluate any Java based applications and help to package the good candidate application to move to cloud. You will use the data collector provided by TA to scan your applications and upload the results back to TA itself.

1. In the Transformation Advisor page, you first create a new workspace by entering the workspace name as **Evaluation** and then clicking **Next**.
 
  ![](images/ta-new-workspace.png)

    Note: A workspace is a designated area that houses the migration recommendations provided by Transformation Advisor against your application server environment. You can name and organize these however you want, whether it’s by business application, location or teams. 

2. Enter the collection name as **Server1** and click **Let’s go**.
 
  ![](images/ta-new-collection.png)

    Note: Each workspace can be divided into collections for more focused assessment and planning. Like workspaces, collections can be named and organized in whatever way you want.

3. Once the Workspace and Collection are created, you get the data collection options page.  You can either download the Data Collector utility and run it against your application server, or upload an existing data file. In this lab, you are going to use the data collector option. Click **Data Collector** to go to the download page.

  ![](images/ta-upload-data.png) 

4. The default platform for the data collector is Linux, which you are using for this lab. Click **Download for Linux** and choose to save the resulting archive.

5. Open a terminal and navigate to `/headless/Downloads` and extract the data collector using the following command, replacing the file name as needed:

  ```
tar xvf transformationadvisor-Linux_Evaluation_Server1.tgz
  ```

6. Change to the bin directory of the data collector and run it, specifying the path to WebSphere with `-w` and the name of the profile to scan with `-p`:

  ```
  cd transformation-advisor-2.3.0/bin
  ./transformationadvisor -w /opt/IBM/WebSphere/AppServer -p AppSrv01
  ```

7. Enter "1" to agree to the license terms, and the data collector will scan your profile, generate reports, and upload the results to TA. Return to the TA web page once it instructs you to do so. Click **Server1** in the top breadcrumb bar to return to the collection page.
 
  ![](images/ta-recommendation-page.png)

  Transformation Advisor provides all migration recommendations for all applications deployed to the WAS server based on the specified source and target environments. 

  On the **Recommendations** page, the identified migration source environment is shown in the **Source environment** section, and the target environment is shown in the **Preferred migration on Cloud Pak for Applications** section. The data collector tool detects that the source environment is your **WebSphere Application Server AppSrv01** profile. The target environment is **Compatible Liberty Runtime**, which is the default target environment.

  The Recommendations page also shows the summary analysis results for all the applications in the AppSrv01 environment to be moved to a Compatible Liberty Runtime environment. For each application, you can see these results:
    * Name
    * Complexity level
    * Dependencies
    * Issues
    * Estimated development cost in days

  For example, if you want to move the **modresorts.ear** application to Liberty on OpenShift, the complexity level is Simple which indicates that the application code does not need to be changed before it can be moved to cloud. The application has no dependency, has one minor level issue and the estimated development effort is 0 day with no code change.

  You can view details for a scanned application by clicking its name. On the application details page, you can see any dependencies detected, the complexity rules which contributed to the overall complexity assessment, and links to additional Technology, Inventory, and Analysis reports.

#### 6.5 Create Migration Bundle

Transformation Advisor has the ability to use the imported application analysis data to generate a migration bundle for the specified application and target environment to accelerate the migration process while minimizing the errors and risks. You use it to create our migration package for moving the Mod Resorts application to Liberty container on OpenShift cluster environment.

1. Click the action icon in **modresorts.ear** application row. 

    ![](images/ta-action-button.png)
2. Select the **View migration plan** action.

  ![](images/ta-view-migration-plan.png)
 
3. Transformation Advisor is now starting to prepare the migration bundle package for the application. It quickly prepares a migration bundle package with several required key files created from the application data collected from the WAS server, including server.xml, pom.xml, OpenShift Operator resource files and Dockerfile. However, it needs a few more application specific dependencies like the application runtime binary file and other library files, like database driver file the application is using.  For the Mod Resorts application, it only needs the application runtime binary file. Select **Binary** option, **Don’t use Accelerator for Teams** option, and **Manual upload** option, then click **Drag or add file** in Application binary row.

    ![](images/ta-migration-drag-or-add-file.png)
4. Navigate to **/headless/Desktop/apps** directory, select **modresorts.war** file and click Open to add the file to the bundle.
 
   ![](images/add-modresorts-war.png)

    Once the file is added, your application migration bundle is completed and ready for use.
    
    If you are sure that no more changes are needed for the migration bundle, you can push it to your GitHub repository and use your delivery pipeline to deploy it to cloud.
    
    In this lab, you are going to make a few changes and deploy the bundle manually.

#### 6.6 Inspect and Update the Migration Bundle

1. Click **Download** to download it to the local machine.

  ![](images/ta-download-bundle.png) 

    Your migration bundle is downloaded to **/headless/Downloads** directory.

2. After the migration bundle is downloaded, click **Applications**>**Files**.

  ![](images/vm-files-link.png)

3. Navigate to the **/headless/Downloads** directory.

  ![](images/downloads-folder.png) 

4. Right click the migration bundle zip file and select **Open With Archive Manager**.

  ![](images/extract-bundle.png) 
5. Click **Extract**.

  ![](images/extract-bundle-2.png) 
6. In the next page, select **Downloads** directory, click the **New Folder** icon to create a new folder named **modresorts_migrationBundle** and click **Create**.

  ![](images/extract-bundle-3.png) 
7. Click **Extract** to unzip the bundle pack in the **/home/student/Downloads/modresorts_migrationBundle** directory

  ![](images/extract-bundle-4.png) 
  
8.  Close the **Archive Manager**. Navigate to the **modresorts_migrationBundle** directory, you see the all files in the migration bundle package. 

  ![](images/migration-bundle-folder.png) 

  There are four key files you needed to build your WAS Base server container image:
    * src/main/liberty/config/server.xml - contains the Liberty server configuration for the application you are migrating. It configures application dependencies such as database connections and messaging. 
    * Dockerfile - this multi-stage file first downloads the application binary and dependencies (if specified), and then builds a docker image which includes your application configured in Liberty.
    * pom.xml – the file contains information about the project and configuration details used by Maven to build the project. In the case of a binary project generated by Transformation Advisor, it may contain coordinates for application binaries and dependencies.
    * target/application runtime – the ear or war file of your application.
    * Operator resources - deploy and manage your migrated application in Cloud Pak for Applications running on OpenShift Container Platform.

9. Navigate to the **modresorts_migrationBundle/operator/deploy** directory, open the **operator.yaml** file to the file editor by double-clicking it. Locate the line which specifies the operator image, around line 20:

  ```
  image: openliberty/operator:0.3.0
  ```


  Update the version of the image so it reads `openliberty/operator:0.6.0` to avoid a normally-rare issue which can occur more frequently in the lab environment.

10. Navigate to the **modresorts_migrationBundle/operator/application** directory and open the **application-cr.yaml** file.  Note the value of **applicationImage:** around line 9. This will be the tag you will use for your container image.

#### 6.8 Containerize Liberty Application

In this task, you containerize the application. You first create a Liberty Docker image that has the Mod Resorts application installed and configured, and then you test the image to confirm that it is operating correctly.

1. From the terminal window issue the command below to stop the WebSphere server and to free the ports it is using:

  ```
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopServer.sh server1
  ```

2.	Go to where your migration artifacts are located and build your image from the docker file.

  ```
  cd /headless/Downloads/modresorts_migrationBundle
  docker build . --no-cache -t modresorts:latest
  ```
  The base Liberty image is pulled down and used to create the image that includes your migrated application.

3. Once the Docker image is built, create a container instance from the image and confirm that it is working correctly:

  ```
  docker run -p 9080:9080 modresorts:latest
  ```
4. If everything looks good, the container has been started and mapped to the port 9080. You can access it from your browser with this link: **localhost:9080/resorts/**.

5. After testing, press **Ctrl-C** to stop the container.

#### 6.9 Deploy the Application to OpenShift

In this step you deploy the docker image you have created to Red Hat OpenShift and create an instance of it. Before you begin, you need push the image to the OpenShift image registry.
Note: The migration artifacts generated by Transformation Advisor (specifically the operator/application/application-cr.yaml file) assume that the default Docker Registry is being used. If you choose to use a different registry, remember to update the image property in the YAML file appropriately.

1.	From your local web browser, return to the tab/window with the OpenShift web console.

2.	In the OpenShift Web Console page, click the **Action** icon next to your username and select **Copy Login** Command to get the ROKS login command.

  ![](images/ocp-copy-login-command.png) 
3.	In the next page, click **Display Token** link.

  ![](images/display-token.png) 
4.	Copy the ROKS login command to the clipboard.

  ![](images/copy-token.png) 

5.  Return to the lab window/tab and use the clipboard palette to send the login command to the lab environment.

6.	In the lab environment, go back to the Terminal window, right-click to paste the ROKS login command window and press Enter to log in to the ROKS cluster.
 
  ![](images/run-oc-login-command.png) 
7.	Create a new project (namespace) as **modresorts**.

  ```
  oc new-project modresorts
  ```
  ![](images/oc-new-project.png)
  You see the message to confirm that the modresorts project is created.


8.	Get ROKS internal image registry URL and cluster URL with commands:

  ```
  export INTERNAL_REG_HOST=`image-registry/openshift-image-registry.svc:5000`
  ```
    
    Note: You are using the cluster-internal name for the registry because the lab environment is also inside the cluster. In most cases, you will use the URL for the image-registry's default route to push images from outside the cluster.

9.	Log in to the OpenShift Docker registry with the command:
    
    ```
    docker login -u $(oc whoami) -p $(oc whoami -t) $INTERNAL_REG_HOST
    ```

 
10. Execute the following command to push your docker image to OpenShift image repository.

    ```
    docker tag modresorts:latest $INTERNAL_REG_HOST/`oc project -q`/modresorts:latest
    docker push $INTERNAL_REG_HOST/`oc project -q`/modresorts:latest
    ```
    When it is done, your application docker image is pushed to the ROKS cluster image registry. This tag should match what you noted earlier in **application-cr.yaml.** If it doesn't, update **application-cr.yaml** to match the tag of the image you pushed.

11.	Verify the pushed Docker image in ROKS cluster.

    a.	From the ROKS Web Console Home page, click **Builds**>**Images Streams**.
  
   ![](images/ocp-builds-is.png) 
   
    b.	Change project (namespace) from **default** to **modresorts**.
   
   ![](images/ocp-modresorts-project.png) 

    c.	You can see the image you just pushed is listed. Click its link to view its details.
 
   ![](images/modresorts-image.png) 
    In the Image Stream Details Page Overview section, you see the image repository you used to push the image.  Note that in most cases, you will push images using the default route of the image registry. If so, the image will have two tags--one referencing the external route and another referencing the internal service name, which you used in the lab environment.
 
   ![](images/modresorts-is-overview.png) 
12.	Return to your terminal and cd to the **modresorts_migrationBundle/operator** directory. Run the following commands to deploy the application operator, which will allow you to create instances of your application by deploying a single file:

```
oc apply -f deploy/service_account.yaml
oc apply -f deploy/role.yaml
oc apply -f deploy/role_binding.yaml
oc apply -f deploy/operator.yaml
oc apply -f application/application-crd.yaml
```

  You can check that the operator has started by running the command `oc get pods`. There should be a pod with a name beginning `modresorts-operator` with **STATUS** "Running". If it isn't running yet, wait a few moments, then check again.

13. Once the operator has been deployed and started, run the following command to use the operator to deploy the image you created earlier:

  ```
oc apply -f application/application-cr.yaml
  ```

14.	Your application is now deployed. To view it, run the following command to display the publicly-viewable URL of the application:

  ```
  oc get route -o jsonpath='{.items[0].spec.host}' && echo
  ```

  The **Open Liberty** home page is displayed in a new browser window.

  ![](images/ocp-welcome-to-liberty.png)
15.	Type the application context root **/resorts** to the end of the URL and press **Enter**.

  ![](images/ocp-add-context-root.png)
  
  The Mod Resorts application home page displayed.

16.	Navigate through the application web pages as you did in the WAS version to verify the application deployment.

### 7.	Summary

In this lab, you have learned how to use Transformation Advisor to prepare a migration bundle for your application and to deploy it to cloud. As a part of IBM Application Modernization solutions in IBM Cloud Pak for Applications, the Transformation Advisor accelerates application migrating to cloud process, minimize errors and risks and reduce time to market. To learn more about IBM Application Modernization solutions, please visit [Cloud Pak for Applications](https://www.ibm.com/cloud/cloud-pak-for-applications).

**Congratulations! You have successfully completed the lab “Modernize Java Application for Container and OpenShift with Transformation Advisor”.**