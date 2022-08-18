# Ordinate
A powerful, platform-agnostic, feature-rich command framework.

## Usage

To learn how to use Ordinate, see the [wiki](https://github.com/Redempt/Ordinate/wiki) and/or read the [javadocs](https://redempt.dev/javadoc/com/github/Redempt/Ordinate-base/index.html).

## Installation

To begin, first select your platform. For example, `Ordinate-spigot`.

The currently supported platforms are:
`Ordinate-spigot`
`Ordinate-sponge`
`Ordinate-base`

Then select your version tag. You can see all available versions [here](https://github.com/Redempt/Ordinate/releases).

### Gradle

```groovy
repositories {
	maven { url = 'https://redempt.dev' }
}
```
```groovy
dependencies {
	implementation 'com.github.Redempt:Ordinate-[platform]:[version]'
}
```

### Maven

```xml
<repository>
        <id>redempt.dev</id>
        <url>https://redempt.dev</url>
</repository>
```
```xml
<dependency>
        <groupId>com.github.Redempt</groupId>
        <artifactId>Ordinate-[platform]</artifactId>
        <version>[version]</version>
        <scope>compile</scope>
</dependency>
```
