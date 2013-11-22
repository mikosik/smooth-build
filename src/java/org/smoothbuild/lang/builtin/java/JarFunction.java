package org.smoothbuild.lang.builtin.java;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.Constants;
import org.smoothbuild.lang.builtin.java.err.CannotAddDuplicatePathError;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.Sets;

public class JarFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    public SString output();

    public SFile manifest();
  }

  @SmoothFunction(name = "jar")
  public static SFile execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private static final Path DEFAULT_OUTPUT = path("output.jar");
    private final Sandbox sandbox;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final Set<Path> alreadyAdded;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
      this.alreadyAdded = Sets.newHashSet();
    }

    public SFile execute() {
      FileBuilder fileBuilder = sandbox.fileBuilder();
      fileBuilder.setPath(outputPath());
      try (JarOutputStream jarOutputStream = createOutputStream(fileBuilder);) {
        for (SFile file : params.files()) {
          addEntry(jarOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return fileBuilder.build();
    }

    private Path outputPath() {
      if (params.output() == null) {
        return DEFAULT_OUTPUT;
      } else {
        return validatedPath("output", params.output());
      }
    }

    private JarOutputStream createOutputStream(FileBuilder fileBuilder) throws IOException {
      OutputStream outputStream = fileBuilder.openOutputStream();
      if (params.manifest() == null) {
        return new JarOutputStream(outputStream);
      } else {
        Manifest manifest = new Manifest(params.manifest().openInputStream());
        return new JarOutputStream(outputStream, manifest);
      }
    }

    private void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
      Path path = file.path();
      if (alreadyAdded.contains(path)) {
        throw new ErrorMessageException(new CannotAddDuplicatePathError(path));
      }
      alreadyAdded.add(path);
      JarEntry entry = new JarEntry(path.value());
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
