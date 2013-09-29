package org.smoothbuild.task;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.Constants.SET_TASK_NAME;

import java.util.Set;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.DuplicatePathError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public class FileSetTask extends AbstractTask {
  private final ImmutableSet<Task> dependencies;

  public FileSetTask(Set<Task> dependencies, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation));
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  @Override
  public void execute(Sandbox sandbox) {
    MutableFileSet result = sandbox.resultFileSet();

    for (Task entry : dependencies) {
      File from = (File) entry.result();
      if (result.contains(from.path())) {
        sandbox.report(new DuplicatePathError(from.path()));
      }
      MutableFile to = result.createFile(from.path());
      to.setContent(from);
    }

    setResult(result);
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies;
  }
}
