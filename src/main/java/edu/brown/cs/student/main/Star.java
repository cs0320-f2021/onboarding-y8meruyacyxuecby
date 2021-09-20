package edu.brown.cs.student.main;

import java.lang.Math;

/**
 * Star class to hold information about stars from CSV.
 */
public class Star {
  private final int id;
  private final String name;
  private final float x;
  private final float y;
  private final float z;

  public Star(int id, String name, float x, float y, float z) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Method for calculating distance between the Star and a point in 3d space
   * @param x - x coordinate
   * @param y - y coordinate
   * @param z - z coordinate
   * @return double representing distance
   */
  public double calcDistance(float x, float y, float z) {
    return Math.sqrt(
        Math.pow((this.x - x), 2)
            + Math.pow((this.y - y), 2)
            + Math.pow((this.z - z), 2));
  }

  /**
   * Getter for id.
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * Getter for name.
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for X coord.
   * @return X
   */
  public float getX() {
    return x;
  }

  /**
   * Getter for Y coord.
   * @return Y
   */
  public float getY() {
    return y;
  }

  /**
   * Getter for Z coord.
   * @return Z
   */
  public float getZ() {
    return z;
  }

  /**
   * Method to convert Star to a string
   * @return String representing Star
   */
  public String toString() {
    return "Star(" + id + "," + name + "," + x + "," + y + "," + z + ")";
  }
}
