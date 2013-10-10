package org.smoothbuild.parse.def;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.parse.def.Assignment.assignment;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.FileSetNode;
import org.smoothbuild.function.def.StringSetNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AssignmentList {
  private final List<Assignment> assignments;
  private final Set<String> namesAlreadyAdded;

  public AssignmentList() {
    this.assignments = Lists.newArrayList();
    this.namesAlreadyAdded = Sets.newHashSet();
  }

  public void add(Param param, Argument argument) {
    Assignment assignment = assignment(param, argument);
    checkState(!namesAlreadyAdded.contains(param.name()));
    assignments.add(assignment);
    namesAlreadyAdded.add(param.name());
  }

  public Map<String, DefinitionNode> createNodesMap() {
    Builder<String, DefinitionNode> builder = ImmutableMap.builder();

    for (Assignment assignment : assignments) {
      Param param = assignment.param();
      Argument argument = assignment.argument();
      builder.put(param.name(), convert(param.type(), argument));
    }

    return builder.build();
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

  private static DefinitionNode convert(Type type, Argument argument) {
    if (argument.type() == Type.EMPTY_SET) {
      if (type == Type.STRING_SET) {
        return new StringSetNode(ImmutableList.<DefinitionNode> of(), argument.codeLocation());
      } else if (type == Type.FILE_SET) {
        return new FileSetNode(ImmutableList.<DefinitionNode> of(), argument.codeLocation());
      } else {
        throw new RuntimeException("Cannot convert from " + argument.type() + " to " + type + ".");
      }
    } else {
      return argument.definitionNode();
    }
  }
}
