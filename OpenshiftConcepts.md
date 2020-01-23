#  Openshift Concepts for WebSphere Administrators

In this chapter, we introduce the most important concepts and processes for working with Openshift. The prerequisite is that you already familiar with the following concepts:
- image
- container
- container orchestration

If you are not, please refer to the previous chapter.

We will present:
- Resource model
- Controller model and declarative specification 
- Custom Resources and Operators
- Immutable images
- Automation

## Resource Model

Note the terminology used to describe the environment being managed: 
| environment |  terminology |
|-------------|----------|
| Openshift  | cluster  |
| traditional WebSphere | cell |
| Liberty     | collective |

In traditional WebSphere, the resource model is specified via WCCM, or WebSphere Common Configure Model. 
When you manage WebSphere through scripting or the admin console, you are making changes to the WCCM model. The model is XML based, and persisted on disk as XML files. 
For example, when you create a cluster or server, there is a corresponding cluster.xml or server.xml stored in the configuration repository. Here is a sample server.xml, with much content redacted:

WebSphere Liberty has its own XML based configuration model. However, it is simpler, and commonly stored in server.xml in the server directory, together with the application. 


Resources in Openshift are stored in JSON format. 
Each resource type is identified by a group, version, and kind. 
Each resource instance for a resource type also has a unique name. 
A group that has no name (empty string) is built-in to Openshift. 
Here are examples of some resource types:

| group  | version | kind |  description  |
|--------|---------|------|---------------|
|        | v1      | Namespace| for administration isolation |
|        | v1      | Pod      | for running containers |
|        | v1      | Deployment | to deploy an image |
|        | v1      | ConfigMap  | configuration customization
|        | v1      | Service| logical endpoint for routing in the cluster
|        | v1      | Route | exposes endpoint access from outside of built-in firewall
| openliberty.io| v1beta1 | OpenLibertyApplication | Deploy an Liberty image |


Recall that Openshift may be used to manage a very large environment.  
Namespaces are used to allow you to isolate disparate resources  within the environment, e.g. between different teams, or applications.  
Resource instances may be scoped to a namespace, or be cluster wide if they are applicable to the entire cluster.

Here is a sample redacted configuration for a Pod:
```
apiVersion: v1
kind: Pod
metadata:
  name: demo-app-7c4f775c8b-8xn2n
  namespace: default
spec:
  containers:
  - env:
    - name: WLP_LOGGING_CONSOLE_LOGLEVEL
      value: info
    - name: WLP_LOGGING_CONSOLE_SOURCE
      value: message,trace,accessLog,ffdc
    - name: WLP_LOGGING_CONSOLE_FORMAT
      value: json
    image: openliberty/open-liberty:full-java8-openj9-ubi
    ports:
    - containerPort: 9080
      name: 9080-tcp
      protocol: TCP
  imagePullSecrets:
  - name: demo-app-dockercfg-657tj
status:
  conditions:
  - lastProbeTime: null
    lastTransitionTime: "2020-01-23T16:05:32Z"
    status: "True"
    type: Initialized
  - containerID: cri-o://9d2d4fcdfcea0c4b3a968daf412b6216b76d1e779eb1364313395caf6f41997f
    image: docker.io/openliberty/open-liberty:full-java8-openj9-ubi
    imageID: docker.io/openliberty/open-liberty@sha256:0bb44ef72d851b3fac9adeea36ba35f6f9a327708b02ec8fd70659d3429a0530
    lastState: {}
    name: app
    ready: true
    restartCount: 0
    started: true
    state:
      running:
        startedAt: "2020-01-23T16:06:06Z"
  hostIP: 10.16.28.115
  phase: Running
  podIP: 10.254.4.23
  podIPs:
  - ip: 10.254.4.23
  qosClass: BestEffort
  startTime: "2020-01-23T16:05:32Z"
```

Note that a Pod:
- is scoped to a namespace.
- defines where to fetch and run the container image. (`image`)
- defines which ports to expose
- contains a sub-resource `status` that describes the current status of the pod.

## Controller Model and Declarative Specification

The administration model for WebSphere is command based. 
You provide the step by step instructions either via a script or through interaction with the graphical console. 
On the other hand, almost all commands in Openshift is declarative. 
You provide the specification of what you want in JSON or YAML, and Openshift will do its best to honor that specification. 

A good analogy to explain the controller model is a thermostat. 
If the specification for the thermostat is 70 degrees Fahrenheit as the desired temperature, 
a controller within the thermostat compares the current temperature to the desired temperature, 
and switches on heating or cooling to reach the desired temperature. 
If you change specification to a different temperature, the controller reacts to the change by switching on heating or cooling to reach the new temperature. 
Note that the controller tries to meet the specification on a best effort basis.
It can not guarantee that the desired temperature will always be reached.

Similarly there is a controller for every resource type within Openshift. It monitors for changes as follows:
- resource added: performs the operations required to add the resource. For example, in the case of a pod, starts container(s) by finding where a new pod may be created, and fetching image(s) to run new containers within the pod.
- resource modified: Determines the differences between the current state of the resource, and the new desired state, and performs actions needed to bring the resource to the new desired state. For example, if any part of the Pod specification is changed, such as image location, or environment variables, the controller determines what changes are required, and carries them out.
- resource deleted: Deletes the underlying resource. In the case of a Pod, the hardware resources required to run the container(s) are release.

# Custom Resources and Operators
