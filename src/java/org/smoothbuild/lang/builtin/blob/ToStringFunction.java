package org.smoothbuild.lang.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  public interface Parameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "toString")
  public static SString execute(NativeApiImpl nativeApi, Parameters params) {
    String string;
    try {
      string = Streams.inputStreamToString(params.blob().openInputStream());
      return nativeApi.string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
