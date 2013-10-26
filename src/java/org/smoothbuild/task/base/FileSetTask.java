package org.smoothbuild.task.base;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import java.util.List;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.base.err.DuplicatePathError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public class FileSetTask extends AbstractTask {
  private final ImmutableList<Task> elements;

  public FileSetTask(List<Task> elements, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation));
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public void execute(Sandbox sandbox) {
    FileSetBuilder builder = sandbox.fileSetBuilder();

    for (Task task : elements) {
      File from = (File) task.result();
      if (builder.contains(from.path())) {
        sandbox.report(new DuplicatePathError(from.path()));
      } else {
        builder.add(from);
      }
    }

    setResult(builder.build());
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return elements;
  }
}
