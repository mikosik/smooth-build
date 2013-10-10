package org.smoothbuild.function.def.args;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterators.unmodifiableIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AssignmentList implements Iterable<Assignment> {
  private final List<Assignment> assignments;
  private final Set<String> namesAlreadyAssigned;

  public AssignmentList() {
    this.assignments = Lists.newArrayList();
    this.namesAlreadyAssigned = Sets.newHashSet();
  }

  public void add(Assignment assignment) {
    String asignedName = assignment.assignedName();
    checkState(!namesAlreadyAssigned.contains(asignedName));
    assignments.add(assignment);
    namesAlreadyAssigned.add(asignedName);
  }

  public Iterator<Assignment> iterator() {
    return unmodifiableIterator(assignments.iterator());
  }

  @Override
  public String toString() {
    int maxParamType = calculateLongestParamType(assignments);
    int maxParamName = calculateLongestParamName(assignments);
    int maxArgType = calculateLongestArgType(assignments);
    int maxArgName = calculateLongestArgName(assignments);
    int maxNumber = calculateLongestArgNumber(assignments);
    StringBuilder builder = new StringBuilder();

    for (Assignment assignment : assignments) {
      String paramPart = assignment.param().toPaddedString(maxParamType, maxParamName);
      Argument argument = assignment.argument();
      String argPart = argument.toPaddedString(maxArgType, maxArgName, maxNumber);
      builder.append("  " + paramPart + " <- " + argPart + "\n");
    }
    return builder.toString();
  }

  private static int calculateLongestParamType(List<Assignment> assignments) {
    int result = 0;
    for (Assignment assignment : assignments) {
      result = Math.max(result, assignment.param().type().name().length());
    }
    return result;
  }

  private static int calculateLongestParamName(List<Assignment> assignments) {
    int result = 0;
    for (Assignment assignment : assignments) {
      result = Math.max(result, assignment.param().name().length());
    }
    return result;
  }

  private static int calculateLongestArgType(List<Assignment> assignments) {
    int result = 0;
    for (Assignment assignment : assignments) {
      result = Math.max(result, assignment.argument().type().name().length());
    }
    return result;
  }

  private static int calculateLongestArgName(List<Assignment> assignments) {
    int result = 0;
    for (Assignment assignment : assignments) {
      result = Math.max(result, assignment.argument().nameSanitized().length());
    }
    return result;
  }

  private static int calculateLongestArgNumber(List<Assignment> assignments) {
    int maxNumber = 0;
    for (Assignment assignment : assignments) {
      maxNumber = Math.max(maxNumber, assignment.argument().number());
    }
    return Integer.toString(maxNumber).length();
  }
}
