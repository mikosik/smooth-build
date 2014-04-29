package org.smoothbuild.lang.builtin.java;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.builtin.string.ToBlobFunction.stringToBlob;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;

import com.tonicsystems.jarjar.Main;

public class JarjarFunction {
  public static SBlob execute(NativeApi nativeApi, BuiltinSmoothModule.JarjarParameters params) {
    TempDirectory tempDir = nativeApi.createTempDirectory();

    Path rulesPath = path("rules");
    Path inJarPath = path("in.jar");
    Path outJarPath = path("out.jar");

    tempDir.writeFile(rulesPath, stringToBlob(nativeApi, params.rules()));
    tempDir.writeFile(inJarPath, params.in());

    File rulesFile = new File(tempDir.asOsPath(rulesPath));
    File inJarFile = new File(tempDir.asOsPath(inJarPath));
    File outJarFile = new File(tempDir.asOsPath(outJarPath));

    try {
      Main main = new Main();
      main.process(rulesFile, inJarFile, outJarFile);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }

    return tempDir.readContent(outJarPath);
  }
}
