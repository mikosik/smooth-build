package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

public class BlobArrayParameter {
  @SmoothFunction
  public static Array<Blob> blobArrayParameter(Container container, Array<Blob> array) {
    return array;
  }
}
