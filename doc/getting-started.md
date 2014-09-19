Getting Started
===============

Installation
------------

You need a [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java SE Downloads")
installed, but it is not necessary to install Groovy because it's bundled with
the Gaiden distribution.

Installing Gainden on Unix based systems is easiest with [GVM](http://gvmtool.net/):

```sh
$ gvm install gaiden
```

Or download [a binary distribution of Gaiden](https://github.com/kobo/gaiden/releases)
and unpack it into some file on your local file system. Add the `bin` directory
path in unpacked the distribution to your `PATH` environment variable.



Then in a shell, type the following:

```sh
$ gaiden create-project sample-project
$ cd sample-project
$ gaiden build
```

You can see the documentation built when you open `build/index.html` file in your
web browser.

