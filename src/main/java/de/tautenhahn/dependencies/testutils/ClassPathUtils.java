package de.tautenhahn.dependencies.testutils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Allows access to the current class path.
 * 
 * @author TT
 */
public final class ClassPathUtils
{

  private ClassPathUtils()
  {
    // utility class
  }

  public static List<Path> parseClassPath(String classpath)
  {
    return Arrays.stream(classpath.split(File.pathSeparator)).map(Paths::get).collect(Collectors.toList());
  }

  /**
   * Returns the class path elements as List.
   */
  public static List<Path> getClassPath()
  {
    return parseClassPath(System.getProperty("java.class.path"));
  }

  /**
   * Returns a new class loader using specified class path. <br>
   * Warning: Loading lots of classes for a big project is not a suitable way to analyze the project. Use only
   * to analyze some special classes.
   * 
   * @param classPath
   */
  public static ClassLoader createClassLoader(List<Path> classPath)
  {
    Collection<URL> urls = classPath.stream().map(ClassPathUtils::toUrl).collect(Collectors.toList());
    return new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
  }

  private static URL toUrl(Path entry)
  {
    try
    {
      return entry.toUri().toURL();
    }
    catch (MalformedURLException e) // cannot happen because JVM does correct escaping
    {
      throw new IllegalArgumentException(e);
    }
  }
}
