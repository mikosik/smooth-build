package org.smoothbuild.load;

import org.smoothbuild.bytecode.obj.cnst.BlobB;

public record MethodSpec(BlobB jar, String classBinaryName, String methodName) {}
