# Jenkins Plugin Example with Unit Test 

This project is sample Jenkins Plugin Example which shows OOO on the Jenkins Job main page. 
You will see how to create Jenkins Plugin project and how to write unit test for it. 

## Getting Started

First, you need to update ~/.m2/settings.xml to download dependencies related to Jenkins as follows.

```
<settings>
  <pluginGroups>
    <pluginGroup>org.jenkins-ci.tools</pluginGroup>
  </pluginGroups>

  <profiles>
    <!-- Give access to Jenkins plugins -->
    <profile>
      <id>jenkins</id>
      <activation>
        <activeByDefault>true</activeByDefault> <!-- change this to false, if you don't like to have it on per default -->
      </activation>
      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <mirrors>
    <mirror>
      <id>repo.jenkins-ci.org</id>
      <url>https://repo.jenkins-ci.org/public/</url>
      <mirrorOf>m.g.o-public</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

Second, run the following command to test the plugin. 
```
$ export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n"
$ mvn hpi:run
```
After Jenkins is up, you can access Jenkins at http://localhost:8080.

If you want to run it Debug mode, you should enter following command.
```
$ mvnDebug hpi:run
```

### Prerequisites

If you want to run this example, you need to install JDK and maven first.


### Installing

You can create .hpi file which can be installed as a Jenkins Plugin from maven install command.


```
$ mvn install
```

That command will generate .hpi file at /target/ folder. You can also use mvn package command to create .hpi file.


## Running the tests

Just run Java Unit test

```
$ mvn test
```

## Deployment

If you got to this point, you should definitely consider hosting your plugin on jenkins-ci.org. Move on to this document for how to do that. This includes the instructions for releasing the plugin.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Kyoungwook Park** - *Initial work* - [sirius2k](https://github.com/sirius2k)

See also the list of [contributors](https://github.com/sirius2k/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* TBD

