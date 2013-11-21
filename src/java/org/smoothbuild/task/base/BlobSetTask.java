package org.smoothbuild.task.base;

import static org.smoothbuild.lang.type.Type.BLOB_SET;

import java.util.List;

import org.smoothbuild.lang.plugin.BlobSetBuilder;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class BlobSetTask extends Task {
  private final ImmutableList<Result> elements;

  public BlobSetTask(List<Result> elements, CodeLocation codeLocation) {
    super(BLOB_SET.name(), true, codeLocation);
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
