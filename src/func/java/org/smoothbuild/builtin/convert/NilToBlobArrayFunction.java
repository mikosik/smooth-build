package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;

public class NilToBlobArrayFunction {
  @SmoothFunction
  public static Array<Blob> nilToBlobArray( //
      NativeApi nativeApi, //
      @Required @Name("input") Array<Nothing> input) {
    return nativeApi.arrayBuilder(Blob.class).build();
  }
}
