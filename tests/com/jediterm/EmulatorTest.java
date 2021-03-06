package com.jediterm;

import com.jediterm.terminal.ArrayTerminalDataStream;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.display.BackBuffer;
import com.jediterm.terminal.display.LinesBuffer;
import com.jediterm.terminal.display.StyleState;
import com.jediterm.terminal.emulator.Emulator;
import com.jediterm.terminal.emulator.JediEmulator;
import com.jediterm.util.BackBufferTerminal;
import com.jediterm.util.FileUtil;
import com.jediterm.util.NullTerminalOutputStream;
import junit.framework.TestCase;
import testData.TestPathsManager;

import java.io.File;
import java.io.IOException;

/**
 * @author traff
 */
public class EmulatorTest extends TestCase {
  public void testSetCursorPosition() throws IOException {
    doTest(3, 4, "X00\n" +  //X wins
                 "0X \n" +
                 "X X\n" +
                 "   \n");
  }

  //public void testTooLargeScrollRegion() throws IOException { TODO: this test fails on Travis somehow
  //  doTest(80, 24);
  //}

  public void testMidnightCommanderOnVT100() throws IOException { 
    doTest(80, 24);
  }

  public void testMidnightCommanderOnXTerm() throws IOException {
    doTest(80, 24);
  }

  private void doTest(int width, int height) throws IOException {
    doTest(width, height, FileUtil.loadFileLines(new File(TestPathsManager.getTestDataPath() + getName() + ".after.txt")));
  }

  private void doTest(int width, int height, String expected) throws IOException {
    StyleState state = new StyleState();

    LinesBuffer scrollBuffer = new LinesBuffer();

    BackBuffer backBuffer = new BackBuffer(width, height, state, scrollBuffer);

    Terminal terminal = new BackBufferTerminal(backBuffer, state);

    ArrayTerminalDataStream
      fileStream = new ArrayTerminalDataStream(FileUtil.loadFileText(new File(TestPathsManager.getTestDataPath() + getName() + ".txt"),
                                                                     "UTF-8"));

    Emulator emulator = new JediEmulator(fileStream, new NullTerminalOutputStream(), terminal);

    while (emulator.hasNext()) {
      emulator.next();
    }

    assertEquals(expected, backBuffer.getLines());
  }
}
