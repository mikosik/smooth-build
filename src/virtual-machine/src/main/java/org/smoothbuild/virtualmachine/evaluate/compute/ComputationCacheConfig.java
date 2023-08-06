package org.smoothbuild.virtualmachine.evaluate.compute;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;

public record ComputationCacheConfig(FileSystem fileSystem, PathS diskCachePath) {}
