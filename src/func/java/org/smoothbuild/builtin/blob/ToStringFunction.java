package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  public interface ToStringParameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "toString")
  public static SString execute(NativeApi nativeApi, ToStringParameters params) {
    try {
      String string = Streams.inputStreamToString(params.blob().openInputStream());
      return nativeApi.string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
