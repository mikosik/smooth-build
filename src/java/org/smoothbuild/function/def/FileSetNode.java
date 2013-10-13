package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.FileSetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class FileSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;
  private final CodeLocation codeLocation;

  public FileSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    this.elements = elements;
    this.codeLocation = codeLocation;
  }

  @Override
  public Type type() {
    return Type.FILE_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<HashCode> builder = ImmutableList.builder();
    for (DefinitionNode node : elements) {
      HashCode hash = taskGenerator.generateTask(node);
      builder.add(hash);
    }
    ImmutableList<HashCode> elementHashes = builder.build();
    return new FileSetTask(elementHashes, codeLocation);
  }

}
