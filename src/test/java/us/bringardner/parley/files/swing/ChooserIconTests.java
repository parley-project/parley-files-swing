package us.bringardner.parley.files.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import javax.swing.Icon;

import org.junit.jupiter.api.Test;

import us.bringardner.swing.gradient.FileIcon;
import us.bringardner.swing.gradient.FolderIcon;
import us.bringardner.swing.gradient.Theme;

/** The file chooser shows folders and files differently, in the theme's colors. */
public class ChooserIconTests {

	@Test
	public void foldersAndFilesHaveTheirOwnThemedIcons() {
		assertTrue(FileSourceChooserDialog.FOLDER_ICON instanceof FolderIcon);
		assertEquals(FolderIcon.Kind.CLOSED, ((FolderIcon) FileSourceChooserDialog.FOLDER_ICON).getKind());
		assertTrue(FileSourceChooserDialog.FILE_ICON instanceof FileIcon);
		assertNotEquals(pixels(FileSourceChooserDialog.FOLDER_ICON), pixels(FileSourceChooserDialog.FILE_ICON),
				"they look different");
	}

	@Test
	public void theyFollowTheTheme() {
		Theme old = Theme.current();
		try {
			Theme.setCurrent(Theme.named("Ocean"));
			String ocean = pixels(FileSourceChooserDialog.FILE_ICON);
			Theme.setCurrent(Theme.named("Plum"));
			assertNotEquals(ocean, pixels(FileSourceChooserDialog.FILE_ICON));
		} finally {
			Theme.setCurrent(old);
		}
	}

	private static String pixels(Icon icon) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = img.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		StringBuilder b = new StringBuilder();
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				b.append(Integer.toHexString(img.getRGB(x, y))).append(',');
			}
		}
		return b.toString();
	}
}
