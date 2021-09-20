package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StarTest {

  @Test
  public void testDistance() {
    Star s1 = new Star(0, "0", 0, 0, 0);
    assertEquals(0, s1.calcDistance(0, 0, 0), 0.01);
    Star s2 = new Star(1, "1", 7, 4, 3);
    assertEquals(10.246951, s2.calcDistance(17, 6, 2), 0.0001);
  }
}
