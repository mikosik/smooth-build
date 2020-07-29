package org.smoothbuild.plugin;

import java.io.IOException;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.db.RecordFactory;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.util.CommandExecutor;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native functions.
 */
public interface NativeApi {
  public RecordFactory factory();

  public MessageLogger log();

  public Array messages();

  /**
   * Creates temporary dir in native Operating System. Such dir is automatically deleted by smooth
   * framework after native function execution completes.
   * <p/>
   * Temporary dir is handy when your native function implementation invokes command line tool (via
   * {@link CommandExecutor}) and want to pass some files to it or from it. TempDir allows you to
   * save any smooth File in that dir and have it accessed by command line tools via
   * {@link TempDir#rootOsPath()} .
   * <p/>
   * Each call to this methods creates separate dir which is what you should do when your command
   * line tool read data from one dir and outputs it to another.
   */
  public TempDir createTempDir() throws IOException;
}
