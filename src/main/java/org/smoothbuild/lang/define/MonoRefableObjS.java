package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.MonoRefableObj;

public sealed interface MonoRefableObjS extends RefableObjS, MonoObjS, MonoRefableObj
    permits ValS {
}
