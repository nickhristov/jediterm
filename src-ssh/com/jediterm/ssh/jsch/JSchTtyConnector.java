/**
 *
 */
package com.jediterm.ssh.jsch;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;

public class JSchTtyConnector implements TtyConnector {
  public static final Logger LOG = Logger.getLogger(JSchTtyConnector.class);

  private InputStream myInputStream = null;
  private OutputStream myOutputStream = null;
  private Session mySession;
  private ChannelShell myChannelShell;
  
  private int myPort = 22;

  private String myUser = null;
  private String myHost = null;
  private String myPassword = null;

  private Dimension myPendingTermSize;
  private Dimension myPendingPixelSize;
  private InputStreamReader myInputStreamReader;
  private OutputStreamWriter myOutputStreamWriter;


  public JSchTtyConnector() {

  }

  public JSchTtyConnector(String host, String user, String password) {
    this.myHost = host;
    this.myUser = user;
    this.myPassword = password;
  }

  public void resize(Dimension termSize, Dimension pixelSize) {
    myPendingTermSize = termSize;
    myPendingPixelSize = pixelSize;
    if (myChannelShell != null) resizeImmediately();
  }

  private void resizeImmediately() {
    if (myPendingTermSize != null && myPendingPixelSize != null) {
      myChannelShell.setPtySize(myPendingTermSize.width, myPendingTermSize.height, myPendingPixelSize.width, myPendingPixelSize.height);
      myPendingTermSize = null;
      myPendingPixelSize = null;
    }
  }


  public void close() {
    if (mySession != null) {
      mySession.disconnect();
      mySession = null;
      myChannelShell = null;
      myInputStream = null;
      myOutputStream = null;
    }
  }

  public boolean init(Questioner q) {

    getAuthDetails(q);

    try {
      mySession = connectSession(q);
      myChannelShell = (ChannelShell)mySession.openChannel("shell");
      myInputStream = myChannelShell.getInputStream();
      myOutputStream = myChannelShell.getOutputStream();
      myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
      myChannelShell.connect();
      resizeImmediately();
      return true;
    }
    catch (final IOException e) {
      q.showMessage(e.getMessage());
      LOG.error("Error opening channel", e);
      return false;
    }
    catch (final JSchException e) {
      q.showMessage(e.getMessage());
      LOG.error("Error opening session or channel", e);
      return false;
    }
  }

  private Session connectSession(Questioner questioner) throws JSchException {
    JSch jsch = new JSch();

    Session session = jsch.getSession(myUser, myHost, myPort);

    final QuestionerUserInfo ui = new QuestionerUserInfo(questioner);
    if (myPassword != null) {
      session.setPassword(myPassword);
      ui.setPassword(myPassword);
    }
    session.setUserInfo(ui);

    final java.util.Properties config = new java.util.Properties();
    config.put("compression.s2c", "zlib,none");
    config.put("compression.c2s", "zlib,none");
    configureSession(session, config);
    session.setTimeout(5000);
    session.connect();
    session.setTimeout(0);

    return session;
  }

  protected void configureSession(Session session, final java.util.Properties config) {
    session.setConfig(config);
  }

  private void getAuthDetails(Questioner q) {
    while (true) {
      if (myHost == null) {
        myHost = q.questionVisible("host:", "localhost");
      }
      if (myHost == null || myHost.length() == 0) {
        continue;
      }
      if (myHost.indexOf(':') != -1) {
        final String portString = myHost.substring(myHost.indexOf(':') + 1);
        try {
          myPort = Integer.parseInt(portString);
        }
        catch (final NumberFormatException eee) {
          q.showMessage("Could not parse port : " + portString);
          continue;
        }
        myHost = myHost.substring(0, myHost.indexOf(':'));
      }

      if (myUser == null) {
        myUser = q.questionVisible("user:", System.getProperty("user.name").toLowerCase());
      }
      if (myHost == null || myHost.length() == 0) {
        continue;
      }
      break;
    }
  }

  public String getName() {
    return "ConnectRunnable";
  }

  @Override
  public int read(char[] buf, int offset, int length) throws IOException {
    return myInputStreamReader.read(buf, offset, length);
  }

  public int read(byte[] buf, int offset, int length) throws IOException {
    return myInputStream.read(buf, offset, length);
  }

  public void write(byte[] bytes) throws IOException {
    myOutputStream.write(bytes);
    myOutputStream.flush();
  }

  @Override
  public boolean isConnected() {
    return myChannelShell.isConnected();
  }

  @Override
  public void write(String string) throws IOException {
    write(string.getBytes("utf-8")); //TODO: fix
  }
}
