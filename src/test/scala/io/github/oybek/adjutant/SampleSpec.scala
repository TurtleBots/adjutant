package io.github.oybek.adjutant

import munit.FunSuite

@munit.IgnoreSuite
class SampleSpec extends FunSuite {

  test("sample test 1") {
    val a = 1
    val b = 2
    assert(a > b)
  }

  test("sample test 2") {
    val a = 1
    val b = 2
    assert(clue(a) > clue(b))
  }

  test("sample test 2") {
    case class Coord(x: Int, y: Int)
    case class Student(name: String, age: Int, coord: Coord)

    val a = Student("john", 23, Coord(1, 2))
    val b = Student("jane", 18, Coord(2, 2))
    assertEquals(a, b)
  }
}
