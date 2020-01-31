# Introduction to Containerization

# Background

If you are expecting a lab about `docker`, you are at the right place. Note that this lab uses `podman` instead of `docker` for the following reasons:

-  `podman` is more secure because it runs in user space, and does not require a daemon.
- `pdoman` command line parameters are compatible with `docker`.
- Both `podman` and `docker` conform to the Open Container Initiative specifications, and they support the same image format and registry APIs.


# Prerequisites

- Ensure you are familiar with the concept of images and containers .
- You have podman installed.
- You have access to the internet.


# Check your environment

- List version of podman: `podman --version`
```
podman version 1.6.4
```

# Run a pre-buit image

- List avaiable local images: `podman images`
```
REPOSITORY   TAG   IMAGE ID   CREATED   SIZE
```

- Pull an test image from docker hub:  `podman pull openshift/hello-openshift`
```
Trying to pull registry.access.redhat.com/openshift/hello-openshift...
  name unknown: Repo not found
Trying to pull docker.io/openshift/hello-openshift...
doGetting image source signatures
Copying config 7af3297a3f done
Writing manifest to image destination
Storing signatures
7af3297a3fb4487b740ed6798163f618e6eddea1ee5fa0ba340329fcae31c8f6
```

- List available local images again: `podman images`
```
REPOSITORY                            TAG      IMAGE ID       CREATED         SIZE
docker.io/openshift/hello-openshift   latest   7af3297a3fb4   21 months ago   6.1 MB
```

- Inspect the image: `podman inspect openshift/hello-openshift` and note that:
  - It exposes two ports: 8080 and  8088
  - It runs as user 1001
  - The entry point executable is /hello-openshift

```
[
    {
        "Id": "7af3297a3fb4487b740ed6798163f618e6eddea1ee5fa0ba340329fcae31c8f6",
        "Digest": "sha256:aaea76ff622d2f8bcb32e538e7b3cd0ef6d291953f3e7c9f556c1ba5baf47e2e",
        ...
        "Config": {
            "User": "1001",
            "ExposedPorts": {
                "8080/tcp": {},
                "8888/tcp": {}
            },
            "Env": [
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
            ],
            "Entrypoint": [
                "/hello-openshift"
            ]
            ...
    }
]
```

- Run the image: `podman run --name hello1 -d -p 8080:8080 -p 8888:8888 openshift/hello-openshift`.  Note that:
  - The `--name` option gives the container a name.
  - The `-d` option runs the command in the background as a daemon
  - The `-p` command maps the host port to the port in the container. Through virtual network, the port in the container may be fixed, When you run the container, you may assign a new port on the host dynamically.

- Access the application in the container.
  - If you are running in the server, try `curl http://localhost:8080`.
  - If you are running on a desktop, point your browser to `http://localhost:8080`
  - Also try port 8888

- Run another instance of the same image: `podman run --name hello2 -d -p 8081:8080 -p 8889:8888 openshift/hello-openshift`.  

- Access the application in the new container the same way. However, use port `8881` and `8889` instead.

- Verify there are two containers running in the same host: `podman ps`:

CONTAINER ID  IMAGE                                       COMMAND  CREATED             STATUS             PORTS
                                  NAMES
5d2c82fe3f7c  docker.io/openshift/hello-openshift:latest           5 seconds ago       Up 5 seconds ago   0.0.0.0:8081->
8080/tcp, 0.0.0.0:8889->8888/tcp  hello2
43c70b723756  docker.io/openshift/hello-openshift:latest           About a minute ago  Up 57 seconds ago  0.0.0.0:8080->
8080/tcp                          hello1```

```

- View the logs: `podman logs hello1`

```
serving on 8888
serving on 8080
```

- View the logs on the second container: `podman logs hello2`. 

```
serving on 8888
serving on 8080
```

- To export the file system of a running container: `podman export hello > hello1.tar`

- List the files on the file system: `tar -tvf hello1.tar`. Note that this is a very small image.

```
drwxr-xr-x root/root         0 2020-01-31 20:31 dev/
drwxr-xr-x root/root         0 2020-01-31 20:31 etc/
-rwxr-xr-x root/root         0 2020-01-31 20:31 etc/hostname
-rwxr-xr-x root/root         0 2020-01-31 20:31 etc/hosts
-rwxr-xr-x root/root         0 2020-01-31 20:31 etc/passwd
-rwxr-xr-x root/root         0 2020-01-31 20:31 etc/resolv.conf
-rwxr-xr-x root/root   6089990 2018-04-18 10:22 hello-openshift
drwxr-xr-x root/root         0 2020-01-31 20:31 proc/
drwxr-xr-x root/root         0 2020-01-31 20:31 run/
-rwxr-xr-x root/root         0 2020-01-31 20:31 run/.containerenv
drwxr-xr-x root/root         0 2020-01-31 20:31 run/secrets/
drwxr-xr-x root/root         0 2020-01-31 20:31 sys/
```

- Run another command in the running container. 
The typical use case is to run a shell command, so you can navigate within the container. 
However, for this image there is only one executable. So we'll execute the same command again: `podman exec -ti hello1 /hello-openshift`. 
Running this command again in the same container results in an error, 
because there is already another copy running in the background that is bound to the ports 8080 and 8888:

```
serving on 8888
serving on 8080
panic: ListenAndServe: listen tcp :8888: bind: address already in use
...
```

## Investigate the podman command

- Access the application running in the container from outside the container:
  - If you are running a server machine, use curl to access the port: `curl http://localhost:9080` and ensure you have output.
  - If you are running on a desktop, point your browser to: `http://localhost:9080`

 - Access the logs to your container: `podman logs --tail=0 -f wlp`. The `--tail=0` option lists all entries in the log. Use `Ctrl-C` To exit.

- Remote shell into your running container to poke around: `podman exec -it wlp /bin/sh`
  - run `whoami` and note you're not running as root.
  - cd `/logs` to find the log files
  - cd `/liberty/wlp` to find the location of the liberty install
  - cd `/liberty/wlp/usr/servers/defaultServer` to find the server congiruation. Note that the default server just runs without any application.
  - Exit from the container: `exit`

- Stop the container: `podman stop wlp`

- List the process: `podman ps` and note that it does not show the stopped container.

- List all processes: `podman ps -a` and note that -a option lists all container, including stopped containers.
```
CONTAINER ID  IMAGE                                      COMMAND               CREATED         STATUS
   PORTS                   NAMES
eff4145f8f9a  docker.io/ibmcom/websphere-liberty:latest  /opt/ibm/wlp/bin/...  19 minutes ago  Exited (143) 7 seconds ag
o  0.0.0.0:9080->9080/tcp  wlp
```

- Remove the container: `podman rm wlp`

- List all containers again and verify the stopped container no longer exsits: `podman ps -a`


## Build and Run Your Own Image

Note: This part of the lab requires about 1GB of disk to store your 

To make the build process repeatable, use a `Containerfile`, which contains instructions to create the new layers of the image.

- Investigate the provided `Containerfile`:

   ```
FROM ibmcom/websphere-liberty:kernel-java8-ibmjava-ubi
COPY server.xml  /config
COPY ServletApp.war /config/dropins/app.war
RUN /liberty/wlp/bin/installUtility install /config/server.xml 
 ```

    -- The first line `FROM` specifies the existing image to be used.  If this is not in the local repository, it will be pulled from a remote registry such as docker hub.

   -- The second line `COPY`  is a straight copy of the file `server.xml` from the local directory to `/config/server.xml` in the image. This adds a new layer to the image with the actual server configuration to be used.
   
   - The third line, another `COPY`, copies `ServletApp.war` from the current directory into a new layer in the image you are creating, at the location `/config/dropins/app.war`.
   - The last line runs he `installUtility` command within the image to install additional features required to run the server as specified in `server.xml1

- Run the build: `podman build -t app .`

  ```
  STEP 1: FROM ibmcom/websphere-liberty:kernel-java8-ibmjava-ubi
  ...
Writing manifest to image destination
Storing signatures
STEP 2: COPY server.xml  /config
13d669701aacedee4e1d171c20a9f85c93935fb111107fda36c9013f7f1223ae
STEP 3: COPY ServletApp.war /config/dropins/app.war
b76a7bf9a089754d30a4883243ed21b3820ed60bb827ed2fa9437c63498cd9d0
STEP 4: RUN /liberty/wlp/bin/installUtility install /config/server.xml
Checking for missing features required by the server ...
The server requires the following additional features: servlet-3.0.  Installing features from the repository ...
Establishing a connection to the configured repositories ...
This process might take several minutes to complete.

Successfully connected to all configured repositories.

Preparing assets for installation. This process might take several minutes to complete.

Additional Liberty features must be installed for this server.

To install the additional features, review and accept the feature license agreement:
Step 1 of 4: Downloading servlet-3.0 ...
Step 2 of 4: Installing servlet-3.0 ...
Step 3 of 4: Validating installed fixes ...
Step 4 of 4: Cleaning up temporary files ...


All assets were successfully installed.

Start product validation...
 Product validation completed successfully.
STEP 5: COMMIT app
baa6bb9ad29d4931a92879ae0aac044c689b16585ad2132f8f2ccf663c095f97
```

- List the images: `podman images`

 ```
 REPOSITORY                            TAG                        IMAGE ID       CREATED         SIZE
localhost/app                         latest                     baa6bb9ad29d   2 minutes ago   544 MB
docker.io/ibmcom/websphere-liberty    kernel-java8-ibmjava-ubi   7ea3d0a2b3fe   4 hours ago     513 MB
 ```

- Start the container. Note that you are running with both http and https ports: `podman run -d -p 9080:9080 -p 9443:9443 --name=app-instance app`

- Access the application running in the container:
  - If you are running in a server, use `curl --insecure https://localhost:9443/app` and ensure you have output that looks like: `<html><h1><font color=green>Simple Servlet ran successfully</font></h1>Powered by WebSphere Liberty  <html>`.
  - If you are running on a desktop with browser
    -  point your browser to `http://localhost:9080/app` and check that renders a page showing `Simple Servlet ran successfully`.
    - Also point your browser to `https://localhost:9443/app`

- To demonstrate container images are running at the process level, create a second container, but this time make it listen on ports 9081 and 9444: `podman run -d -p 9081:9080 -p 9444:9443 --name=app-instance1 app`. Note that network isolation allows each container to run on port 9080, but outside the container the actual ports used at 9080 and 9081. 
   - If you are running on a server: `curl --insecure https://localhost:9444/app` and ensure you get the same output as before.
   - If you are running on a desktop with browser, point your browser to `https://localhost:9444/app`.

- List the running containers: `podman ps`

```
  CONTAINER ID  IMAGE                 COMMAND               CREATED             STATUS                 PORTS NAMES
45e82c9cd416  localhost/app:latest  /opt/ibm/wlp/bin/...  About a minute ago  Up About a minute ago  0.0.0.0:9081->9080/ tcp, 0.0.0.0:9444->9443/tcp  app-instance1
cb20ce464c88  localhost/app:latest  /opt/ibm/wlp/bin/...  10 minutes ago      Up 10 minutes ago      0.0.0.0:9080->9080/ tcp                          app-instance
```

- Stop the first instance: `podman stop app-instance`

- Stop the second instance: `podman stop app-instance1`

- Remove the first instance: `podman rm app-instance`

- Remove the second instance: `podman rm app-instance1`

- Congratulations. You have completed the introduction to containerization lab.