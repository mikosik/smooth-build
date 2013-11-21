package org.smoothbuild.task.base;

import static org.smoothbuild.lang.type.Type.FILE_SET;

import java.util.List;

import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class FileSetTask extends Task {
  private final ImmutableList<Result> elements;

  public FileSetTask(List<Result> elements, CodeLocation codeLocation) {
    super(FILE_SET.name(), true, codeLocation);
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    ArrayBuilder<File> builder = sandbox.fileArrayBuilder();

    for (Result task : elements) {
      File from = (File) task.result();
      builder.add(from);
    }
    return builder.build();
  }
}
