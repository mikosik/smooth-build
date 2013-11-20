package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.Value;
import org.smoothbuild.lang.plugin.BlobSetBuilder;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class BlobSetTask extends Task {
  private final ImmutableList<Result> elements;

  public BlobSetTask(List<Result> elements, CodeLocation codeLocation) {
    super("Blob*", true, codeLocation);
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    BlobSetBuilder blobSetBuilder = sandbox.blobSetBuilder();

    for (Result task : elements) {
      blobSetBuilder.add((Blob) task.result());
    }

    return blobSetBuilder.build();
  }
}
