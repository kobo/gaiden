# Gaiden

Gaiden is a tool that makes it easy to create documentation with Markdown.
It assumes familiarity with the command-line.
The [Groovy](http://groovy.codehaus.org/ "Groovy - Home") language is the base of the tool.


## Getting Started

You need a [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java SE Downloads") installed, but it is not necessary to install Groovy because it's bundled with the Gaiden distribution.

Installing Gainden on Unix based systems is easiest with [GVM](http://gvmtool.net/):

```sh
$ gvm install gaiden
```

Or download [a binary distribution of Gaiden](https://github.com/kobo/gaiden/releases) and unpack it into some file on your local file system.
Add the `bin` directory path in unpacked the distribution to your `PATH` environment variable.

Then in a shell, type the following:

```sh
$ gaiden create-project sample-project
$ cd sample-project
$ gaiden build
```

You can see the documentation built when you open `build/index.html` file in your web browser.


## Commands

The basic usage is:

```sh
$ gaiden <command name>
```

<dl>
  <dt>create-project &lt;project name&gt;</dt>
  <dd>
    The starting point for Gaiden.
    The <code>create-project</code> command creates the project directory using the <em>&lt;project name&gt;</em> specified by user.
  </dd>

  <dt>build</dt>
  <dd>The <code>build</code> command builds HTML pages from Markdown files using templates, and copies static resources.</dd>

  <dt>clean</dt>
  <dd>The <code>clean</code> command deletes all built and copied resources.</dd>
</dl>

## Project Structure

* `build/` - Built documents and static resources will be placed under this directory.
* `pages/` - The place of source Markdown files. The directory layout will be kept when it is placed in the `build` directory.
    * `toc.groovy` - The table of contents written by DSL. See [ToC](#toc).
* `static/` - The place of static resources. All of the files and subdirectories will be copied under the `build` directory.
* `templates/` - The place of template files. See [Template](#template).
* `GaidenConfig.groovy` - The configuration file. See [Configuration](#configuration).


## Template

The cool template is provided by default in Gaiden.
Alternatively, if you don't like it very much, you can customize the template.
To customize the template, you will need to edit the `templates/layout.html` file.

The most simple template is the following:

```html
<html>
<head>
    <title>$title</title>
</head>
<body>
    $content
</body>
</html>
```

You can use  extension properties and methods which start with `$` in the template.
It can be used to refer to variables or run helper methods.


### Extension Property and Method

Gaiden supports the following extension properties and methods by default:

<dl>
  <dt>title</dt>
  <dd>
    A title of a documentation which can be configured in <code>GaidenConfig.groovy</code>:
    <pre>
&lt;title&gt;$title&lt;/title&gt;
</pre>
  </dd>

  <dt>content</dt>
  <dd>
    A content of html which is generated from a Markdown source in the <code>pages</code> directory:
  <pre>
&lt;body&gt;
    $content
&lt;/body&gt;
</pre>
  </dd>

  <dt>resource</dt>
  <dd>
    Creates a link to the static resources, e.g. image, css and javascript:
    <pre>
&lt;html&gt;
&lt;head&gt;
    ...
    &lt;link rel="stylesheet" href="${resource('/css/main.css')}"&gt;
    &lt;script src="${resource('/js/jquery.min.js')}"&gt;&lt;/script&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;img src="${resource('/img/logo.jpg')}"/&gt;
    ...
&lt;/body&gt;
&lt;/html&gt;
</pre>
  </dd>

  <dt>tocPath</dt>
  <dd>
    Create a link to the table of contents page:
    <pre>
&lt;a href="$tocPath"&gt;Table of Contents&lt;/a&gt;
</pre>
  </dd>
</dl>


## Configuration

Gaiden provides settings for generating the document in 'GaidenConfig.groovy'.
You can customize behavior by changing settings.

<dl>
  <dt>title</dt>
  <dd>Base title of page.</dd>

  <dt>tocTitle</dt>
  <dd>The title of TOC.</dd>

  <dt>templateFilePath</dt>
  <dd>
    <p>The path of template file.</p>
    <p><strong>default:</strong> <code>templates/layout.html</code></p>
  </dd>

  <dt>tocFilePath</dt>
  <dd>
    <p>The path of TOC file.</p>
    <p><strong>default:</strong> <code>pages/toc.groovy</code>
  </dd>

  <dt>tocOutputFilePath</dt>
  <dd>
    <p>The path of TOC output file.</p>
    <p><strong>default:</strong> <code>toc.html</code>
  </dd>

  <dt>pagesDirectoryPath</dt>
  <dd>
    <p>The path of page source files directory.</p>
    <p><strong>default:</strong> <code>pages</code>
  </dd>

  <dt>staticDirectoryPath</dt>
  <dd>
    <p>The path of static files directory.</p>
    <p><strong>default:</strong> <code>static</code>
  </dd>

  <dt>outputDirectoryPath</dt>
  <dd>
    <p>The path of directory to be outputted a document.</p>
    <p><strong>default:</strong> <code>build</code>
  </dd>

  <dt>inputEncoding</dt>
  <dd>
    <p>The encoding of input Markdown resource.</p>
    <p><strong>default:</strong> <code>UTF-8</code>
  </dd>

  <dt>outputEncoding</dt>
  <dd>
    <p>The encoding of output document.</p>
    <p><strong>default:</strong> <code>UTF-8</code>
  </dd>

  <dt>templatePath (deprecated)</dt>
  <dd>
    <p>Please use <code>templateFilePath</code> instead.</p>
  </dd>

  <dt>tocPath (deprecated)</dt>
  <dd>
    <p>Please use <code>tocFilePath</code> instead.</p>
  </dd>

  <dt>tocOutputPath (deprecated)</dt>
  <dd>
    <p>Please use <code>tocOutputFilePath</code> instead.</p>
  </dd>

  <dt>pagesDirectory (deprecated)</dt>
  <dd>
    <p>Please use <code>pagesDirectoryPath</code> instead.</p>
  </dd>

  <dt>staticDirectory (deprecated)</dt>
  <dd>
    <p>Please use <code>staticDirectoryPath</code> instead.</p>
  </dd>

  <dt>outputDirectory (deprecated)</dt>
  <dd>
    <p>Please use <code>outputDirectoryPath</code> instead.</p>
  </dd>
</dl>

**NOTE**: The encoding of `GaidenConfig.groovy` must be same as the `file.encoding` system property.
If you want to use another encoding, you can use `JAVA_OPTS` or `GAIDEN_OPTS` to pass JVM options to Gaiden as follows, for Unix:

```sh
$ export GAIDEN_OPTS="-Dfile.encoding=Shift_JIS"
```

for Windows:

```
> set GAIDEN_OPTS="-Dfile.encoding=UTF-8"
```


## ToC

Create `pages/toc.groovy` file which defines document structure if you need a table of contents.
You can write it with DSL support as follows:

```groovy
"introduction.md"(title: "Introduction")
"quickstart/quickstart.md"(title: "Quick Start")
```

This file also defines the section titles using the `title` attribute.
If you have nested pages, you can use block:

```groovy
"introduction.md"(title: "Introduction") {
    "introduction/whatis.md"(title: "What is Gaiden?")
    "introduction/install.md"(title: "Install")
}
"quickstart/quickstart.md"(title: "Quick Start") {
    "quickstart/addingcontent.md"(title: "Adding content")
}
```


## Markdown

If you don't know the Markdown, please refer to [Markdown Syntax Guide](http://daringfireball.net/projects/markdown/syntax) at Daring Firebal.

Gaiden uses [PegDown](https://github.com/sirthias/pegdown) as a Markdwon library under the hood.
In addition, Gaiden provides some syntax support.


### Image

You can use an absolute path from a static resource directory in a path of image syntax.
For example:

```markdown
![alt text](/img/some-image.png)
```

Gaiden automatically generates relative a path from an absolute path which is specified.


## License

Gaiden is licensed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html "Apache License, Version 2.0")
