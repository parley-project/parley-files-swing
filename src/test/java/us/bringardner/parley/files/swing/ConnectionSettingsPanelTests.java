package us.bringardner.parley.files.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import us.bringardner.parley.files.ConnectionSetting;

/** The Swing form drawn from connection settings (headless: no window is shown). */
public class ConnectionSettingsPanelTests {

	static final List<ConnectionSetting> SETTINGS = List.of(
			ConnectionSetting.text("host", "Host").asRequired(),
			ConnectionSetting.integer("port", "Port", 1L, 65535L).withDefault("22"),
			ConnectionSetting.choice("auth", "Authentication", "password", "keyFile"),
			ConnectionSetting.secret("password", "Password").visibleWhen("auth", "password"),
			ConnectionSetting.localFile("identityFile", "Key file").visibleWhen("auth", "keyFile"),
			ConnectionSetting.integer("timeout", "Timeout", 0L, null).asAdvanced(),
			ConnectionSetting.hidden("sessionKey"));

	@BeforeAll
	public static void headless() {
		System.setProperty("java.awt.headless", "true");
	}

	private static void onEdt(Runnable r) throws Exception {
		SwingUtilities.invokeAndWait(r);
	}

	@Test
	public void aFieldOfTheRightKindForEachSetting() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			Properties values = new Properties();
			values.setProperty("host", "example.com");
			values.setProperty("password", "pw");
			values.setProperty("sessionKey", "abc");
			panel.setSettings(SETTINGS, values);
			assertTrue(panel.fieldFor("password") instanceof JPasswordField);
			assertTrue(panel.fieldFor("auth") instanceof JComboBox);
			assertEquals("22", ((JTextField) panel.fieldFor("port")).getText());
			assertEquals(null, panel.fieldFor("sessionKey"));

			Properties got = panel.getProperties();
			assertEquals("example.com", got.getProperty("host"));
			assertEquals("pw", got.getProperty("password"));
			// hidden settings are carried through
			assertEquals("abc", got.getProperty("sessionKey"));
		});
	}

	@Test
	public void choosingShowsAndHidesSettings() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			Properties values = new Properties();
			values.setProperty("password", "pw");
			panel.setSettings(SETTINGS, values);
			Component password = panel.fieldFor("password").getParent();   // the field and its "From store..." button
			Component keyFile = ((JTextField) panel.fieldFor("identityFile")).getParent();
			assertTrue(password.isVisible());
			assertFalse(keyFile.isVisible());
			// advanced settings stay out of the way
			assertFalse(panel.fieldFor("timeout").isVisible());

			@SuppressWarnings("unchecked")
			JComboBox<String> auth = (JComboBox<String>) panel.fieldFor("auth");
			auth.setSelectedItem("keyFile");
			assertFalse(password.isVisible());
			assertTrue(keyFile.isVisible());
			((JTextField) panel.fieldFor("identityFile")).setText("/home/me/.ssh/id");
			((JTextField) panel.fieldFor("host")).setText("h");

			Properties got = panel.getProperties();
			// the password no longer applies, so it isn't sent
			assertEquals("", got.getProperty("password"));
			assertEquals("/home/me/.ssh/id", got.getProperty("identityFile"));
			assertEquals(List.of(), panel.validateValues());
		});
	}

	@Test
	public void problemsAreReported() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			panel.setSettings(SETTINGS, new Properties());
			((JTextField) panel.fieldFor("port")).setText("abc");
			assertEquals(List.of("Host is required", "Port must be a whole number"), panel.validateValues());
		});
	}

	@Test
	public void propertiesNobodyDescribed() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			Properties values = new Properties();
			values.setProperty("url", "jdbc:x");
			values.setProperty("password", "pw");
			panel.setProperties(values);
			assertTrue(panel.fieldFor("password") instanceof JPasswordField);
			assertEquals("jdbc:x", panel.getProperties().getProperty("url"));
		});
	}

	/** A keychain in memory that can list what it holds. */
	static class FakeKeychain implements us.bringardner.parley.files.credentials.CredentialProvider {
		final java.util.Map<String, String> entries = new java.util.TreeMap<>();
		@Override public String scheme() { return "keychain"; }
		@Override public boolean isAvailable() { return true; }
		@Override public boolean canStore() { return true; }
		@Override public char[] resolve(us.bringardner.parley.files.credentials.CredentialRef ref,
				us.bringardner.parley.files.credentials.CredentialRequest req) {
			return entries.get(ref.locator()).toCharArray();
		}
		@Override public void store(us.bringardner.parley.files.credentials.CredentialRef ref, char[] secret) {
			entries.put(ref.locator(), new String(secret));
		}
		@Override public List<us.bringardner.parley.files.credentials.CredentialRef> list() {
			List<us.bringardner.parley.files.credentials.CredentialRef> ret = new java.util.ArrayList<>();
			for(String k : entries.keySet()) {
				ret.add(new us.bringardner.parley.files.credentials.CredentialRef("keychain", k));
			}
			return ret;
		}
	}

	private static Properties sftpValues(String password) {
		Properties v = new Properties();
		v.setProperty("host", "prod");
		v.setProperty("user", "deploy");
		v.setProperty("auth", "password");
		v.setProperty("password", password);
		return v;
	}

	private static final List<ConnectionSetting> WITH_USER = List.of(
			ConnectionSetting.text("host", "Host"), ConnectionSetting.text("user", "User"),
			ConnectionSetting.choice("auth", "Authentication", "password", "keyFile"),
			ConnectionSetting.secret("password", "Password").visibleWhen("auth", "password"));

	@Test
	public void aReferenceIsShownInClearAndAPasswordMasked() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			panel.setSettings(WITH_USER, sftpValues("@env:DB_PW"));
			JPasswordField pw = (JPasswordField) panel.fieldFor("password");
			assertEquals(0, pw.getEchoChar(), "a reference isn't a secret");
			pw.setText("hunter2");
			assertTrue(pw.getEchoChar() != 0, "a password is masked");
			assertEquals("hunter2", panel.getProperties().getProperty("password"));
		});
	}

	@Test
	public void aTypedPasswordCanBeSavedToTheKeychain() throws Exception {
		FakeKeychain keychain = new FakeKeychain();
		us.bringardner.parley.files.credentials.Credentials.register(keychain);
		try {
			onEdt(()->{
				ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
				panel.setFactoryId("sftp");
				panel.setSettings(WITH_USER, sftpValues("hunter2"));
				javax.swing.JCheckBox save = panel.saveToKeychainFor("password");
				assertTrue(save != null && save.isEnabled());
				save.setSelected(true);
				Properties p = panel.getProperties();
				assertEquals("@keychain:parley/sftp/deploy@prod", p.getProperty("password"));
				assertEquals("hunter2", keychain.entries.get("parley/sftp/deploy@prod"));
				assertFalse(save.isSelected());
				assertFalse(save.isEnabled(), "nothing to save once the field holds a reference");
				assertEquals(List.of("@keychain:parley/sftp/deploy@prod"), ConnectionSettingsPanel.storedReferences());
			});
		} finally {
			us.bringardner.parley.files.credentials.Credentials.restoreDefaults();
		}
	}

	@Test
	public void withNoKeychainThereIsNoSaveBox() throws Exception {
		us.bringardner.parley.files.credentials.Credentials.unregister("keychain");
		try {
			onEdt(()->{
				ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
				panel.setSettings(WITH_USER, sftpValues("x"));
				assertEquals(null, panel.saveToKeychainFor("password"));
			});
		} finally {
			us.bringardner.parley.files.credentials.Credentials.restoreDefaults();
		}
	}

	@Test
	public void anUnknownStoreIsReported() throws Exception {
		onEdt(()->{
			ConnectionSettingsPanel panel = new ConnectionSettingsPanel();
			panel.setSettings(WITH_USER, sftpValues("@vault:prod"));
			assertEquals(List.of("Password: there is no credential store called 'vault' (@vault:prod)"), panel.validateValues());
		});
	}
}
