package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;

public class ConcatenateBlobsFunction {

  public static SArray<SBlob> execute(NativeApi nativeApi,
      BuiltinSmoothModule.ConcatenateBlobsParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  public static class Worker {
    private final NativeApi nativeApi;
    private final BuiltinSmoothModule.ConcatenateBlobsParameters params;

    public Worker(NativeApi nativeApi, BuiltinSmoothModule.ConcatenateBlobsParameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public SArray<SBlob> execute() {
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
}
