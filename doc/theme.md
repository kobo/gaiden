Theme
=====

Gaiden supports changing the look and feel of a generated document via themes.
A theme is a collection of HTML layouts, stylesheets, images, javascripts, and other static files.


Specifying a theme
------------------

To configure a theme, you should specify the theme name in your `config.groovy`:

```
theme = 'theme name'
```

When the theme name is not specified, "default" of a default theme name is used.


Installing a theme
------------------

You can install a theme using an 'install-theme' command at the top of an existing Gaiden project:

```
gaiden install-theme <theme name>
```

The `install-theme` command will install resources of the given theme name to a `themes` directory.
You can customize these resources to change the look and feel of a generated document.


Directory structure of themes
-----------------------------

The `themes` directory structure looks like this:

```
<your project>
├── themes
│   ├── <theme name>
│   │   ├── layouts
│   │   │   └── ...
│   │   └── assets
│   │       └── ...
│   ...
```

The themes which are installed in the your `themes` directory are called 'User Theme'.

The names of immediate subdirectories of the `themes` directory are used as a theme name.
For example, the theme name of a directory called `themes/simple` is a `simple`.

A theme directory contains two directory which be called 'layouts' and 'assets'.

layouts
:   Layout files.
    The file name of the layout file is used as a layout name.
    The layout name can be specified in `config.groovy` or `pages.groovy`.

assets
:   Stylesheets, Images, JavaScripts, and other static files.
    These files will be copied to the output directory.


If the user theme is a same name as a bundled theme by default, the user theme takes priority.
It means that assets of a bundled theme are overwritten by assets of a user theme.
And a layout file of a user theme is used if there is a layout file with a same name.
In other words, you can remove resources which are not changed in installed resources.


Using layouts
-------------

You can specify a layout which you want to use.
There are two ways to specify the theme.


### config.groovy

In `config.groovy`, you can specify a layout name as follows:

```
layout = 'layout name'
```

The default value is `main`.
This setting specifies a layout which is used by default.


### pages.groovy

In `pages.groovy`, you can specify a layout name for each page as follows:

```
'introduction.md'(layout: 'layout name')
'install.md'(layout: 'other layout name')
'examples.md'
```

This settings which is specified in `pages.groovy` get preference over the setting on `config.groovy`.


Customizing layouts
-------------------

You can change the appearance of a generated document by customizing layouts.
Here is a simple example of a layout:

```
<html>
  <head>
    <title>$title</title>
  </head>
  <body>
    $content
    ${include('footer')}
  </body>
</html>
```

The Layout supports JSP-like scriptlets, script, and EL expressions.
Internally, the [SimpleTemplateEngine](http://groovy.codehaus.org/api/groovy/text/SimpleTemplateEngine.html) which is provided by Groovy is used for rendering a document.


### Properties and Methods

You can use the following properties and methods in your layout.

#### title

The title which is configured in `config.groovy`.

#### content

The content which is a generated html from a Markdown file.

#### page

The `gaiden.Page` instance of the target page.

#### metadata

The metadata which is configured in `config.groovy` for the target page.

#### prevPage

The previous page of the target page.

#### nextPage

The next page of the target page.

#### homePage

The home page which is configured in `config.groovy`. If it is not configured, the first page in `pages.groovy` is used.

#### document

The document is a instance of `gaiden.Document`.

#### config

The config is a instance of `gaiden.GaidenConfig`.

#### resource

The resource method is used to create a link to the static resources, e.g. image, css and javascript.

```
<script src="${resource('js/application.js')}"></script>
<link rel="stylesheet" href="${resource('js/application.js')}">
```

The path of the argument is processed as a relative path from the assets directory.

#### include

The include method is used to include another layout as follows:

```
${include('footer')}
```

The layout name of the argument is resolved from the layouts directory.
Note that the layout name does not contain an extension such as 'html'.

#### extensionScripts

The extensionScripts is a list of JavaScript file of all that is included in installed extensions.
Entries which are included in the list are an instance of `java.nio.file.Path`.
This can be used as follows:

```
<% extensionScripts.each { script -> %>
<script src="${script}"></script>
<% } %>
```

#### extensionStyles

The extensionScripts is a list of CSS file of all that is included in installed extensions.
Entries which are included in the list are an instance of `java.nio.file.Path`.
This can be used as follows:

```
<% extensionStyles.each { style -> %>
<link rel="stylesheet" href="${style}">
<% } %>
```
