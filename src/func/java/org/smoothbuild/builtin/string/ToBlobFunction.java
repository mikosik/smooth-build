package org.smoothbuild.builtin.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ToBlobFunction {
  public interface ToBlobParameters {
    @Required
    public SString string();
  }

  @SmoothFunction
  public static Blob toBlob(NativeApi nativeApi, ToBlobParameters params) {
    return stringToBlob(nativeApi, params.string());
  }

  public static Blob stringToBlob(NativeApi nativeApi, SString string) {
    BlobBuilder builder = nativeApi.blobBuilder();
    try (OutputStreamWriter writer = new OutputStreamWriter(builder.openOutputStream(), CHARSET)) {
      writer.write(string.value());
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
    return builder.build();
  }
}
