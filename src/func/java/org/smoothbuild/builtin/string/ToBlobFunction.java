package org.smoothbuild.builtin.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;

public class ToBlobFunction {
  @SmoothFunction
  public static Blob toBlob( //
      Container container, //
      @Required @Name("string") SString string) {
    return stringToBlob(container, string);
  }

  public static Blob stringToBlob(Container container, SString string) {
    BlobBuilder builder = container.blobBuilder();
    try (OutputStreamWriter writer = new OutputStreamWriter(builder.openOutputStream(), CHARSET)) {
      writer.write(string.value());
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
    return builder.build();
  }
}
