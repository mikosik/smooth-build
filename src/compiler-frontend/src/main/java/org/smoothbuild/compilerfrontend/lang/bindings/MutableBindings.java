package org.smoothbuild.compilerfrontend.lang.bindings;

public sealed interface MutableBindings<E> extends Bindings<E>
    permits FlatMutableBindings, ScopedMutableBindings {
  /**
   * @return element that has been overwritten. For ScopedBindings,
   * when element is present in outer scoped, adding element with same
   * name doesn't overwrite one from outer scope but shadows it so
   * in such case method returns null.
   */
  public E add(String name, E elem);
}
