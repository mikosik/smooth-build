package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.MonoReferenceable;

public sealed interface PMonoReferenceable extends PReferenceable, MonoReferenceable
    permits PItem {}
