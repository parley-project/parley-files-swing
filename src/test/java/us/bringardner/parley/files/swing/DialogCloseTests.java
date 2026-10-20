package us.bringardner.parley.files.swing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import us.bringardner.parley.files.FileSource;
import us.bringardner.parley.files.memory.MemoryFileSourceFactory;

/** Only OK (or Save) accepts these dialogs: closing them any other way cancels. */
public class DialogCloseTests {

	/** Close the first showing window of a type as its close button does. */
	private static void closeWhenShowing(Class<? extends Window> type) {
		AtomicInteger ticks = new AtomicInteger();
		Timer timer = new Timer(100, null);
		timer.addActionListener(e -> {
			for (Window w : Window.getWindows()) {
				if( type.isInstance(w) && w.isShowing() ) {
					timer.stop();
					w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
					return;
				}
			}
			if( ticks.incrementAndGet() > 100 ) {
				timer.stop();
			}
		});
		timer.start();
	}

	@Test
	void closingTheFactoryDialogCancels() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display");
		boolean[] cancel = new boolean[1];
		SwingUtilities.invokeAndWait(() -> {
			FactoryPropertiesDialog d = new FactoryPropertiesDialog();
			closeWhenShowing(FactoryPropertiesDialog.class);
			d.showDialog(new MemoryFileSourceFactory());
			cancel[0] = d.isCancel();
		});
		assertTrue(cancel[0], "closing is cancelling");
	}

	@Test
	void closingTheExamineDialogCancels() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display");
		MemoryFileSourceFactory f = new MemoryFileSourceFactory();
		FileSource file = f.createFileSource("/examine.txt");
		try (java.io.OutputStream out = file.getOutputStream()) {
			out.write("x".getBytes());
		}
		boolean[] cancel = new boolean[1];
		SwingUtilities.invokeAndWait(() -> {
			try {
				FileSourceExamineDialog d = new FileSourceExamineDialog();
				closeWhenShowing(FileSourceExamineDialog.class);
				d.showDialog(file);
				cancel[0] = d.isCanceled();
			} catch (java.io.IOException e) {
				throw new java.io.UncheckedIOException(e);
			}
		});
		assertTrue(cancel[0], "closing is cancelling");
	}
}
