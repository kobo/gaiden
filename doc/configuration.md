Configuration
=============

Gaiden has two configuration files: `config.groovy` and `pages.groovy`.

`config.groovy` provides settings for generating the document.

`pages.groovy` provides files list and settings for table of contents. See [Using layouts](theme.html#using-layouts).

You can customize behavior by changing these settings.

Settings
--------

title
: Base title of pages.
: **Note:** [check encoding](#encoding) for the title using multi-bytes.

theme
: This setting specifies a theme for build.
: **default:** `default` (means ./themes/default/ )

layout
: This setting specifies a layout file which is used by default.
: **default:** `main` (means main.html in theme directory)

inputEncoding
: The encoding of input Markdown resource.
: **default:** `UTF-8`

outputEncoding
: The encoding of output document.
: **default:** `UTF-8`

Encoding
--------
The encoding of `config.groovy` must be same as the `file.encoding` system property.
If you want to use another encoding, you can use `JAVA_OPTS` or `GAIDEN_OPTS` to pass JVM options to Gaiden as follows.

For Unix:

```sh
$ export GAIDEN_OPTS="-Dfile.encoding=Shift_JIS"
```

For Windows:

```ps
> set GAIDEN_OPTS="-Dfile.encoding=UTF-8"
```
