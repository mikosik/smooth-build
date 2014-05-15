package org.smoothbuild.builtin.java;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnjarFunction {

  public interface UnjarParameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "unjar")
  public static SArray<SFile> execute(NativeApi nativeApi, UnjarParameters params) {
    return new Unjarer(nativeApi).unjar(params.blob());
  }
}
