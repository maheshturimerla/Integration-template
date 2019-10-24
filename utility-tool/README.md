# {{functionalDomain}}-{{serviceDomain}}-{{artifactId}}

{{serviceDescription}}

<!-- This section describes the service, providing an overview of the -->
<!-- purpose and function, as well as specific limitations or exclusions if -->
<!-- helpful (e.g. this service does not do 'x', see service 'xyz'). -->



Show how to call the service as concisely as possible. Developers
should be able to figure out how the service solves their problem by
looking at the code example. Make sure the API you describe is the
obvious and main use case for the service, and that your example code
is short and concise.

``` http
{{kube_url}}/api/v1/proxy/namespaces/{{functionalDomain}}-{{serviceDomain}}/services/{{artifactId}}/service/home
```



List all dependencies and prerequisites for the project.



List specific issues that will have an impact on the roll-out of this
service. For example, if new versions of this service can only be
deployed to production during off-hours, etc.



List the manual tests that are required to certify this service - if
any - and link to the additional test details.



Links to additional documentation, if any.



** Developers: **

Owner: {{developer}}

**Jenkins Job:** {{jenkinsjob_url}}/job/{{jenkins-joblink}}

**ICD:** {{kube_url}}/api/v1/proxy/namespaces/{{functionalDomain}}-{{serviceDomain}}/services/{{artifactId}}/icd

**Documentation:** {{kube_url}}/api/v1/proxy/namespaces/default/services/{{artifactId}}

**Service:** {{kube_url}}/api/v1/proxy/namespaces/{{functionalDomain}}-{{serviceDomain}}/services/{{artifactId}}/service

** JIRA:** http://egjira.dtvops.net - Bug and features tracking

** Clouds Dashboard:** https://cloudops.dtveng.net - Relevant links to every component in the platform

````bash
$ java -jar ./target/{your-project-compiled}.jar
````

Build and test
````bash
$ mvn clean install
````

Create Docker container
````bash
$ mvn docker
````