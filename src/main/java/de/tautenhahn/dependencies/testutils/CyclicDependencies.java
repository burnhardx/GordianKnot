package de.tautenhahn.dependencies.testutils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import de.tautenhahn.dependencies.analyzers.CycleFinder;
import de.tautenhahn.dependencies.analyzers.DiGraph;
import de.tautenhahn.dependencies.analyzers.DiGraph.IndexedNode;
import de.tautenhahn.dependencies.parser.ContainerNode;
import de.tautenhahn.dependencies.parser.Node;
import de.tautenhahn.dependencies.parser.Node.ListMode;
import de.tautenhahn.dependencies.parser.ProjectScanner;


/**
 * Checks the enclosing project for cyclic dependencies on package and jar level. Define an own test class
 * which fails if results are worse than expected.
 * 
 * @author TT
 */
public class CyclicDependencies
{

  public String getPackageCycleReport()
  {
    ProjectScanner scanner = new ProjectScanner();
    ContainerNode root = scanner.scan(ClassPathUtils.getClassPath()
                                                    .stream()
                                                    .map(Paths::get)
                                                    .filter(Files::isDirectory)
                                                    .collect(Collectors.toList()));
    root.walkSubTree().forEach(n -> n.setListMode(ListMode.LEAFS_COLLAPSED));
    DiGraph packageDeps = new DiGraph(root);
    CycleFinder cycles = new CycleFinder(packageDeps);
    StringBuilder result = new StringBuilder();
    cycles.getStrongComponents().stream().filter(c -> c.size() > 1).forEach(c -> explainCycle(c, result));
    return result.toString();
  }

  private void explainCycle(List<IndexedNode> c, StringBuilder result)
  {
    result.append("Found cyclic dependency:\n");
    c.forEach(n -> n.getSuccessors()
                    .stream()
                    .filter(c::contains)
                    .forEach(s -> explainDependency(n.getNode(), s.getNode(), result)));
  }

  private void explainDependency(Node n, Node s, StringBuilder result)
  {
    result.append(getPackageName(n)).append(" depends on ").append(getPackageName(s)).append("   ");
    n.getDependencyReason(s)
     .forEach(p -> result.append(p.getFirst().getSimpleName())
                         .append("->")
                         .append(p.getSecond().getSimpleName())
                         .append(", "));
    result.append("\n");
  }

  private String getPackageName(Node n)
  {
    String result = n.getName();
    return result.substring(result.indexOf('.') + 1);
  }
}