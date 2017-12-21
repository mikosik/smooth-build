package org.smoothbuild.builtin.blob;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;

public class ConcatenateBlobArraysFunction {

  @SmoothFunction
  public static Array concatenateBlobArrays(NativeApi nativeApi, Array blobs, Array with) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(nativeApi.types().blob());

    for (Blob blob : blobs.asIterable(Blob.class)) {
      builder.add(blob);
    }
    for (Blob blob : with.asIterable(Blob.class)) {
      builder.add(blob);
    }

    return builder.build();
  }
}
