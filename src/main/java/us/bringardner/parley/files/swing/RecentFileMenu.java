/**
 * <PRE>
 * 
 * Copyright Tony Bringarder 1998, 2025 <A href="http://bringardner.com/tony">Tony Bringardner</A>
 * 
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       <A href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</A>
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  </PRE>
 *   
 *   
 *	@author Tony Bringardner   
 *
 *
 * ~version~V000.01.17-V000.00.01-V000.00.00-
 */
package us.bringardner.parley.files.swing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import us.bringardner.parley.files.FileSource;
import us.bringardner.parley.files.FileSourceFactory;
import us.bringardner.parley.files.browse.RecentFile;
import us.bringardner.swing.menu.RecentItemsMenu;

/**
 * A "Recent Files" menu, saved with java.util.prefs.Preferences. The menu itself is
 * swing-widgets' {@link RecentItemsMenu}, and each entry is a parley-files {@link RecentFile},
 * which holds everything that isn't Swing; parley-files-fx's RecentFileMenu is built the same
 * way, so the two menus behave the same and read each other's lists.
 * <p>
 * Each entry keeps its factory's connection properties so it can be reopened,
 * but secret values (the factory decides which, see
 * {@link FileSourceFactory#isSecretProperty(String)}) are never saved. A secret that
 * had a value is saved as its name alone; when the entry is opened the user is asked
 * for it (see {@link #setSecretPrompter(RecentFile.SecretPrompter)}), and the answer is kept in
 * memory for the rest of the session. Secrets that were empty are left out.
 * <p>
 * Up to 1.0.1 the whole list, passwords included, was saved encrypted with a key
 * derived from the user name and a fixed IV, which anyone could reverse. That list
 * is read once, saved again without its secrets, and removed.
 */
public class RecentFileMenu extends RecentItemsMenu<RecentFile> {

	private static final long serialVersionUID = 1L;

	/** Where 1.0.1 and earlier kept the encrypted list; migrated, then removed. */
	public static final String PREF_LEGACY_RECENT_LIST = RecentFile.PREF_LEGACY_RECENT_LIST;
	/** The list: one entry per line, secret values left out. */
	public static final String PREF_RECENT_LIST = RecentItemsMenu.PREF_RECENT_LIST;
	public static final String PREF_MAX_FILES = RecentItemsMenu.PREF_MAX_ITEMS;

	private static final Codec<RecentFile> CODEC = new Codec<RecentFile>() {
		@Override
		public String encode(RecentFile entry) {
			return entry.toString();
		}

		@Override
		public RecentFile decode(String line) {
			return new RecentFile(line);
		}
	};

	private RecentFile.SecretPrompter secretPrompter = this::askForSecret;

	/**
	 * RecentFileMenu uses java.util.prefs.Preferences to store information.
	 * The java.util.prefs.Preferences uses the targetClass to identify the correct storage location.
	 *
	 * @param targetClass
	 * @throws IOException
	 */
	public RecentFileMenu(Class<?> targetClass) throws IOException {
		this(Preferences.userNodeForPackage(targetClass));
	}

	/** Saves the list in the given preferences node. */
	public RecentFileMenu(Preferences prefs) throws IOException {
		super("Recent Files", RecentFile.migrateLegacyList(prefs), CODEC);
	}

	/** Sets how secrets are asked for; null restores the default (a password dialog). */
	public void setSecretPrompter(RecentFile.SecretPrompter prompter) {
		secretPrompter = prompter == null ? this::askForSecret : prompter;
	}

	/** @return the entries, newest first, without connecting to anything */
	public List<RecentFile> getRecentEntries() {
		return getItems();
	}

	/**
	 * Opens an entry, first asking for any secret that wasn't saved.
	 * If the open fails, the secrets just entered are forgotten so they are asked for again.
	 *
	 * @return the file, or null if a prompt was cancelled
	 */
	public FileSource openEntry(RecentFile entry) throws IOException {
		return entry.open(secretPrompter);
	}

	private String askForSecret(RecentFile entry, String name) {
		JPasswordField field = new JPasswordField(20);
		int answer = JOptionPane.showConfirmDialog(this, new Object[] {getLabel(entry), name, field},
				"Enter "+name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if( answer != JOptionPane.OK_OPTION ) {
			return null;
		}
		char[] value = field.getPassword();
		try {
			return new String(value);
		} finally {
			Arrays.fill(value, '\0');
		}
	}

	/**
	 * Opens every entry. Remote entries connect, and may ask for secrets;
	 * entries whose prompt is cancelled are left out.
	 */
	public List<FileSource> getRecentFiles() throws IOException {
		List<FileSource> ret = new ArrayList<>();
		for(RecentFile e : new ArrayList<>(getItems())) {
			FileSource file = openEntry(e);
			if( file != null ) {
				ret.add(file);
			}
		}
		return ret;
	}

	public void setRecentFiles(List<FileSource> list) throws IOException {
		List<RecentFile> ret = new ArrayList<>();
		for(FileSource file : list) {
			ret.add(new RecentFile(file));
		}
		setItems(ret);
	}

	public void setMaxFiles(int maxRecent) throws IOException {
		setMaxItems(maxRecent);
	}

	public int getMaxFiles() {
		return getMaxItems();
	}

	public void addRecent(FileSource file) throws IOException {
		addItem(new RecentFile(file));
	}

	@Override
	protected String getLabel(RecentFile entry) {
		return entry.getLabel();
	}

	/** Local files that are gone are dropped; remote entries aren't checked (see {@link RecentFile#isGone()}). */
	@Override
	protected boolean isStale(RecentFile entry) {
		return entry.isGone();
	}

	/** Keeps the secrets entered this session for the entry being replaced. */
	@Override
	protected RecentFile merge(RecentFile newer, RecentFile older) {
		return newer.keepSecretsFrom(older);
	}

	/** Asks for missing secrets and connects; the event's source is the FileSource. */
	@Override
	protected Object openItem(RecentFile entry) throws IOException {
		return openEntry(entry);
	}

	@Override
	protected String getMaxItemsText(int max) {
		return "Max Files:"+max;
	}
}
