package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.PolyRefableObj;

public sealed interface PolyRefableObjN extends RefableObjN, PolyRefableObj
    permits FuncN {
}
