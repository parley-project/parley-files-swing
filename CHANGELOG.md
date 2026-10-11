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
- `RecentFileMenu.ListEntry` is now parley-files' `us.bringardner.parley.files.browse.RecentFile`,
  and `RecentFileMenu.SecretPrompter` is `RecentFile.SecretPrompter` (its first argument is a
  `RecentFile`). Everything about an entry that isn't Swing moved there, so parley-files-fx's
  `RecentFileMenu` works the same way and the two menus share a list.

### Changed (no code change needed)

- Dragging a file out of a file system that isn't the local one copies it with parley-files'
  `FileSourceCopy`, which sizes the blocks for both ends and opens both streams with that size. It was
  `Files.copy` on a stream with no size, which reads a remote file in the JDK's 8 to 16 KB.
