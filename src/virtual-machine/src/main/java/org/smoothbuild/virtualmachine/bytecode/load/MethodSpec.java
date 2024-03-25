package org.smoothbuild.virtualmachine.bytecode.load;

import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;

/**
 * Specifies java method by providing method name, binary name of enclosing class and jar content as
 * BlobB.
 */
public record MethodSpec(BBlob jar, String classBinaryName, String methodName) {}
