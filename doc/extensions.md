Extensions
==========

Gaiden supports an extension mechanism.
This allows you to add extra static resources, behavior on building, and others to your gaiden project.


Using Extensions
----------------

To use an extension, you need to install the extension in `extensions` directory.
The `extensions` directory structure looks like this:

```
<YOUR GAIDEN PROJECT>
├── extensions
│   └── <EXTENSION NAME>
│       ├── assets
│       │   └── ...
│       └── config.groovy
...
```

The names of immediate subdirectories of the `extensions` directory are used as a extension name.


### Static resources of extensions

The static resources in the assets directory of the extensions are copied to the output directory at built time.
The path of copied resources will be `<OUTPUT DIRECTORY>/extensions/<EXTENSION NAME>`.

To refer to the resources in your layout, you can use [`extensionScripts`](theme.md#extensionscripts) or [`extensionStyles`](theme.md#extensionstyles):

```
<% extensionScripts.each { script -> %>
<script src="${script}"></script>
<% } %>
<% extensionStyles.each { style -> %>
<link rel="stylesheet" href="${style}">
<% } %>
```

If you want to refer individually to the resource, you can specify the direct path using the [`resource`](theme.md#resource).

```
"${resource('extensions/<EXTENSION NAME>/<RESOURCE FILE NAME>')}"
```


### Configuration of extensions

To refer to the configuration of the extensions in your layout, you can use [`config`](theme.md#config):

```
${config.extensions.'<EXTENSION NAME>'.<KEY NAME>}
```


### Filters of extensions

Gaiden supports a filter in `config.groovy`.
The filter which defined in config.groovy is automatically enabled automatically.

Then, the filter is registered with the name `<EXTENSION NAME>.<FILTER NAME>`.
If you want to override a behavior of the filter, register a filter with that name.

