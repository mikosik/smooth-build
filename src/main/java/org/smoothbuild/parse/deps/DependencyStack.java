package org.smoothbuild.parse.deps;

import java.util.ArrayDeque;
import java.util.Deque;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.parse.ParseError;
import org.smoothbuild.parse.ast.Named;

public class DependencyStack {
  private final String name;
  private final Deque<StackElem> stack = new ArrayDeque<>();

  public DependencyStack(String name) {
    this.name = name;
  }

  public boolean isEmpty() {
    return stack.isEmpty();
  }

  public void push(StackElem element) {
    stack.addLast(element);
  }

  public StackElem pop() {
    return stack.removeLast();
  }

  public StackElem peek() {
    return stack.getLast();
  }

  public ParseError createCycleError() {
    Name lastMissing = peek().missing().name();
    int first = -1;
    StackElem[] array = stack.toArray(new StackElem[stack.size()]);
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
      StackElem current = array[i];
      Named missing = current.missing();
      builder.append(current.name().toString() + missing.location()
          + " -> " + missing.name() + "\n");
    }
    Location location = array[first].missing().location();
    return new ParseError(location, name + " contains cycle:\n" + builder.toString());
  }
}
