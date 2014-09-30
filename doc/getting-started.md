Getting Started
===============

Installation
------------

You need a [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java SE Downloads")
installed, but it is not necessary to install Groovy because it's bundled with
the Gaiden distribution.

Installing Gaiden on Unix based systems is easiest with [GVM](http://gvmtool.net/):

```sh
$ gvm install gaiden
```

Or download [a binary distribution of Gaiden](https://github.com/kobo/gaiden/releases)
and unpack it into some file on your local file system. Add the `bin` directory
path in unpacked the distribution to your `PATH` environment variable.


Create Project
--------------

To create a new project, you can use the `create-project` command.
This command requires a project name as arguments as follows:

```
$ gaiden create-project hello-gaiden
```


Project Structure
-----------------

The project created by `create-project` will be as follows:

```
<PROJECT ROOT>
├── README.md
├── config.groovy
├── pages.groovy
├── gaidenw
├── gaidenw.bat
└── wrapper
    ├── gaiden-wrapper.jar
    └── gaiden-wrapper.properties
```

config.groovy
:   The configuration file of the project.
    Please see [Configuration](configuration.md) for more details.

pages.groovy
:   This is a file to define a list of pages.
    It is used for defining a metadata or generating a table of contents.
    Please see [Configuration](configuration.md) for more details.

gaidenw(.bat), wrapper
:   The resources for Gaiden Wrapper.
    The Gaiden Wrapper allows you to run Gaiden commands in a project without
    installing Gaiden first. This concept inspired from [Gradle](http://www.gradle.org/).
    The wrapper is a batch script on Windows, and a shell script for other
    operating systems. It can be used as follows:

    ```
    $ ./gaidenw build
    ```

    When you run a Gaiden command via the wrapper, Gaiden will be automatically
    downloaded and used to run the command.


Writing a page
--------------

To write a page, you can use the Markdown.
If you don't know the Markdown, please refer to
[Markdown Syntax Guide](Markdown Syntax Guide) at Daring Firebal.

These pages can be located in under the source directory.
The source directory is set the project root directory by default.

Let's create a new page called `my-page.md` in your project root directory.

```
# My Page
```

And add the page to `pages.groovy` as follows:

```
"README.md"
"my-page.md"
```

The headers of the page will be included in a table of content by adding to
`pages.groovy`.


Building a document
-------------------

To build a document, you can use the `build` command in your project root
directory as follows.

```
$ gaiden build
```

Executing `gaiden build` generates document to a output directory.
Now you can see the generated documentation in a web browser by opening the
html file located in the output directory.
