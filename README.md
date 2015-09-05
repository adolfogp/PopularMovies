# Popular Movies

An Android application that displays a list of movies and details about them. The
information is retrieved from [themoviedatabase.org](https://www.themoviedb.org/),
using their RESTful API. So, in order to build and use this project, you'll need
to [get a key](https://www.themoviedb.org/documentation/api) and place it in the
configuration properties file `app/src/main/res/raw/configuration.xml`.

This application was developed as an assignment for [Udacity's Android Developer
Nanodegree](https://www.udacity.com).

## Configuration

Before you build the application, edit `app/src/main/res/raw/configuration.xml`
and add your access key for themoviedatabase.org's RESTful API. For example:

    ...
    <entry key="apikey.themoviedb">4N4W350M3K3Y</entry>
    ...

## Building the application

### Requirements:

* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads).
  This project uses Java 8's lambda syntax, so a lower version may not be used.
* [Android SDK](https://developer.android.com/sdk/installing/index.html)

### Compiling and installing

This project uses [Gradle](https://gradle.org/) and the different build tasks may
be executed using the provided Gradle Wrapper scripts. For example, to list the
available tasks you may execute:

    $ ./gradlew tasks

You may create the [APK](https://developer.android.com/tools/building/index.html)
by invoking:

    $ ./gradlew assembleDebug
or

    $ ./gradlew build

The resulting APK may be found at `app/build/outputs`.

## Frameworks and libraries used

This application uses the following frameworks and libraries:

* [Data Binding Library]
  (https://developer.android.com/tools/data-binding/guide.html) to follow the
  Model-View-ViewModel pattern. More information can be found in the following
  articles:
    - [Android databinding: Goodbye presenter, hello ViewModel!]
      (http://tech.vg.no/2015/07/17/android-databinding-goodbye-presenter-hello-viewmodel/)
    - [Don't forget the View Model!]
      (http://tech.vg.no/2015/04/06/dont-forget-the-view-model/)
* [Retrofit](http://square.github.io/retrofit/) to consume the RESTful API.
* [Picasso](http://square.github.io/picasso/) to download, cache and display images.
* [Dagger 2](http://google.github.io/dagger/) for dependency injection.
* [Retrolambda](https://github.com/orfjackal/retrolambda) in order to use
  Java 8's lambda syntax.
* [Commons Lang](https://commons.apache.org/proper/commons-lang/) for common
  tasks, like implementing `equals`, `hashCode` and `toString`.

## Licencing and attributions

For licencing information, read `LICENSE.txt` and `NOTICE.txt`.

    Copyright 2015 Jesús Adolfo García Pasquel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
