package org.smoothbuild.common.testing;

import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.testing.TestingBucketId.PROJECT;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;

public class TestingFullPath {
  public static final FullPath PROJECT_PATH = fullPath(PROJECT, Path.root());
  public static final FullPath COMPUTATION_DB_PATH =
      fullPath(PROJECT, path(".smooth/computations"));
  public static final FullPath BYTECODE_DB_PATH = fullPath(PROJECT, path(".smooth/bytecode"));
}
