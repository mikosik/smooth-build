package org.smoothbuild.builtin.java;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class UnjarFunction {
  @SmoothFunction
  public static Array<SFile> unjar( //
      NativeApi nativeApi, //
      @Required @Name("blob") Blob blob) {
    return new Unjarer(nativeApi).unjar(blob);
  }
}
