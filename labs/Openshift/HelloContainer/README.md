# Introduction to Containerization

## Table of Contents

- [Background](#Background)
- [Prerequisites](#Prerequisites)
- [What is a Container](#What_is_Container)
- [Check Your Environment](#Check_Environment)
- [Run a Pre-built Image](#Run_Prebuilt)
- [Build and Run Your Own Image](#Build_Your_Own)
- [Managing Image Versions](#Versions)
- [Extra Credit](#Extra_Credit)

<a name="Background"> </a>
## Background

If you are expecting a lab about `docker`, you are at the right place.
This lab will introduce you to the basic concepts of containerization, including:

- What are containers and container images
- How to start, stop, and remove containers.
- How to create container images
- How to version container images

Note: This lab uses `docker`. 
However, We encourage you to use `podman` instead of `docker` in your own environment. By default, Openshift 4.x uses `podman`.
The reason is that `podman` is more secure.
It runs in user space, and does not require a daemon.
If `podman` is available, substitute `docker` with `podman` below.

- `podman` command line parameters are compatible with `docker`.
- Both `podman` and `docker` conform to the Open Container Initiative specifications, and they support the same image format and registry APIs.

The reason we are not yet using `podman` for this lab is that it does not yet work in the web terminal environment.
The web terminal runs in a container. 
In order to run this lab within the web terminal, we need a container that runs in a container.
We have not yet been able to configure `podman` to run inside a container.

<a name="Prerequisites"> </a>
## Prerequisites

- You have `podman` or `docker` installed.
- You have access to the internet.
- You have cloned this lab from github. If not, follows these steps:

```
git clone https://github.com/IBM/openshift-workshop-was.git
cd openshift-workshop-was/labs/Openshift/HelloContainer
```

<a name="What_is_Container"> </a>
## What is a Container

Compared to virtual machines, containers supports virtualization at the process level. 
Think of them as virtual processes.
The isolation abstraction provided by the operating system makes the process think that it's running in its own virtual machine.
As processes, containers may be created, started, and stopped much more quickly than virtual machines.

Everything you need to run your application, from the operating system and up, is stored in a special file called a container image.  
Container images are self contained and portable. 
You may run one or more instances anywhere. 
And you don't have to worry about missing prerequisites, because all prerequisites are stored in the image.

Container images are created via tools such as `docker` or `podman`. 
Existing images are hosted in container registries. 
For example, docker hub, or registry.access.redhat.com, or your own internal registry. 


If you need more background on containers: https://www.docker.com/resources/what-container


<a name="Check_Environment"> </a>
## Check your environment

1. List version of docker: `docker --version`
   - For more background on docker command line: https://docs.docker.com/engine/reference/commandline/cli/

<a name="Run_Prebuilt"> </a>
## Run a pre-built image

1. Container images must be available locally before they can be run. To list available local images: `docker images`

    ```
    REPOSITORY   TAG   IMAGE ID   CREATED   SIZE
    ```

1. Images are hosted in container registries. The default container registry for docker is docker hub, located at https://hub.docker.com.  Let's pull a test image from docker hub:  

    ```
    docker pull openshift/hello-openshift
    ```

    And the output:

    ```
    Using default tag: latest
    latest: Pulling from openshift/hello-openshift
    4f4fb700ef54: Pull complete 
    8b32988996c5: Pull complete 
    Digest: sha256:aaea76ff622d2f8bcb32e538e7b3cd0ef6d291953f3e7c9f556c1ba5baf47e2e
    Status: Downloaded newer image for openshift/hello-openshift:latest
    docker.io/openshift/hello-openshift:latest
    ```

1. List available local images again: `docker images`

    ```
    REPOSITORY                  TAG                 IMAGE ID            CREATED             SIZE
    openshift/hello-openshift   latest              7af3297a3fb4        2 years ago         6.09MB
    ```

1. Inspect the image metadata:
   ```
   docker inspect openshift/hello-openshift
   ``` 
   Note that:
    - It exposes two ports: 8080 and  8888
    - It runs as user 1001
    - The entry point executable is /hello-openshift
  
    ```
    [
        {
            "Id": "sha256:7af3297a3fb4487b740ed6798163f618e6eddea1ee5fa0ba340329fcae31c8f6",
            "RepoTags": [
                "openshift/hello-openshift:latest"
            ],
            "RepoDigests": [
                "openshift/hello-openshift@sha256:aaea76ff622d2f8bcb32e538e7b3cd0ef6d291953f3e7c9f556c1ba5baf47e2e"
            ],
            
            ...
            
            "Config": {
                "User": "1001",
                ...
                
                "ExposedPorts": {
                    "8080/tcp": {},
                    "8888/tcp": {}
                },
                "Env": [
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
                ],
                ...
                
                "Entrypoint": [
                    "/hello-openshift"
                ]
                ...
        }
    ]
    ```

1. Run the image in an container:
   ```
   docker run --name hello1 -d -p 8080:8080 -p 8888:8888 openshift/hello-openshift
   ```
   Note that:
    - The `--name` option gives the container a name.
    - The `-d` option runs the command in the background as a daemon
    - The `-p` option maps the port on the host to the port in the container. Through virtual networking, the port within the container is always the same for all running instances. But to support multiple concurrent running instances, the actual port on the host must be different for each instance. When you start the container, you may assign a new port on the host dynamically.
    - The output of the command is the container ID for the running container.
    - If the container starts successfully, the executable specified by the `Entrypoint` in the metadata is run. For our sample, it is `/hello-openshift`.
  
1. Access the application in the container.
    - If you are running in a server, or the web terminal, try `curl http://localhost:8080`.
    - If you are running on a desktop, point your browser to `http://localhost:8080`
    - Also try port 8888

1. Run another instance of the same image. Note that this new instance is assigned new port numbers 8081 and 8889 on the host. This is so that they don't conflict with the ports 8080 and 8888 already allocated to the first instance.
   ```
   docker run --name hello2 -d -p 8081:8080 -p 8889:8888 openshift/hello-openshift
   ```
    Question: how does this compare to the time it takes to start a new virtual machine?

1. Access the application in the new container the same way. However, use port `8081` and `8889` instead.

1. Verify there are two containers running in the same host: `docker ps`:

    ```
    CONTAINER ID        IMAGE                       COMMAND              CREATED              STATUS              PORTS                                            NAMES
    5a62f8527b44        openshift/hello-openshift   "/hello-openshift"   About a minute ago   Up About a minute   0.0.0.0:8081->8080/tcp, 0.0.0.0:8889->8888/tcp   hello2
    c9d49aaa01b7        openshift/hello-openshift   "/hello-openshift"   4 minutes ago        Up 4 minutes        0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp   hello1
    ```

1. View the logs: 
   ```
   docker logs hello1
   ```

   And the output:

    ```
    serving on 8888
    serving on 8080
    ```

1. View the logs on the second container: 
   ```
   docker logs hello2
   ```

   And the output:

   ```
   serving on 8888
   serving on 8080
   ```

   Note: within the container, each instance behaves as if it's running in its own virtual environment, and has opened the same ports. Outside of the container, different ports are opened.

1. To export the file system of a running container: 
   ```
   docker export hello1 > hello1.tar
   ```

1. List the files on the file system: 
   ```
   tar -tvf hello1.tar
   ``` 
   
   Note that this is a very small image.

   ```
   -rwxr-xr-x 0/0               0 2020-04-29 16:48 .dockerenv
   drwxr-xr-x 0/0               0 2020-04-29 16:48 dev/
   -rwxr-xr-x 0/0               0 2020-04-29 16:48 dev/console
   drwxr-xr-x 0/0               0 2020-04-29 16:48 dev/pts/
   drwxr-xr-x 0/0               0 2020-04-29 16:48 dev/shm/
   drwxr-xr-x 0/0               0 2020-04-29 16:48 etc/
   -rwxr-xr-x 0/0               0 2020-04-29 16:48 etc/hostname
   -rwxr-xr-x 0/0               0 2020-04-29 16:48 etc/hosts
   lrwxrwxrwx 0/0               0 2020-04-29 16:48 etc/mtab -> /proc/mounts
   -rwxr-xr-x 0/0               0 2020-04-29 16:48 etc/resolv.conf
   -rwxr-xr-x 0/0         6089990 2018-04-18 10:22 hello-openshift
   drwxr-xr-x 0/0               0 2020-04-29 16:48 proc/
   drwxr-xr-x 0/0               0 2020-04-29 16:48 sys/
   ```

1. Run another command in the running container. 
You can reach into the running container to run another command. 
The typical use case is to run a shell command, so you can use the shell to navigate within the container and run other commands.
However, our image is tiny, and there is no built-in shell.
This makes it safer, but also more difficult to debug.
For the purpose of this lab, we'll execute the same command again: `docker exec -ti hello1 /hello-openshift`. 
Running this command again in the same container results in an error, 
because there is already another copy running in the background that is bound to the ports 8080 and 8888:

    ```
    serving on 8888
    serving on 8080
    panic: ListenAndServe: listen tcp :8888: bind: address already in use
    ...
    ```

1. Stop the containers:
   - `docker stop hello1`
   - `docker stop hello2`

1. List running containers: `docker ps`

1. List all containers, including stopped ones: `docker ps -a`

    ```
    CONTAINER ID        IMAGE                       COMMAND              CREATED             STATUS                      PORTS               NAMES
    5a62f8527b44        openshift/hello-openshift   "/hello-openshift"   28 minutes ago      Exited (2) 28 seconds ago                       hello2
    c9d49aaa01b7        openshift/hello-openshift   "/hello-openshift"   31 minutes ago      Exited (2) 32 seconds ago                       hello1
    ```
   
1. restart a stopped container: `docker restart hello1`

1. List running containers: `docker ps`

    ```
    CONTAINER ID        IMAGE                       COMMAND              CREATED             STATUS              PORTS                                            NAMES
    c9d49aaa01b7        openshift/hello-openshift   "/hello-openshift"   33 minutes ago      Up 8 seconds        0.0.0.0:8080->8080/tcp, 0.0.0.0:8888->8888/tcp   hello1
    ```
    
1. Stop the container: `docker stop hello1`

1. Remove stopped containers, and note that there are no more containers:
    - `docker rm hello1`
    - `docker rm hello2`
    - `docker ps -a`

1. Remove the image from local cache:

    ```
    docker images
    docker rmi openshift/hello-openshift
    docker images
    ```
 

<a name="Build_Your_Own"> </a>
## Build and Run Your Own Image

We use a `Containerfile`, which contains the instructions to create the new layers of your image. 
For those familiar with docker, the `Containerfile` is equivalent to `Dockerfile`.

Recall that an image contains the entire file system that you want to use to run your virtual process in a container.
For this sample, we are building a new image for a Java EE web application ServletApp.war.
It is configured to run on the WebSphere Liberty Runtime. 
The configuration file for the server is in the server.xml.

1. Change directory to /openshift-workshop-was/labs/Openshift/HelloContainer 

1. Review the provided `Containerfile` from the directory:

   - the content of `Containerfile`:
    ```
    FROM ibmcom/websphere-liberty:kernel-java8-ibmjava-ubi
    COPY server.xml  /config
    COPY ServletApp.war /config/dropins/app.war
    RUN /liberty/wlp/bin/installUtility install --acceptLicense /config/server.xml 
    ```

   - To create a new image, you start with a pre-existing image. The first line `FROM` specifies the existing image to be used as the base.  If this is not in the local registry, it will be pulled from a remote registry such as docker hub. The base image we are using, `ibmcom/websphere-liberty`, is already prepackaged for us and made available on docker hub.

   - The second line `COPY`  is a straight copy of the file `server.xml` from the local directory to `/config/server.xml` in the image. This adds a new layer to the image with the actual server configuration to be used.
   
   - The third line, another `COPY`, copies `ServletApp.war` from the current directory into a new layer in the image you are creating, at the location `/config/dropins/app.war`.

   - The last line `RUN` runs the `installUtility` command within the image to install additional features required to run the server as specified in `server.xml`. You can use the `RUN` command to run any command that is available within the image to customize the image itself.

1. Run the build.  Ensure you include `.` at the end of the command (the dot indicates using the file from the current directory):
    ```
    docker build -t app -f Containerfile .
    ```

    - The `-t` option tags the name of the image as `app`.  
    - The `-f` option specifies the name of the `Containerfile`. 
    - The build command runs the commands in `Containerfile` to build a new image called `app`.

    - docker build output:
    ```
    Sending build context to Docker daemon   25.6kB
    Step 1/4 : FROM ibmcom/websphere-liberty:kernel-java8-ibmjava-ubi
    kernel-java8-ibmjava-ubi: Pulling from ibmcom/websphere-liberty
    ee2244abc66f: Pull complete 
    befb03b11956: Pull complete 
    137dc88f6a93: Pull complete 
    5bdd69a33184: Pull complete 
    d4e2554981d7: Pull complete 
    32c91bc0f2e1: Pull complete 
    db7e931336a9: Pull complete 
    3b32f9956ae2: Pull complete 
    304584ffa0a2: Pull complete 
    9f6da4c82b7e: Pull complete 
    b6fa5b2e2325: Pull complete 
    Digest: sha256:d76f79695afe2f653fc7b272f9a629105446e6b78ff0d733d494c93ff05728e7
    Status: Downloaded newer image for ibmcom/websphere-liberty:kernel-java8-ibmjava-ubi
     ---> 4d9265befb26
    Step 2/4 : COPY server.xml  /config
     ---> 4a02d03d3725
    Step 3/4 : COPY ServletApp.war /config/dropins/app.war
     ---> b2def2a0feac
    Step 4/4 : RUN /liberty/wlp/bin/installUtility install --acceptLicense /config/server.xml
     ---> Running in 5f5b05aec1ae
    Checking for missing features required by the server ...
    The server requires the following additional features: appsecurity-2.0 servlet-3.0.  Installing features from the repository ...
    Establishing a connection to the configured repositories ...
    This process might take several minutes to complete.

    Successfully connected to all configured repositories.

    Preparing assets for installation. This process might take several minutes to complete.

    Additional Liberty features must be installed for this server.

    To install the additional features, review and accept the feature license agreement:
    The --acceptLicense argument was found. This indicates that you have
    accepted the terms of the license agreement.

    Step 1 of 12: Downloading ssl-1.0 ...
    Step 2 of 12: Installing ssl-1.0 ...
    Step 3 of 12: Downloading appSecurity-2.0 ...
    Step 4 of 12: Installing appSecurity-2.0 ...
    Step 5 of 12: Downloading servlet-3.0 ...
    Step 6 of 12: Installing servlet-3.0 ...
    Step 7 of 12: Downloading jndi-1.0 ...
    Step 8 of 12: Installing jndi-1.0 ...
    Step 9 of 12: Downloading distributedMap-1.0 ...
    Step 10 of 12: Installing distributedMap-1.0 ...
    Step 11 of 12: Validating installed fixes ...
    Step 12 of 12: Cleaning up temporary files ...

    All assets were successfully installed.

    Start product validation...
    Product validation completed successfully.
    Removing intermediate container 5f5b05aec1ae
     ---> e1c6bfabda76
    Successfully built e1c6bfabda76
    Successfully tagged app:latest
    ```

1. List the images to see that the new image `app` is built: 
   ```
   docker images
   ```
   Note that the base image, `ibmcom/websphere-liberty` has also been pulled into the local registry.

   ```
   REPOSITORY                            TAG                        IMAGE ID       CREATED         SIZE
   app                                   latest                     baa6bb9ad29d   2 minutes ago   544 MB
   ibmcom/websphere-liberty              kernel-java8-ibmjava-ubi   7ea3d0a2b3fe   4 hours ago     544 MB
   ```

1. Start the container. Note that you are running with both http and https ports: 
   ```
   docker run -d -p 9080:9080 -p 9443:9443 --name=app-instance app
   ```

1. Access the application running in the container:
   - If you are running in a server, or using the web terminal, run with the curl command:
     ```
     curl --insecure https://localhost:9443/app
     ```
    and ensure you have output that looks like:
     ```
     <html><h1><font color=green>Simple Servlet ran successfully</font></h1>Powered by WebSphere Liberty  <html>
     ```
   - If you are running on a desktop with browser,
     - point your browser to port 9080: `http://localhost:9080/app` 
     - and check that renders a page showing `Simple Servlet ran successfully`.
     - Also point your browser to 9443:  `https://localhost:9443/app`


1. List the running containers: 
    ```
    docker ps
    ```
    ```
    CONTAINER ID     IMAGE     COMMAND                  CREATED             STATUS              PORTS                                NAMES                                     
    595cdc49c710     app       "/opt/ibm/helpers/ru…"   8 minutes ago       Up 8 minutes        0.0.0.0:9080->9080/tcp, 0.0.0.0:9443->9443/tcp      app-instance
    ```

1. Access the logs to your container: 
   ```
   docker logs -f app-instance
   ```
   Use `Ctrl-C` To exit.

1. Remote shell into your running container to poke around: 
   ```
   docker exec -it app-instance /bin/sh
   ```
   In the shell session,
    - run `whoami` and then run `id`, note you're not running as root.
    - Note that this is a stripped down environment where many commands are not available. For example, try `which ps`.
    - cd `/logs` to find the log files
    - cd `/liberty/wlp` to find the location of the liberty install
    - cd `/liberty/wlp/usr/servers/defaultServer` to find the server configuration. 
    - cd `/opt/ibm/wlp/output/defaultServer` to find the workarea files required by the server runtime.
    - Exit from the container: `exit`
1. Cleanup:
    - `docker stop app-instance`
    - `docker rm app-instance`

<a name="Versions"> </a>
## Managing Image Versions

There is no built-in versioning for container images. 
However, you may use a tagging convention to version your images. 
The convention is to use `major.minor.patch`, such as `1.3.5`.
The default tag if you don't specify one is `latest`.
    
Let's assume that the first version we will build for our environment is 1.3.5. (The earlier versions are built in a different environment.) Run the commands to tag the latest `app` image for our first version:

 ```
 docker tag app app:1
 docker tag app app:1.3
 docker tag app app:1.3.5
 ```

 List the images again:

 ```
 docker images
 ```

 And the output:

 ```
 REPOSITORY                 TAG                        IMAGE ID            CREATED             SIZE
 app                        1                          d98cbdf82a0d        21 hours ago        542MB
 app                        1.3                        d98cbdf82a0d        21 hours ago        542MB
 app                        1.3.5                      d98cbdf82a0d        21 hours ago        542MB
 app                        latest                     d98cbdf82a0d        21 hours ago        542MB
 ```

Note that all the different tags are currently associated with the same image, as they have the same image ID.
After tagging, the command `docker run app:<version> ...` or `docker pull app:<version> ...` resolves the available versions as follows:

- `app:1` resolves to the latest 1.x.x version, which in this case is `1.3.5`.
- `app:1.3` resolves to the latest 1.3.x version, which in this case is the `1.3.5`
- `app:1.3.5` resolves to the exact version `1.3.5`.

After you build a new patch image containing defect fixes, you want to manage the tags for the new image so that a
new `docker run app:<version> ...` or `docker pull app:<version> ...` command resolves the images as follows:

- `app:1.3.5`: resolves to the existing `1.3.5` image.
- `app:1.3.6`: resolves to the new image
- `app:1.3`: resolves to the new image.
- `app:1`: resolves to the new image


Let's simulate a defect fix by building a new image using `Containerfile1` instead of `Containerfile`:
```
docker build -t app -f Containerfile1 .
```


Tag it as follows :

```
docker tag app app:1
docker tag app app:1.3
docker tag app app:1.3.6
```

Verify that these are the same images: `app:1`, `app:1.3`, `app:1.3.6`.


A new minor version involves compatible changes beyond just bug fixes. After you build a new minor version image, you want to manage the tags such that:

- `app:1.3.5`: resolves to the existing `1.3.5` image.
- `app:1.3.6`: resolves to the existing `1.3.6` image
- `app:1.4.0`: resolves to the new image
- `app:1.3`: resolves to the existing `1.3.6` image.
- `app:1.4`: resolves to the new image.
- `app:1`: resolves to the new image

Build a new image using `Containerfile2`:
```
docker build -t app -f Containerfile2 .
```

Tag it as  follows:

```
docker tag app app:1
docker tag app app:1.4
docker tag app app:1.4.0
```

Verify that 

- `1`, `1.4`, and `1.4.0` are the same image
- `1.3` and `1.3.6` are the same image


<a name="Extra_Credit"> </a>
## Extra Credit

- Search the internet for information about multi-stage build. 
  In a single stage build, the final image contains both build and runtime artifacts. 
  A multi-stage build allows you to build with one base image, and copy the result of the build to another base image. 
  This gives you even more control over the output of the build, and the size of the final image.
- Start another instances of the `app` image for vertical scaling, but with different port numbers on the host.
- Point your browser to `hub.docker.com`, click "Explore" and explore the millions of available images.
- Think about how you would tag a new image at a major version, `2.0.0`.
- Think about what would be required to manage containers across multiple machines to support horizontal scaling.
    

Congratulations! You have completed the **Introduction to Containerization** lab.

## Next
Please follow the link to do the next lab **Introduction to Container Orchestration using Openshift**:
- [Introduction to Container Orchestration using Openshift](https://github.com/IBM/openshift-workshop-was/tree/master/labs/Openshift/IntroOpenshift)


