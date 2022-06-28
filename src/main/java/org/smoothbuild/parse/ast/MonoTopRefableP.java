package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.MonoTopRefable;

public sealed interface MonoTopRefableP extends TopRefableP, MonoTopRefable
    permits ValP {
}
