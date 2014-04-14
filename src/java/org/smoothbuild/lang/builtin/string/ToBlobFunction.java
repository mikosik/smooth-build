package org.smoothbuild.lang.builtin.string;

import static org.smoothbuild.SmoothContants.CHARSET;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ToBlobFunction {
  public interface Parameters {
    @Required
    public SString string();
  }

  @SmoothFunction(name = "toBlob")
  public static SBlob execute(NativeApi nativeApi, Parameters params) {
    return stringToBlob(nativeApi, params.string());
  }

  public static SBlob stringToBlob(NativeApi nativeApi, SString string) {
    BlobBuilder builder = nativeApi.blobBuilder();
    try (OutputStreamWriter writer = new OutputStreamWriter(builder.openOutputStream(), CHARSET)) {
      writer.write(string.value());
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
    return builder.build();
  }
}
