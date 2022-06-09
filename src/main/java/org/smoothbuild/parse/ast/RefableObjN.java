package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.RefableObj;

public sealed interface RefableObjN extends RefableN, RefableObj
    permits MonoRefableObjN, PolyRefableObjN {
}
