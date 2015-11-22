package org.smoothbuild.builtin.compress;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class UnzipFunction {
  @SmoothFunction
  public static Array<SFile> unzip(
      Container container,
      @Required @Name("blob") Blob blob) {
    return new Unzipper(container).unzip(blob);
  }
}
