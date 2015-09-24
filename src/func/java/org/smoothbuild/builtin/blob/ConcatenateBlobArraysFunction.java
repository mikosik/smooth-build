package org.smoothbuild.builtin.blob;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;

public class ConcatenateBlobArraysFunction {

  @SmoothFunction
  public static Array<Blob> concatenateBlobArrays( //
      Container container, //
      @Required @Name("blobs") Array<Blob> blobs, //
      @Required @Name("with") Array<Blob> with) {
    ArrayBuilder<Blob> builder = container.create().arrayBuilder(Blob.class);

    for (Blob blob : blobs) {
      builder.add(blob);
    }
    for (Blob blob : with) {
      builder.add(blob);
    }

    return builder.build();
  }
}
