package org.smoothbuild.lang.builtin.file;

import java.util.Set;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

import com.google.common.collect.Sets;

public class MergeFunction {

  public interface Parameters {
    @Required
    public Array<File> files();

    @Required
    public Array<File> with();
  }

  @SmoothFunction(name = "merge")
  public static Array<File> execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  public static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public Array<File> execute() {
      Set<Path> alreadyAdded = Sets.newHashSet();
      FileSetBuilder builder = sandbox.fileSetBuilder();

      for (File file : params.files()) {
        addFile(file, builder, alreadyAdded);
      }
      for (File file : params.with()) {
        addFile(file, builder, alreadyAdded);
      }

      return builder.build();
    }

    private void addFile(File file, FileSetBuilder builder, Set<Path> alreadyAdded) {
      Path path = file.path();
      if (alreadyAdded.contains(path)) {
        sandbox.report(new DuplicateMergedPathError(path));
      } else {
        alreadyAdded.add(path);
        builder.add(file);
      }
    }
  }
}
