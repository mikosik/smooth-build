package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.err.DuplicatePathError;

import com.google.common.collect.ImmutableList;

public class FileSetTask implements Task {
  private final ImmutableList<Result> elements;

  public FileSetTask(List<Result> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public String name() {
    return "File*";
  }

  @Override
  public Value execute(Sandbox sandbox) {
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
