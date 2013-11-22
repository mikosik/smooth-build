package org.smoothbuild.lang.builtin.file;

import java.util.Set;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

import com.google.common.collect.Sets;

public class MergeFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SArray<SFile> with();
  }

  @SmoothFunction(name = "merge")
  public static SArray<SFile> execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  public static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public SArray<SFile> execute() {
      Set<Path> alreadyAdded = Sets.newHashSet();
      ArrayBuilder<SFile> builder = sandbox.fileArrayBuilder();

      for (SFile file : params.files()) {
        addFile(file, builder, alreadyAdded);
      }
      for (SFile file : params.with()) {
        addFile(file, builder, alreadyAdded);
      }

      return builder.build();
    }

    private void addFile(SFile file, ArrayBuilder<SFile> builder, Set<Path> alreadyAdded) {
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
