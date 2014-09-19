Project Structure
=================


* `build/` - Built documents and static resources will be placed under this directory.
* `pages/` - The place of source Markdown files. The directory layout will be kept when it is placed in the `build` directory.
    * `toc.groovy` - The table of contents written by DSL. See [ToC](#toc).
* `static/` - The place of static resources. All of the files and subdirectories will be copied under the `build` directory.
* `templates/` - The place of template files. See [Template](#template).
* `GaidenConfig.groovy` - The configuration file. See [Configuration](#configuration).
