# parley-files-swing

Swing user interface for parley-files (Parley, formerly the Bringardner Java Library). Owner: Tony
Bringardner. **Java 11**, Maven, JUnit 5. Related repos live next to this one in
`/Volumes/Data/eclipse-git/`: parley-parent, parley-core, parley-io, parley-files, parley-files-fx,
swing-widgets.

## How Tony wants work done

- **One git branch per batch of work**, created from `master`. Never commit to `master` directly.
- Tony runs `mvn package` himself, then publishes and merges from GitHub Desktop. Don't push.
- `mvn package` must pass with every test running before a batch is called done.
- Commit messages say what changed and why, in plain words.

## How the code is laid out

- Package `us.bringardner.parley.files.swing`.
- The logic every UI shares (connection settings, browsing, copying) is in parley-files. Keep it
  there, not here, so the Swing and JavaFX UIs behave the same.
- `ConnectionSettingsPanel` mirrors parley-files-fx's `ConnectionSettingsForm`; a change to one
  usually belongs in the other too.
