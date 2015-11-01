package org.smoothbuild.lang.plugin;

import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.util.CommandExecutor;

public interface Container {
  public ValueFactory create();

  public void log(Message message);

  /**
   * Creates temporary directory in native Operating System. Such directory is
   * automatically deleted by smooth framework after native function execution
   * completes.
   * <p/>
   * Temporary directory is handy when your native function implementation
   * invokes command line tool (via {@link CommandExecutor}) and want to pass
   * some files to it or from it. TempDirectory allows you to save any smooth
   * Value in that directory and have it accessed by command line tools via
   * {@link TempDirectory#rootOsPath()}.
   * <p/>
   * Each call to this methods creates separate directory which is what you
   * should do when your command line tool read data from one directory and
   * outputs it to another.
   */
  public TempDirectory createTempDirectory();
}
