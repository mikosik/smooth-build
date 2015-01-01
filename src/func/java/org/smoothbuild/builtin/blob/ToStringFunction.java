package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  public interface ToStringParameters {
    @Required
    public Blob blob();
  }

  @SmoothFunction
  public static SString toString(NativeApi nativeApi, ToStringParameters params) {
    try {
      String string = Streams.inputStreamToString(params.blob().openInputStream());
      return nativeApi.string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
