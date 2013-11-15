package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.lang.plugin.File;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.err.DuplicatePathError;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class FileSetTask extends Task {
  private final ImmutableList<Result> elements;

  public FileSetTask(List<Result> elements, CodeLocation codeLocation) {
    super("File*", true, codeLocation);
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public String name() {
    return "File*";
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    FileSetBuilder builder = sandbox.fileSetBuilder();

    for (Result task : elements) {
      File from = (File) task.result();
      if (builder.contains(from.path())) {
        sandbox.report(new DuplicatePathError(from.path()));
      } else {
        builder.add(from);
      }
    }
    return builder.build();
  }
}
