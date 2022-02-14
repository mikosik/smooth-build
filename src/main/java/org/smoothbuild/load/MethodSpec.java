package org.smoothbuild.load;

import org.smoothbuild.bytecode.obj.val.BlobB;

public record MethodSpec(BlobB jar, String classBinaryName, String methodName) {}
