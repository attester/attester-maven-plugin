## Introduction

This plugin provides the capacity to run [atjstestrunner](https://github.com/ariatemplates/atjstestrunner-nodejs)
as part of a maven build.

This allows to easily run Javascript unit tests during a maven build (just like Java unit tests), with the following features:

* Code coverage
* Fully headless tests with [PhantomJS](http://phantomjs.org)
* Integration with [Sonar](http://www.sonarsource.org/)
* Tests can be run in parallel

You can check the [features of atjstestrunner](https://github.com/ariatemplates/atjstestrunner-nodejs#features) for more information.

Even if [atjstestrunner](https://github.com/ariatemplates/atjstestrunner-nodejs) depends on [node.js](http://nodejs.org) and is able to use [PhantomJS](http://phantomjs.org),
this maven plugin does not require those programs to be pre-installed on the build machine, they simply have to be available in the maven repository and they will be downloaded as
maven artifacts.

<em>Note: this maven plugin currently only references the Windows version of the [node.js](http://nodejs.org) and [PhantomJS](http://phantomjs.org) artifacts.
Moreover, it was only tested on Windows. However, support for other environments may be added in the future, as [atjstestrunner](https://github.com/ariatemplates/atjstestrunner-nodejs)
is platform-independent, and both [node.js](http://nodejs.org) and [PhantomJS](http://phantomjs.org) are compatible with several platforms.</em>

### Goals overview

Here are the main goals provided by this maven plugin:

* [atjstestrunner:test](test-mojo.html) : Runs Javascript tests in a fully automated way and writes result and coverage reports.
* [atjstestrunner:run](run-mojo.html) : Starts an internal web server with the same configuration as the [atjstestrunner:test](test-mojo.html) goal
but waits until it is manually stopped. This allows to run tests manually in any browser, and to debug failing tests.

Additionally, the following more general goals are available, because their functionality was needed to implement the main goals:

* [atjstestrunner:node](node-mojo.html) : Runs any [node.js](http://nodejs.org) program. An error is raised if the exit code is different from 0.
* [atjstestrunner:extract-dependencies](extract-dependencies-mojo.html) : Extracts zip artifacts in a temporary directory named according to the artifact. If an artifact is already extracted, it is not extracted again.

### Usage

General instructions on how to use this plugin can be found on the [usage page](usage.html).

To report issues, bugs and ask for new features, please [open an issue in GitHub](https://github.com/ariatemplates/atjstestrunner-maven-plugin/issues).

