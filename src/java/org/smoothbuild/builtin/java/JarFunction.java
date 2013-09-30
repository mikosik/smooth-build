package org.smoothbuild.builtin.java;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class JarFunction {

  public interface Parameters {
    @Required
    public FileSet files();

    public String output();

    public File manifest();
  }

  @SmoothFunction("jar")
  public static File execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private static final Path DEFAULT_OUTPUT = path("output.jar");
    private final Sandbox sandbox;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      MutableFile output = sandbox.createFile(outputPath());
      try (JarOutputStream jarOutputStream = createOutputStream(output);) {
        for (File file : params.files()) {
          addEntry(jarOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return output;
    }

    private Path outputPath() {
      if (params.output() == null) {
        return DEFAULT_OUTPUT;
      } else {
        return validatedPath("output", params.output());
      }
    }

    private JarOutputStream createOutputStream(MutableFile output) throws IOException {
      if (params.manifest() == null) {
        return new JarOutputStream(output.openOutputStream());
      } else {
        Manifest manifest = new Manifest(params.manifest().openInputStream());
        return new JarOutputStream(output.openOutputStream(), manifest);
      }
    }

    private void addEntry(JarOutputStream jarOutputStream, File file) throws IOException {
      JarEntry entry = new JarEntry(file.path().value());
      jarOutputStream.putNextEntry(entry);

      try (InputStream inputStream = file.openInputStream();) {
        int readCount = inputStream.read(buffer);
        while (readCount > 0) {
          jarOutputStream.write(buffer, 0, readCount);
          readCount = inputStream.read(buffer);
        }
      }

      jarOutputStream.closeEntry();
    }
  }
}
