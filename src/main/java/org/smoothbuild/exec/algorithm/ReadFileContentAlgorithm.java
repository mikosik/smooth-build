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
import org.smoothbuild.exec.java.JavaCodeLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;
import okio.Okio;

public class ReadFileContentAlgorithm extends Algorithm {
  private final FilePath nativeFile;
  private final JavaCodeLoader javaCodeLoader;
  private final FullPathResolver fullPathResolver;

  public ReadFileContentAlgorithm(Spec spec, FilePath nativeFile,
      JavaCodeLoader javaCodeLoader, FullPathResolver fullPathResolver) {
    super(spec, false);
    this.nativeFile = nativeFile;
    this.javaCodeLoader = javaCodeLoader;
    this.fullPathResolver = fullPathResolver;
  }

  @Override
  public Hash hash() {
    return readFileContentAlgorithmHash(nativeFile);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws IOException {
    Path resolvedJarPath = fullPathResolver.resolve(nativeFile);
    Blob content = createContent(nativeApi, resolvedJarPath);
    if (content == null) {
      return new Output(null, nativeApi.messages());
    }
    javaCodeLoader.storeJarFile(new JarFile(nativeFile, resolvedJarPath, content.hash()));
    return new Output(content, nativeApi.messages());
  }

  private Blob createContent(NativeApi nativeApi, Path jdkPath) {
    try (BufferedSource source = Okio.buffer(Okio.source(jdkPath))) {
      return nativeApi.factory().blob(sink -> sink.writeAll(source));
    } catch (FileNotFoundException e) {
      nativeApi.log().error("Cannot find file '" + nativeFile.prefixedPath() + "'.");
      return null;
    } catch (IOException e) {
      nativeApi.log().error("Error reading file '" + nativeFile.prefixedPath() + "'.");
      return null;
    }
  }
}
