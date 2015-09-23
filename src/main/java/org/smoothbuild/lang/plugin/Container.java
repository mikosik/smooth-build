package org.smoothbuild.lang.plugin;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.CommandExecutor;

public interface Container extends ValueFactory {
  @Override
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> arrayType);

  @Override
  public SFile file(Path path, Blob content);

  @Override
  public BlobBuilder blobBuilder();

  @Override
  public SString string(String string);

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
