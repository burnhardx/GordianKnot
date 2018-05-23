package de.tautenhahn.dependencies.core.analyzers;

import org.junit.Test;

import de.tautenhahn.dependencies.analyzers.CycleFinder;
import de.tautenhahn.dependencies.core.InnerNode;
import de.tautenhahn.dependencies.core.Leaf;


/**
 * @author TT
 */
public class TestCycleFinder
{

  @Test
  public void test()
  {
    InnerNode root = InnerNode.createRoot();
    for ( char label = 'a' ; label < 'g' ; label++ )
    {
      root.createLeaf("" + label);
    }
    ((Leaf)root.find("a")).addSuccessor((Leaf)root.find("d"));
    ((Leaf)root.find("a")).addSuccessor((Leaf)root.find("f"));
    ((Leaf)root.find("b")).addSuccessor((Leaf)root.find("a"));
    ((Leaf)root.find("c")).addSuccessor((Leaf)root.find("d"));
    ((Leaf)root.find("d")).addSuccessor((Leaf)root.find("e"));
    ((Leaf)root.find("e")).addSuccessor((Leaf)root.find("a"));
    ((Leaf)root.find("f")).addSuccessor((Leaf)root.find("e"));

    root.walkSubTree().forEach(n -> System.out.println(n + " -> " + n.getSuccessors()));

    new CycleFinder(root);
  }

}