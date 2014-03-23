package org.smoothbuild.lang.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.task.exec.PluginApiImpl;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  public interface Parameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "toString")
  public static SString execute(PluginApiImpl pluginApi, Parameters params) {
    String string;
    try {
      string = Streams.inputStreamToString(params.blob().openInputStream());
      return pluginApi.string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
