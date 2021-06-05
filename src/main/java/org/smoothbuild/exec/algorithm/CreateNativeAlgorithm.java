package org.smoothbuild.exec.algorithm;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.define.ModuleLocation;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;
import okio.Okio;

public class CreateNativeAlgorithm extends Algorithm {

  public CreateNativeAlgorithm(Spec nativeSpec, String path,
      ModuleLocation moduleLocation) {
    super(nativeSpec);
  }

  @Override
  public Hash hash() {
    return null;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
//    createContent(nativeApi, input.objects().get());
    return null;
  }
}
