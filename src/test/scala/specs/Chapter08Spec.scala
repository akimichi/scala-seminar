package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter08Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 8."){
    describe("sec 8.1: Extending a Class"){
      class Person
      class Employee extends Person {
        var salary:Double =  0.0
      }
      val worker = new Employee
      worker.salary should equal(0.0)
    }
    describe("sec 8.2: Overriding Methods"){
      info("親クラスのメソッドを上書きする場合は、 override修飾子を付けることが必要である")
      class Person(val name:String) {
        override def toString = getClass.getSimpleName + "[name=" + name + "]"
      }
      val adam = new Person("adam")
      adam.toString should equal("%s[name=%s]".format(adam.getClass.getSimpleName,adam.name))

      info("superで親クラスのインスタンスを参照する")
      class Employee(override val name:String) extends Person(name) {
        var salary:Double =  0.0
        override def toString = super.toString + "[salary=" + salary + "]"
      }
      val eve = new Employee("eve")
      eve.toString should equal("%s[name=%s][salary=%s]".format(eve.getClass.getSimpleName,eve.name, eve.salary))
      
    }
    describe("sec 8.3: Type Checks and Casts"){
      class Person
      class Employer extends Person {
        var fee:Double =  1000.0
      }
      class Employee extends Person {
        var salary:Double =  10.0
      }
      it("isInstanceOf"){
        val person:Person = new Employee
        if (person.isInstanceOf[Employee]) {
          val employee = person.asInstanceOf[Employee] // s has type Employee
          employee.salary should equal(10.0)
        } else
          fail()
      }
      it("nullの場合"){
        val person:Person = null
        info("null.isInstanceOf[Employee] は、false を返す")
        if (person.isInstanceOf[Employee]) {
          fail("falseになるべき")
        } else {
          info("null.asInstanceOf[Employee] は、null を返す")
          val employee = person.asInstanceOf[Employee]
          employee should equal(null)
        }
      }
      it("person が Employee でない場合"){
        intercept[java.lang.ClassCastException]{
          val person:Int = 1
          val employee = person.asInstanceOf[Employee]
        }
      }
      it("classOf"){
        val person:Person = new Employee
        if (person.getClass == classOf[Employee])
          assert(true)
        else
          fail("trueになるべき")
          
      }
    }
    describe("sec 8.4: Protected Fields and Methods"){
    }
    describe("sec 8.5: Superclass Construction"){
    }
  }
}
