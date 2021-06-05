package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readFileContentAlgorithmHash;

import java.io.IOException;
import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;
import okio.Okio;

public class ReadFileContentAlogirthm extends Algorithm {
  private final Path jdkPath;

  public ReadFileContentAlogirthm(Spec spec, Path jdkPath) {
    super(spec, false);
    this.jdkPath = jdkPath;
  }

  @Override
  public Hash hash() {
    return readFileContentAlgorithmHash(jdkPath.toAbsolutePath().toString());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws IOException {
    return new Output(createContent(nativeApi, jdkPath), nativeApi.messages());
  }

  private static Blob createContent(NativeApi nativeApi, Path jdkPath) throws IOException {
    try (BufferedSource source = Okio.buffer(Okio.source(jdkPath))) {
      return nativeApi.factory().blob(sink -> sink.writeAll(source));
    }
  }
}
