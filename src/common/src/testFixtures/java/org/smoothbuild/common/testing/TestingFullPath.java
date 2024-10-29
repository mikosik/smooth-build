package org.smoothbuild.common.testing;

import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.testing.TestingBucketId.PROJECT;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;

public class TestingFullPath {
  public static final FullPath PROJECT_PATH = fullPath(PROJECT, Path.root());
}
