package org.smoothbuild.load;

import org.smoothbuild.vm.bytecode.expr.value.BlobB;

public record MethodSpec(BlobB jar, String classBinaryName, String methodName) {}
