# parley-files-swing

Swing user interface for [parley-files](https://github.com/parley-project/parley-files). It was
part of parley-files until 1.0.0; it moved here so parley-files needs no UI toolkit.

A JavaFX counterpart, parley-files-fx, was built beside it until November 2026. It is retired; the
tag `javafx-final` in its repository is its last version.

## Why this library exists

[parley-files](https://github.com/parley-project/parley-files) reads and writes files on any file
system: local, SFTP, FTP, a database. Servers, command-line tools and shells use it with no screen
at all, so it must not need a UI toolkit. But a desktop application also needs to show those file
systems: open and save dialogs that reach remote servers (`JFileChooser` only knows local files), a
form to connect, and a recent files menu that can reopen a remote file. This library is that UI, for
Swing.

The logic every UI needs (which connection settings apply, listing and sorting, copying, what a
recent files entry saves) stays in parley-files, without a UI. This library only shows it.

## What's inside

All classes are in the package `us.bringardner.parley.files.swing`:

- `FileSourceChooserDialog`: open and save dialogs for `FileSource`s on any file system, used like
  `JFileChooser`.
- `ConnectionSettingsPanel`: a form for any file system's connection settings, drawn from
  `FileSourceFactory.getConnectionSettings()`.
- `FactoryPropertiesDialog`: choose a file system type and fill in its settings.
- `RecentFileMenu`: a recent-files menu that can reopen remote files. Secrets are never saved; it
  asks for them again through a `SecretPrompter`. Its entries are parley-files' `RecentFile`s.
- `FileSourceTransferable`: drag and drop, and copy and paste, of `FileSource`s.
- `ProgressMonitorProgress`: a Swing `ProgressMonitor` as a `FileSourceProgress`.
- `BackupDialog`, `FileSourceExamineDialog`.

## Changing the look

The library never forces Parley's look on your application. File and folder icons come from the
current look and feel, and the one image of its own, the main menu ("hamburger") icon in
`FileSourceChooserDialog`, is looked up in the `UIManager` first:

```java
UIManager.put(FileSourceChooserDialog.MENU_ICON_KEY, myIcon);   // "ParleyFiles.menuIcon"
```

Set it before the dialog is created. Without it, the dialog uses its built-in icon.

## Requirements

- Java 11
- parley-files and [swing-widgets](https://github.com/parley-project/swing-widgets)

## Building

```bash
mvn package
```

## License

Apache License 2.0. See [LICENSE](LICENSE).
