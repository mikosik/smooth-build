package org.smoothbuild.virtualmachine.evaluate.compute;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;

public record ComputationCacheConfig(FileSystem fileSystem, Path diskCachePath) {}
