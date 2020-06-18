package org.smoothbuild.parse.deps;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayDeque;
import java.util.Deque;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.parse.ast.Named;

public class DependencyStack<T extends Named> {
  private final String name;
  private final Deque<StackElem<T>> stack = new ArrayDeque<>();

  public DependencyStack(String name) {
    this.name = name;
  }

  public boolean isEmpty() {
    return stack.isEmpty();
  }

  public void push(StackElem<T> element) {
    stack.addLast(element);
  }

  public StackElem<T> pop() {
    return stack.removeLast();
  }

  public StackElem<T> peek() {
    return stack.getLast();
  }

  public Log createCycleError() {
    String lastMissing = peek().missing().name();
    int first = -1;
    @SuppressWarnings("unchecked")
    StackElem<T>[] array = stack.toArray(new StackElem[0]);
    for (int i = array.length - 1; 0 <= i; i--) {
      if (array[i].name().equals(lastMissing)) {
        first = i;
        break;
      }
    }
    if (first == -1) {
      throw new IllegalStateException("Couldn't find expected cycle in call graph.");
    }
    StringBuilder builder = new StringBuilder();
    for (int i = first; i < array.length; i++) {
      StackElem<T> current = array[i];
      Named missing = current.missing();
      builder.append(missing.location());
      builder.append(": ");
      builder.append(current.name());
      builder.append(" -> ");
      builder.append(missing.name());
      builder.append("\n");
    }
    return error(name + " contains cycle:\n" + builder.toString());
  }
}
