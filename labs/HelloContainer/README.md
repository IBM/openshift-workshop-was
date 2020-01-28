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

- List avaiable local images: `podman images`
```
REPOSITORY   TAG   IMAGE ID   CREATED   SIZE
```

- Pull an test image from docker hub:  `podman pull hello-world`
```
Trying to pull registry.access.redhat.com/hello-world...
  name unknown: Repo not found
Trying to pull docker.io/library/hello-world...
...
```

- List local images: `podman images`
```
REPOSITORY                      TAG      IMAGE ID       CREATED         SIZE
docker.io/library/hello-world   latest   fce289e99eb9   13 months ago   8.03 kB
```

- Run the image: `podman run hello-world`. You should expect to see a message about your installation working correctly:

```
Hello from Docker!
This message shows that your installation appears to be working correctly.
...
```

## Investigate the podman command

- Investigate the layers that make up the image: `podman history hello-world`. Note that this image has 2 layers, with two commands. Use the `--no-trunc` option to get non-truncated output.
```
ID             CREATED         CREATED BY                                      SIZE   COMMENT
fce289e99eb9   13 months ago   /bin/sh -c #(nop) CMD ["/hello"]                0B
<missing>      13 months ago   /bin/sh -c #(nop) COPY file:f77490f70ce51d...   977B
```

- Pull a default WebSphere Liberty UBI image. Note that the UBI (Universal Base Image) is a distribution of Red Hat Enterprise Linux that may be hosted on any container registry, not just Red Hat registry: `podman pull ibmcom/websphere-liberty`

- List the history of the  commands used to create the layers in the image: `podman history ibmcom/websphere-liberty`. Use the `--no-trunc` option to not get truncated output. Note the Liberty image has a lot more layers compared to the hello world image.

- Run the image: `podman run -d -p 9080:9080 --name wlp ibmcom/websphere-liberty`. Note the `-d` option runs the container in the background.

- List the running container: `podman ps`
```
CONTAINER ID  IMAGE                                      COMMAND               CREATED         STATUS             PORTS
                  NAMES
eff4145f8f9a  docker.io/ibmcom/websphere-liberty:latest  /opt/ibm/wlp/bin/...  57 seconds ago  Up 56 seconds ago  0.0.0. 0:9080->9080/tcp  wlp
```

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

To make the build process repeatable, use a `Containerfile`, which contains instructions to create the new layers of the image.

- Investigate the provided `Containerfile`:

   ```
    FROM ibmcom/websphere-liberty
    COPY ServletApp.war /config/dropins/app.war
   ```

    -- The first line `FROM` specifies the existing image to be used.  If this is not in the local repository, it will be pulled from a remote registry such as docker hub.

   -- The second line `COPY`  is a straight copy of the file `ServletApp.war` from the directory you are running the build into a new layer in the image you are creating, at the location `/config/dropins/app.war`.

- Run the build: `podman build -t app .`
  ```
  STEP 1: FROM ibmcom/websphere-liberty
  STEP 2: COPY ServletApp.war /config/dropins/app.war
  STEP 3: COMMIT app
  ed48a27f7703f94ce8c615fbbc5db56425a24910b73c878e0cda85e868050bf6
  ```

- List the images: `podman images`

  ```
  REPOSITORY                           TAG      IMAGE ID       CREATED              SIZE
  localhost/app                        latest   ed48a27f7703   About a minute ago   853 MB
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