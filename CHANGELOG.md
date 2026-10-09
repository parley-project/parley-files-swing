# Changelog

## parley-files-swing 1.0.0 (unreleased)

First release. These classes were in parley-files (and before that BjlFileSystem); they moved here
so parley-files needs no UI toolkit.

### Changed (needs a code change)

- Package: `us.bringardner.parley.files` is now `us.bringardner.parley.files.swing` for
  `FileSourceChooserDialog`, `ConnectionSettingsPanel`, `FactoryPropertiesDialog`,
  `RecentFileMenu`, `BackupDialog`, `FileSourceExamineDialog`, `FileSourceTransferable` and
  `ProgressMonitorProgress`.
- `FileSourceChooserDialog.copyInto` is now `FileCopy.copyInto` in parley-files' package
  `us.bringardner.parley.files.browse`.
- `RecentFileMenu` extends swing-widgets' generic `RecentItemsMenu`. Its public methods are
  unchanged, and it saves to the same preferences, so existing recent lists carry over. The "Max
  Files" dialog's title is now "Max Items".
