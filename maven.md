# Publishing to Maven Central
Start with the SNAPSHOT. SNAPSHOTs are published at https://central.sonatype.com. Please note that SNAPSHOTs are for testing only and are deleted after 90 days.


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


## Publishing a release

After the previous steps, continue with the following steps:

### 1. Create a GPG key (once per computer)

Create an RSA key:
```
gpg --full-generate-key
```

Extract the fingerprint:
```
gpg --list-secret-keys --keyid-format short
```

Publish the public key, using the fingerprint shown under the `sec` line. 
```
gpg --keyserver hkps://keyserver.ubuntu.com:443 --send-keys FINGERPRINT
```

This may fail! Then try extracting your public key first:
```
gpg --armor --export FINGERPRINT > public-key.asc
```

 Then paste the file content at https://keyserver.ubuntu.com (**Submit Key**). 
 
 To verify that the key is published:

```
curl -s "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xFINGERPRINT" | gpg --show-keys
```

### 2. Required files and POM metadata

These are already in the repository.

- Create the `LICENSE` (Apache License 2.0) and `NOTICE` files in the root folder.
- Complete the `url`, `licenses`, `developers` and `scm` sections in the root `pom.xml`:

```xml
<url>https://github.com/dissys/vpr-data</url>

<licenses>
    <license>
        <name>Apache License, Version 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
    </license>
</licenses>

<developers>
    <developer>
        <name>Goksel Misirli</name>
        <url>https://github.com/dissys</url>
    </developer>
</developers>

<scm>
    <url>https://github.com/dissys/vpr-data</url>
    <connection>scm:git:https://github.com/dissys/vpr-data.git</connection>
    <developerConnection>scm:git:git@github.com:dissys/vpr-data.git</developerConnection>
</scm>
```

- Add a `release` profile in the root `pom.xml` to build:
  *  build `-sources.jar` (`maven-source-plugin`)
  *  build `-javadoc.jar` (`maven-javadoc-plugin`)
  *  sign all files (`maven-gpg-plugin`)
  
- Double check the creation of Javadoc locally:  
```
mvn compile javadoc:javadoc
```
and check the outputs:  `target/reports/apidocs/index.html`.

- Check if the release builds without GPG signing:

```
mvn clean verify -P release -DskipTests -Dgpg.skip
```

### 3. Deploy the release

With git flow, prepare the release on the release branch (for example version 2.0.18):

Update the release version:
```
mvn versions:set -DnewVersion=2.0.18 -DgenerateBackupPoms=false
```

Finally, deploy.
If using MacOS, 
```
mvn clean deploy -P release -DskipTests
```

If you receive the "Inappropriate ioctl for device" error then type the following and try deploying again. This happens when GPG is unable to ask for the GPG passphrase. As a Mac user, i had this error!

```
export GPG_TTY=$(tty)
mvn clean deploy -P release -DskipTests
```

This will upload to Maven Central (`Deployments` at https://central.sonatype.com) due to having the `autoPublish` parameter set to `false`. Go to your Account and select "View Deployments".

 When the status becomes ``Validated``, click ``Publish``. The release should be available on Maven Central, at https://repo1.maven.org/maven2/org/virtualparts/virtualparts-data/.

 This took 11 minutes for me!!!

### 4. Finish the release
Do not forget to finish the release. I use git flow, so:
```
git flow release finish 2.0.18
git push origin main develop --tags
```
