Configuration
=============

Gaiden has two configuration files: `config.groovy` and `pages.groovy`.

`config.groovy` provides settings for generating the document.

`pages.groovy` provides files list and settings for table of contents.

You can customize behavior by changing these settings.

Settings in config.groovy
-------------------------

title
: Base title of page.
: **Note:** [check encoding](#encoding-for-configgroovy) for the title using multi-bytes.

theme
: The theme for build.
: **default:** `default`

inputEncoding
: The encoding of input Markdown resource.
: **default:** `UTF-8`

outputEncoding
: The encoding of output document.
: **default:** `UTF-8`

Encoding for config.groovy
-----
The encoding of `GaidenConfig.groovy` must be same as the `file.encoding` system property.
If you want to use another encoding, you can use `JAVA_OPTS` or `GAIDEN_OPTS` to pass JVM options to Gaiden as follows.

For Unix:

```sh
$ export GAIDEN_OPTS="-Dfile.encoding=Shift_JIS"
```

For Windows:

```ps
> set GAIDEN_OPTS="-Dfile.encoding=UTF-8"
```

Settings in pages.groovy
------------------------

List the pages of document for table of contents with intended order.

For example:

```groovy
"getting-started.md"
"project-structure.md"
"configuration.md"
"commands.md"
"theme.md"
"extensions.md"
```
