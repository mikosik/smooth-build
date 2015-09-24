package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  @SmoothFunction
  public static SString toString(Container container, @Required @Name("blob") Blob blob) {
    try {
      String string = Streams.inputStreamToString(blob.openInputStream());
      return container.create().string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
