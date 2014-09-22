Commands
========

```sh
$ gaiden <command>
```

build
-----
The `build` command builds HTML pages from Markdown files using theme. All HTML pages and static resources will generated in **build** directory.

clean
-----
This command cleans out all built resources.

create-project
--------------
```sh
$ gaiden create-project <project-name>
```
The starting point for Gaiden. This command creates a project template using the `<project name>` specified.

install-theme
-------------
```sh
$ gaiden install-theme <theme-name>
```
This command installs theme with static resources for customizing look-and-feel. You can specify **default** theme as `<theme-name>` by default.

watch
-----
This command provides continuous `build` by modification detection. `Ctrl+c` stops this rebuilding.


wrapper
-------
This command installs **gaiden wrapper** for portable use, with `gaidenw` command.

```sh
$ ./gaidenw <command>
```

