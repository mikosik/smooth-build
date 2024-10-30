package org.smoothbuild.virtualmachine.wire;

import org.smoothbuild.common.bucket.base.FullPath;

public record VmConfig(FullPath projectPath, FullPath computationDbPath, FullPath bytecodeDbPath) {}
