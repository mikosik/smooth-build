package org.smoothbuild.lang.base.type.api;

/**
 * The only reason we need this interface is the fact that TypeB is in different package
 * than Type and because we don't use java-modules then TypeB cannot appear normally inside permit
 * clause of Type.
 */
public non-sealed interface TypeBBridge extends Type {
}
