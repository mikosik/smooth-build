package org.smoothbuild.builtin.blob;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;

public class ConcatenateBlobsFunction {

  public static SArray<SBlob> execute(NativeApi nativeApi,
      BuiltinSmoothModule.ConcatenateBlobsParameters params) {
    ArrayBuilder<SBlob> builder = nativeApi.arrayBuilder(BLOB_ARRAY);

    for (SBlob blob : params.blobs()) {
      builder.add(blob);
    }
    for (SBlob blob : params.with()) {
      builder.add(blob);
    }

    return builder.build();
  }
}
