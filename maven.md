# Publishing SNAPSHOTs to Maven Central
SNAPSHOTs are published at https://central.sonatype.com. Please note that SNAPSHOTs are for testing only and are deleted after 90 days.


## 1. Set up the namespace (once)
1. Make sure The `groupId` (`org.virtualparts`) from the POM file is a namespace that you own in the Central Portal. Double check at  https://central.sonatype.com/publishing/namespaces.
2. Enable SNAPSHOTs through namespace's menu

## 2. Add your token to Maven's settings (once per computer)

1. Create a token in Maven Central (View Account->Generate User Token) and add teh details given to `~/.m2/settings.xml`.

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>TOKEN_USERNAME</username>
      <password>TOKEN_PASSWORD</password>
    </server>
  </servers>
</settings>
```

You can customise the id value based on the  `publishingServerId` in the file POM.

## 3. POM configuration

These settings are already in the repository; this section explains them.

**Version.** The version must end with `-SNAPSHOT`. For vpr-data, the version is set within the root `pom.xml` and inherited by all modules:

```xml
<groupId>org.virtualparts</groupId>
<artifactId>virtualparts-parent</artifactId>
<version>2.0.18-SNAPSHOT</version>
```

**Publishing plugin.** The root `pom.xml` uses Sonatype's `central-publishing-maven-plugin`.

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.sonatype.central</groupId>
                <artifactId>central-publishing-maven-plugin</artifactId>
                <version>0.12.0</version>
                <extensions>true</extensions>
                <configuration>
                    <publishingServerId>central</publishingServerId>
                    <autoPublish>false</autoPublish>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>

    <plugins>
        <plugin>
            <groupId>org.sonatype.central</groupId>
            <artifactId>central-publishing-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

SNAPSHOTs are uploaded directly. However, you can use the `autoPublish` parameter to verify the releases first and then **Publish** via the Central dashboard.

vpr-data also produces `virtualparts-data-2.0.18-SNAPSHOT-with-dependencies.jar`. But this is not uploaded due to using the `outputFile` property in the POM file. The `createDependencyReducedPom=false` property makes sure library users can access all dependencies:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <!-- Built in target/ only; not installed or deployed -->
                <outputFile>${project.build.directory}/${project.artifactId}-${project.version}-with-dependencies.jar</outputFile>
                <createDependencyReducedPom>false</createDependencyReducedPom>
            </configuration>
        </execution>
    </executions>
</plugin>
```


## Maven uploads
| Module | Uploaded files |
|---|---|
| `virtualparts-parent` | POM |
| `virtualparts-rdf` | jar, POM |
| `virtualparts-sbol` | jar, POM |
| `virtualparts-data` | jar, POM |

## 4. Deploy

Run the following to deploy and to skip testing the SynBioKit token.

```
mvn clean deploy -DskipTests
```

## 5. Verify

SNAPSHOTs do not appear under Deployments in the Central portal. You can use the following to check a snapshot repository instead.

```
https://central.sonatype.com/repository/maven-snapshots/org/virtualparts/virtualparts-data/{VERSION}/maven-metadata.xml
```

For example:
```
https://central.sonatype.com/repository/maven-snapshots/org/virtualparts/virtualparts-data/2.0.18-SNAPSHOT/maven-metadata.xml
```


## Releases

Publishing a release (for example `2.0.18`) additionally requires GPG signatures, source and Javadoc jars, and the license, developers and SCM sections in the POM. A release is uploaded with `mvn clean deploy`, validated by the portal, and then published manually from **Deployments**. Once published, a release cannot be changed or deleted.
