package us.bringardner.parley.files.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** An application can replace the chooser's menu icon through the UIManager, so the library never forces its look. */
public class MenuIconTests {

	@AfterEach
	public void clearOverride() {
		UIManager.put(FileSourceChooserDialog.MENU_ICON_KEY, null);
	}

	@Test
	public void defaultIconIsInThePackage() {
		assertNotNull(FileSourceChooserDialog.class.getResource(FileSourceChooserDialog.DEFAULT_MENU_ICON),
				"the default icon must be in us/bringardner/parley/files/swing, not at the root of the classpath");
		Icon icon = FileSourceChooserDialog.menuIcon();
		assertEquals(20, icon.getIconWidth());
	}

	@Test
	public void uiManagerOverrideWins() {
		Icon mine = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
		UIManager.put(FileSourceChooserDialog.MENU_ICON_KEY, mine);
		assertSame(mine, FileSourceChooserDialog.menuIcon());
	}
}
