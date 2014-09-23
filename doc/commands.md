Commands
========

```sh
$ gaiden <command>
```

build
-----
This command builds HTML pages from Markdown files using a theme. All HTML pages and static resources will be generated in **build** directory.

clean
-----
This command cleans out all built resources.

create-project
--------------
```sh
$ gaiden create-project <project-name>
```
The starting point for Gaiden. This command creates a project template using the `<project name>` specified.

help
-------------
```sh
$ gaiden help [<command-name>]
```
Show help for the `<command-name>` or command list.

install-theme
-------------
```sh
$ gaiden install-theme <theme-name>
```
This command installs layouts and static resources of the specified theme for customizing look-and-feel. You can specify **default** theme as `<theme-name>` by default.

watch
-----
This command will run the `build` command continuously when project files are modified. `Ctrl+c` stops this rebuilding.

wrapper
-------
This command installs **gaiden wrapper** for portable use, with `gaidenw` command.

```sh
$ ./gaidenw <command>
```

