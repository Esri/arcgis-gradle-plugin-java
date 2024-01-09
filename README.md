# arcgis-gradle-plugin-java

## Features

This plugin simplifies the development of Gradle projects that use the
[ArcGIS Maps SDK for Java](https://developers.arcgis.com/java/), by automating the necessary dependencies.

## Instructions

### Adding the plugin to a project

Open the project you want to apply the plugin to. In your `build.gradle`, add the plugin to the `plugins` block:
```groovy
plugins {
  id 'com.esri.arcgis-java-plugin' version '0.1.0'
}
```

Add an `arcgis` block to `build.gradle`, specifying the version of the ArcGIS Maps SDK for Java you want to use, for 
example:
```groovy
arcgis {
  version = '200.3.0'
}
```

### Updating existing projects to use this plugin

If you added the ArcGIS Maps SDK for Java to your project before this plugin was available, you can now delete the
following things that you previously added manually to `build.gradle`:
1. The `copyNatives` task, and the `dependsOn copyNatives` line in the `run` block:
```groovy
task copyNatives(type: Copy) {
  description = "Copies the arcgis native libraries into the .arcgis directory for development."
  group = "build"
  configurations.natives.asFileTree.each {
    from(zipTree(it))
  }
  into "${System.properties.getProperty("user.home")}/.arcgis/$arcgisVersion"
}

run {
  dependsOn copyNatives
}
```
2. The `natives` configuration:
```groovy
configurations {
  natives
}
```
3. The `ext` block (its functionality is provided by the `arcgis` block mentioned above):
```groovy
ext {
  arcgisVersion = '200.1.0'
}
```
4. These `arcgis-java` dependencies from the `dependencies` block:
```groovy
    implementation "com.esri.arcgisruntime:arcgis-java:$arcgisVersion"
    natives "com.esri.arcgisruntime:arcgis-java-jnilibs:$arcgisVersion"
    natives "com.esri.arcgisruntime:arcgis-java-resources:$arcgisVersion"
```

## Requirements

See [System Requirements for ArcGIS Maps SDK for Java](https://developers.arcgis.com/java/reference/system-requirements/).

## Resources

* [ArcGIS Maps SDK for Java](https://developers.arcgis.com/java/)
* [ArcGIS Maps SDK for Java Resources](https://developers.arcgis.com/java/resources/)

## Issues

Find a bug in this Plugin or want to request a new feature?  Please let us know by submitting an issue in this
repository.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing

Copyright 2023 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's 
[license.txt](https://github.com/ArcGIS/arcgis-gradle-plugin-java/blob/main/license.txt) file.