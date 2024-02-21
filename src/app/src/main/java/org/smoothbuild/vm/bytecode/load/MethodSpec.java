package org.smoothbuild.vm.bytecode.load;

import org.smoothbuild.vm.bytecode.expr.value.BlobB;

/**
 * Specifies java method by providing method name, binary name of enclosing class and jar content as
 * BlobB.
 */
public record MethodSpec(BlobB jar, String classBinaryName, String methodName) {}
