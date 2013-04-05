## How to build

This page describes how to build attester-maven-plugin from sources.

<em>As attester-maven-plugin is not yet included in any public maven repository, this step is mandatory before being
able to use attester-maven-plugin.</em>

### Check dependencies

The following programs have to be installed:

* [maven](http://maven.apache.org)
* [node.js](http://nodejs.org/)

The following program can optionally be used:

* [Git](http://git-scm.com/)

### Download sources from GitHub

The sources of both attester and attester-maven-plugin are needed.

You can either get them with [Git](http://git-scm.com/):

* ``git clone https://github.com/ariatemplates/attester.git``
* ``git clone https://github.com/ariatemplates/attester-maven-plugin.git``

Or you can download the corresponding zip files and extract them to two different directories on your computer:

* [attester](https://github.com/ariatemplates/attester/zipball/master)
* [attester-maven-plugin](https://github.com/ariatemplates/attester-maven-plugin/zipball/master)


### Build attester

* Go to the directory where you extracted attester.
* Run:
<pre>
	cd maven
	mvn clean install
</pre>
* If the build is successful, this should install attester in the local maven repository.

### Install node.js as a maven artifact

* Copy ``node.exe`` from its installation directory (typically ``C:\Program Files\nodejs\node.exe``) to the ``scripts`` sub-directory of the folder
where you extracted attester-maven-plugin.

* Execute the ``installNode.cmd`` script from the same directory.

* Press ENTER to confirm the installation.

### Install PhantomJS as a maven artifact

* Download PhantomJS from [here](https://phantomjs.googlecode.com/files/phantomjs-1.9.0-windows.zip), and extract the ``phantomjs.exe``
file from the zip file in the ``scripts`` sub-directory of the folder where you extracted attester-maven-plugin.

* Execute the ``installPhantomJS.cmd`` script from the same directory.

* Press ENTER to confirm the installation.

### Build attester-maven-plugin

* Go to the directory where you extracted attester-maven-plugin.
* Run:
<pre>
	mvn clean install
</pre>
* If the build is successful, this should install attester-maven-plugin in the local maven repository. You are now ready to [use this plugin](usage.html).
