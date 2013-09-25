package org.smoothbuild.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.FileSystemError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;

public class FileSetTask extends AbstractTask {
  private final ImmutableSet<Task> dependencies;

  public FileSetTask(Set<Task> dependencies) {
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

      try (InputStream is = from.openInputStream(); OutputStream os = to.openOutputStream();) {
        ByteStreams.copy(is, os);
      } catch (IOException e) {
        throw new FileSystemError(e);
      }
    }

    setResult(result);
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies;
  }
}
