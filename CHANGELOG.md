# Changelog

## parley-files-swing 1.1.0 (unreleased)

### Changed

- The file chooser shows folders and files with their own icons in the theme's colors (swing-widgets'
  `FolderIcon` and `FileIcon`), as the viewers do. Both were the gradient look's tree marker, so
  they looked the same. Needs swing-widgets 1.1.0.
- The dialogs (`FileSourceChooserDialog`, `FileSourceExamineDialog`, `BackupDialog`,
  `FactoryPropertiesDialog`) take on the application's gradient look when it uses swing-widgets'
  `GradientLook`, as parley-files-viewer and fsh-ide do; otherwise they look as before.
  swing-widgets 1.1.0.

## parley-files-swing 1.0.0 (2026-10-16)

First release. These classes were in parley-files (and before that BjlFileSystem); they moved here
so parley-files needs no UI toolkit.

### Added

- `FileSourceChooserDialog.MENU_ICON_KEY` ("ParleyFiles.menuIcon"): put an `Icon` under this key in
  the `UIManager` to replace the dialog's main menu icon, so an application can use its own look.
- GraalVM native-image metadata in the jar, listing the menu icon, so native builds include it.

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

- The main menu icon moved from the root of the classpath (`/HamburgerMenu_20_black.png`) into the
  package, `us/bringardner/parley/files/swing/`, so it can't clash with a file of the same name in
  another jar.
- Dragging a file out of a file system that isn't the local one copies it with parley-files'
  `FileSourceCopy`, which sizes the blocks for both ends and opens both streams with that size. It was
  `Files.copy` on a stream with no size, which reads a remote file in the JDK's 8 to 16 KB.
