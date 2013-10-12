package org.smoothbuild.task;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.Constants.SET_TASK_NAME;

import java.util.List;

import org.smoothbuild.hash.HashTask;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.DuplicatePathError;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class FileSetTask extends AbstractTask {
  private final ImmutableList<HashCode> elements;

  public FileSetTask(List<HashCode> elements, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation), HashTask.fileSet(elements));
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public void execute(Sandbox sandbox, HashedTasks hashedTasks) {
    MutableFileSet result = sandbox.resultFileSet();

    for (HashCode hash : elements) {
      File from = (File) hashedTasks.get(hash).result();
      if (result.contains(from.path())) {
        sandbox.report(new DuplicatePathError(from.path()));
      } else {
        MutableFile to = result.createFile(from.path());
        to.setContent(from);
      }
    }

    setResult(result);
  }

  @Override
  public ImmutableCollection<HashCode> dependencies() {
    return elements;
  }
}
