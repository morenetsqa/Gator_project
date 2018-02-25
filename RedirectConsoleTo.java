package gator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JTextArea;


public class RedirectConsoleTo{

	public void redirectConsoleTo(JTextArea textarea) {
        PrintStream out = new PrintStream(new ByteArrayOutputStream() {
            public synchronized void flush() throws IOException {
                textarea.setText(toString());
            }
        }, true);

        System.setErr(out);
        System.setOut(out);
    }
    
}