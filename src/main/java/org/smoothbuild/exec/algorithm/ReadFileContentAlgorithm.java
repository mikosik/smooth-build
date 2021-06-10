package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readFileContentAlgorithmHash;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.nativ.NativeLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.base.define.ModuleLocation;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;
import okio.Okio;

public class ReadFileContentAlgorithm extends Algorithm {
  private final ModuleLocation moduleLocation;
  private final NativeLoader nativeLoader;
  private final FullPathResolver pathResolver;

  public ReadFileContentAlgorithm(Spec spec, ModuleLocation moduleLocation,
      NativeLoader nativeLoader, FullPathResolver pathResolver) {
    super(spec, false);
    this.moduleLocation = moduleLocation;
    this.nativeLoader = nativeLoader;
    this.pathResolver = pathResolver;
  }

  @Override
  public Hash hash() {
    return readFileContentAlgorithmHash(moduleLocation);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws IOException {
    Path resolvedJarPath = pathResolver.resolve(moduleLocation.toNative());
    Blob content = createContent(nativeApi, resolvedJarPath);
    if (content == null) {
      return new Output(null, nativeApi.messages());
    }
    nativeLoader.storeJarFile(new JarFile(moduleLocation, resolvedJarPath, content.hash()));
    return new Output(content, nativeApi.messages());
  }

  private Blob createContent(NativeApi nativeApi, Path jdkPath) {
    try (BufferedSource source = Okio.buffer(Okio.source(jdkPath))) {
      return nativeApi.factory().blob(sink -> sink.writeAll(source));
    } catch (FileNotFoundException e) {
      nativeApi.log().error("Cannot find file '" + moduleLocation.toNative().prefixedPath() + "'.");
      return null;
    } catch (IOException e) {
      nativeApi.log().error("Error reading file '" + moduleLocation.toNative().prefixedPath() + "'.");
      return null;
    }
  }
}
