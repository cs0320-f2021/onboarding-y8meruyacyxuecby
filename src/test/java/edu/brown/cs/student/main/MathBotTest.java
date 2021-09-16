package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  // TODO: add more unit tests of your own
  @Test
  public void testBoth() {
    MathBot bot = new MathBot();
    double ans = bot.add(bot.subtract(18, 17), 5);
    assertEquals(6, ans, 0.01);
  }

  @Test
  public void testZero() {
    MathBot bot = new MathBot();
    assertEquals(0.0, bot.add(0.0, 0.0), 0.01);
    assertEquals(0.0, bot.add(-1.0, 1.0), 0.01);
    assertEquals(0.0, bot.subtract(0.0, 0.0), 0.01);
    assertEquals(0.0, bot.subtract(1.0, 1.0), 0.01);
  }

  @Test
  public void testNegative() {
    MathBot bot = new MathBot();
    assertEquals(2.0, bot.add(5.0, -3.0), 0.01);
    assertEquals(2.0, bot.add(-3.0, 5.0), 0.01);
    assertEquals(2.0, bot.subtract(0.0, -2.0), 0.01);
    assertEquals(-2.0, bot.subtract(-4.0, -2.0), 0.01);
  }

  @Test
  public void testDecimals() {
    MathBot bot = new MathBot();
    assertEquals(2.5, bot.add(5.0, -2.5), 0.001);
    assertEquals(1.748, bot.add(-3.252, 5.0), 0.00001);
    assertEquals(2.11, bot.subtract(0.11, -2.0), 0.0001);
  }
}
