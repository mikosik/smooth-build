package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;

public sealed interface PTypeDefinition extends TypeDefinition permits PStruct, PTypeParam {}
