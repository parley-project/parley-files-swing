# parley-files-swing

Swing user interface for [parley-files](https://github.com/tony-bringardner/parley-files). It was
part of parley-files until 1.0.0; it moved here so parley-files needs no UI toolkit. The JavaFX
counterpart is [parley-files-fx](https://github.com/tony-bringardner/parley-files-fx).

## Why this library exists

[parley-files](https://github.com/tony-bringardner/parley-files) reads and writes files on any file
system: local, SFTP, FTP, a database. Servers, command-line tools and shells use it with no screen
at all, so it must not need a UI toolkit. But a desktop application also needs to show those file
systems: open and save dialogs that reach remote servers (`JFileChooser` only knows local files), a
form to connect, and a recent files menu that can reopen a remote file. This library is that UI, for
Swing.

The logic every UI needs (which connection settings apply, listing and sorting, copying, what a
recent files entry saves) stays in parley-files, without a UI. This library and
[parley-files-fx](https://github.com/tony-bringardner/parley-files-fx) only show it, so the Swing
and JavaFX UIs behave the same and share one recent files list.

**Use parley-files-swing** when your application's UI is Swing. That includes when it has to run on
Java 11, or on platforms where JavaFX isn't available. For a JavaFX application, use parley-files-fx.

## What's inside

All classes are in the package `us.bringardner.parley.files.swing`:

- `FileSourceChooserDialog`: open and save dialogs for `FileSource`s on any file system, used like
  `JFileChooser`.
- `ConnectionSettingsPanel`: a form for any file system's connection settings, drawn from
  `FileSourceFactory.getConnectionSettings()`.
- `FactoryPropertiesDialog`: choose a file system type and fill in its settings.
- `RecentFileMenu`: a recent-files menu that can reopen remote files. Secrets are never saved; it
  asks for them again through a `SecretPrompter`. Its entries are parley-files' `RecentFile`s, as
  in parley-files-fx's `RecentFileMenu`, so the two menus share a list.
- `FileSourceTransferable`: drag and drop, and copy and paste, of `FileSource`s.
- `ProgressMonitorProgress`: a Swing `ProgressMonitor` as a `FileSourceProgress`.
- `BackupDialog`, `FileSourceExamineDialog`.

## Requirements

- Java 11
- parley-files and [swing-widgets](https://github.com/tony-bringardner/swing-widgets)

## Building

```bash
mvn package
```

## License

Apache License 2.0. See [LICENSE](LICENSE).
