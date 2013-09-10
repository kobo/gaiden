# Gaiden

Gaiden is a tool that makes it easy to create documentation with Markdown.
It assumes familiarity with the command-line.
The [Groovy](http://groovy.codehaus.org/ "Groovy - Home") language the base of the tool.


## Getting Started

You need a [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java SE Downloads") installed, but it is not necessary to install Groovy because it's bundled with the Gaiden distribution.

To install Gaiden, Download a binary distribution of Gaiden and unpack it into some file on your local file system.
Add the `bin` directory path in unpacked the distribution to your `PATH` environment variable.
Then in a shell, type the following:

```
gaiden create-project sample-project
cd sample-project
gaiden build
```


## Commands

The basic usage scenario is:

```
gaiden <command name>
```

create-project &lt;project name&gt;
: The starting point for Gaiden.
  The `create-project` command creates the project directory using the _&lt;project name&gt;_ specified by user.

build
: The `build` command builds HTML pages from Markdown files using templates, and copies static resources.

clean
: The `clean` command deletes all built and copied resources.


## Project Structure

* `build/` - Built documents and static resources will be placed under this directory.
* `pages/` - The place of source Markdown files. The directory layout will be kept when it is placed in the `build` directory.
    * `toc.groovy` - The table of contents written by DSL. See [ToC](#toc).
* `static/` - The place of static resources. All of the files and subdirectories will be copied under the `build` directory.
* `templates/` - The place of template files. See [Template](#template).
* `GaidenConfig.groovy` - The configuration file. See [Configuration](#configuration).


## Template

The cool template is provided by default in Gaiden.
Alternatively, If you are not sure you can customize the template.
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

You can use  extension properties and methods which start with '$' in the template.
It can be used to reference to variables or run helper methods.

### Extension Property and Method

Gaiden supports the following extension properties and methods:

title
:   A title of documents which is configured in `GaidenConfig.groovy`:
    ```
    <title>$title</title>
    ```

content
:   A content of html which is generated from a markdown source in the pages directory:
    ```
    <body>
        $content
    </body>
    ```

link
:   Creates a link to the static resources, e.g. image, css and javascript:
    ```
    <html>
    <head>
        ...
        <link rel="stylesheet" href="${link('/css/main.css')}">
        <script src="${link('/js/jquery.min.js')}"></script>
    </head>
    <body>
        <img src="${link('/img/logo.jpg')}"/>
        ...
    </body>
    </html>
    ```

tocList
:   Create a link to the table of contents page:
    ```
    <a href="$tocLink">Table of Contents</a>
    ```

## Configuration

Gaiden provides settings for generating the document in 'GaidenConfig.groovy'.
You can customize behavior by changing settings.

title
:   Base title of page.

templatePath
:   Path of template file.

    **default:** `templates/layout.html`

tocPath
:   Path of TOC file.

    **default:** `pages/toc.groovy`

tocOutputPath
:   Path of TOC output file.

    **default:** `toc.html`

pagesDirectory
:   Directory of page source files.

    **default:** `pages`

staticDirectory
:   Directory of static files.

    **default:** `static`

outputDirectory
:   Directory to be outputted a document.

    **default :** `build`

inputEncoding
:   Encoding of input markdown resource.

    **default :** `UTF-8`

outputEncoding
:   Encoding of output document.

    **default :** `UTF-8`

## ToC

Create `pages/toc.groovy` file which defines document structure, if you need a table of contents.
You can write it with dsl support following like:

```
"introduction.html"(title: "Introduction")
"quickstart/quickstart.html"(title: "Quick Start")
```

This file also defines the section titles using the `title` attribute.
If you have nested documents, you can use block:

```
"introduction.html"(title: "Introduction") {
    "introduction/whatis.html"(title: "What is Gaiden?")
    "introduction/install.html"(title: "Install")
}
"quickstart/quickstart.html"(title: "Quick Start") {
    "quickstart/addingcontent.html"(title: "Adding content")
}
```

## Markdown

If you don't know the Markdown, please refer to [Markdown Syntax Guide](http://daringfireball.net/projects/markdown/syntax) at Daring Firebal.

Gaiden uses [PegDown](https://github.com/sirthias/pegdown) as a markdwon library under the hood.
In addition, Gaiden provides some syntax support.

### Image

You can use absolute path from the static resource directory in the path of image syntax.
For example:

```
![alt text](/img/some-image.png)
```

Gaiden generates automatically relative path from absolute path which is specified.


## License

Gaiden is licensed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html "Apache License, Version 2.0")
