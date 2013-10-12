package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.StringSetTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class StringSetNode implements DefinitionNode {
  private final ImmutableList<? extends DefinitionNode> elements;
  private final CodeLocation codeLocation;

  public StringSetNode(ImmutableList<? extends DefinitionNode> elements, CodeLocation codeLocation) {
    this.elements = elements;
    this.codeLocation = codeLocation;
  }

  @Override
  public Type type() {
    return STRING_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<HashCode> builder = ImmutableList.builder();
    for (DefinitionNode node : elements) {
      HashCode hash = taskGenerator.generateTask(node);
      builder.add(hash);
    }
    ImmutableList<HashCode> elementHashes = builder.build();
    return new StringSetTask(elementHashes, codeLocation);
  }
}
