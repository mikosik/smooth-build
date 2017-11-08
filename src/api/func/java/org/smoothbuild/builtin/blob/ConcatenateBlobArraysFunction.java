package org.smoothbuild.builtin.blob;

import static org.smoothbuild.lang.type.Types.BLOB;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;

public class ConcatenateBlobArraysFunction {

  @SmoothFunction
  public static Array concatenateBlobArrays(Container container, Array blobs, Array with) {
    ArrayBuilder builder = container.create().arrayBuilder(BLOB);

    for (Blob blob : blobs.asIterable(Blob.class)) {
      builder.add(blob);
    }
    for (Blob blob : with.asIterable(Blob.class)) {
      builder.add(blob);
    }

    return builder.build();
  }
}
