package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.MonoReferenceable;

public sealed interface SMonoReferenceable extends SReferenceable, MonoReferenceable
    permits SItem {}
