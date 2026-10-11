package us.bringardner.parley.files.swing;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import us.bringardner.parley.files.FileSource;
import us.bringardner.parley.files.memory.MemoryFileSourceFactory;

/** Dragging a file out of a file system that isn't the local one makes a local copy of it. */
public class FileSourceTransferableTests {

	@Test
	public void aFileFromAnotherFileSystemIsExportedWithAllItsBytes() throws Exception {
		MemoryFileSourceFactory f = new MemoryFileSourceFactory();
		byte[] data = new byte[300_007];
		new Random(9).nextBytes(data);
		FileSource source = f.createFileSource("/export-me.bin");
		try (OutputStream out = source.getOutputStream()) {
			out.write(data);
		}
		FileSource empty = f.createFileSource("/empty.bin");
		empty.createNewFile();

		FileSourceTransferable t = new FileSourceTransferable(Arrays.asList(source, empty));
		assertTrue(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
		@SuppressWarnings("unchecked")
		List<File> exported = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

		assertEquals(2, exported.size());
		assertEquals("export-me.bin", exported.get(0).getName(), "it keeps its name");
		assertArrayEquals(data, Files.readAllBytes(exported.get(0).toPath()));
		assertEquals("empty.bin", exported.get(1).getName());
		assertEquals(0, exported.get(1).length());
		// each export is in a directory of its own, so two files of one name don't collide
		FileSource other = f.createFileSource("/sub-export.bin");
		try (OutputStream out = other.getOutputStream()) {
			out.write(1);
		}
		@SuppressWarnings("unchecked")
		List<File> again = (List<File>) new FileSourceTransferable(Arrays.asList(source)).getTransferData(DataFlavor.javaFileListFlavor);
		assertTrue(!again.get(0).equals(exported.get(0)), "a second export is a different file");
		assertArrayEquals(data, Files.readAllBytes(again.get(0).toPath()));
	}
}
