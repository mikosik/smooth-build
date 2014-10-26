package org.smoothbuild.builtin.java;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnjarFunction {

  public interface UnjarParameters {
    @Required
    public Blob blob();
  }

  @SmoothFunction
  public static SArray<SFile> unjar(NativeApi nativeApi, UnjarParameters params) {
    return new Unjarer(nativeApi).unjar(params.blob());
  }
}
