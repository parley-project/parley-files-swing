# parley-files-swing

Swing user interface for [parley-files](https://github.com/tony-bringardner/parley-files). It was
part of parley-files until 1.0.0; it moved here so parley-files needs no UI toolkit. The JavaFX
counterpart is [parley-files-fx](https://github.com/tony-bringardner/parley-files-fx).

All classes are in the package `us.bringardner.parley.files.swing`:

- `FileSourceChooserDialog`: open and save dialogs for `FileSource`s on any file system, used like
  `JFileChooser`.
- `ConnectionSettingsPanel`: a form for any file system's connection settings, drawn from
  `FileSourceFactory.getConnectionSettings()`.
- `FactoryPropertiesDialog`: choose a file system type and fill in its settings.
- `RecentFileMenu`: a recent-files menu that can reopen remote files. Secrets are never saved; it
  asks for them again through a `SecretPrompter`.
- `FileSourceTransferable`: drag and drop, and copy and paste, of `FileSource`s.
- `ProgressMonitorProgress`: a Swing `ProgressMonitor` as a `FileSourceProgress`.
- `BackupDialog`, `FileSourceExamineDialog`.

The logic every UI shares (connection settings, listing and sorting, copying) is UI-free and stays
in parley-files, so the Swing and JavaFX UIs behave the same.

## Requirements

- Java 11
- parley-files and [swing-widgets](https://github.com/tony-bringardner/swing-widgets)

## Building

```bash
mvn package
```

## License

Apache License 2.0. See [LICENSE](LICENSE).
