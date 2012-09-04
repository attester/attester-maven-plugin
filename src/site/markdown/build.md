## How to build

This page describes how to build atjstestrunner-maven-plugin from sources.

<em>As atjstestrunner-maven-plugin is not yet included in any public maven repository, this step is mandatory before being
able to use atjstestrunner-maven-plugin.</em>

### Check dependencies

The following programs have to be installed:

* [maven](http://maven.apache.org)
* [node.js](http://nodejs.org/)

The following program can optionally be used:

* [Git](http://git-scm.com/)

### Download sources from GitHub

The sources of both atjstestrunner-nodejs and atjstestrunner-maven-plugin are needed.

You can either get them with [Git](http://git-scm.com/):

* ``git clone https://github.com/ariatemplates/atjstestrunner-nodejs.git``
* ``git clone https://github.com/ariatemplates/atjstestrunner-maven-plugin.git``

Or you can download the corresponding zip files and extract them to two different directories on your computer:

* [atjstestrunner-nodejs](https://github.com/ariatemplates/atjstestrunner-nodejs/zipball/master)
* [atjstestrunner-maven-plugin](https://github.com/ariatemplates/atjstestrunner-maven-plugin/zipball/master)


### Build atjstestrunner-nodejs

* Go to the directory where you extracted atjstestrunner-nodejs.
* Run:
<pre>
	cd maven
	mvn clean install
</pre>
* If the build is successful, this should install atjstestrunner-nodejs in the local maven repository.

### Install node.js as a maven artifact

* Copy ``node.exe`` from its installation directory (typically ``C:\Program Files\nodejs\node.exe``) to the ``scripts`` sub-directory of the folder
where you extracted atjstestrunner-maven-plugin.

* Execute the ``installNode.cmd`` script from the same directory.

* Press ENTER to confirm the installation.

### Install PhantomJS as a maven artifact

* Download PhantomJS from [here](http://phantomjs.googlecode.com/files/phantomjs-1.6.1-win32-static.zip) and save it as ``phantomjs-1.6.0-win32-static.zip``
in the ``scripts`` sub-directory of the folder where you extracted atjstestrunner-maven-plugin.

* Execute the ``installPhantomJS.cmd`` script from the same directory.

* Press ENTER to confirm the installation.

### Build atjstestrunner-maven-plugin

* Go to the directory where you extracted atjstestrunner-maven-plugin.
* Run:
<pre>
	mvn clean install
</pre>
* If the build is successful, this should install atjstestrunner-maven-plugin in the local maven repository. You are now ready to [use this plugin](usage.html).
