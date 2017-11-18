package org.smoothbuild.lang.plugin;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.util.CommandExecutor;

public interface NativeApi {
  public ValueFactory create();

  public void log(Message message);

  /**
   * Creates temporary dir in native Operating System. Such dir is automatically
   * deleted by smooth framework after native function execution completes.
   * <p/>
   * Temporary dir is handy when your native function implementation invokes
   * command line tool (via {@link CommandExecutor}) and want to pass some files
   * to it or from it. TempDir allows you to save any smooth Value in that dir
   * and have it accessed by command line tools via {@link TempDir#rootOsPath()}
   * .
   * <p/>
   * Each call to this methods creates separate dir which is what you should do
   * when your command line tool read data from one dir and outputs it to
   * another.
   */
  public TempDir createTempDir();
}
