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
      it("パターンマッチのほうが望ましい"){
        val person:Person = new Employee
        person match {
          case employee: Employee => {
            employee.salary should equal(10.0)
          }
          case _ => fail()
        }
        
      }
    }
    describe("sec 8.4: Protected Fields and Methods"){
      
    }
    describe("sec 8.5: Superclass Construction"){
      class Person(val name:String, val age:Int)      
      class Employee(name: String, age: Int, val salary : Double) extends Person(name, age)

      it("javaのクラスを継承する"){
        import java.awt.Rectangle

        class Square(x: Int, y: Int, width: Int) extends java.awt.Rectangle(x, y, width, width)

      }
    }
    describe("sec 8.6: Overriding Fields"){
      {
        class Person(val name: String) {
        override def toString = getClass.getName + "[name=" + name + "]"
        }
        class SecretAgent(codename: String) extends Person(codename) {
          override val name = "secret" // Don’t want to reveal name . . .
          override val toString = "secret" // . . . or class name
        }
      }

      {
        abstract class Person { // See Section 8.8 for abstract classes
          def id: Int // Each person has an ID that is computed in some way
        }
        class Student(override val id: Int) extends Person
        
      }

      
    }
    describe("sec 8.7: Anonymous Subclasses"){
      class Person(val name:String)      
      val alien = new Person("Fred") {
        def greeting = "Greetings, Earthling! My name is %s.".format(name)
      }
      alien.greeting should equal("Greetings, Earthling! My name is Fred.")
    }
    describe("sec 8.8: Abstract Classes"){
      abstract class Person(val name: String) {
        def id: Int // 抽象メソッド
      }
      class Employee(name: String) extends Person(name) {
        def id = name.hashCode // override keyword not required
      }
      val adam = new Employee("adam")
      adam.id should equal(2988943)
    }
    describe("sec 8.9: Abstract Fields"){
      abstract class Person {
        val id: Int // No initializer—this is an abstract field with an abstract getter method
        var name: String // Another abstract field, with abstract getter and setter methods
      }
      class Employee(val id: Int) extends Person { // Subclass has concrete id property
        var name = "undefined" // and concrete name property
      }
      val adam = new Employee(1)
      adam.name should equal("undefined")
      adam.name = "adam"
      adam.name should equal("adam")
      it("無名クラスでインスタンス化する"){
        val fred = new Person {
          val id = 1729
          var name = "Fred"
        }
        fred.name should equal("Fred")
      }
    }
    describe("sec 8.10: Construction Order and Early Definitions"){
      class Creature {
        val range: Int = 10
        val env: Array[Int] = new Array[Int](range) // ここで range を使っているのが問題となる
      }
      it("コンストラクタ初期化の順序がまぎらわしい例"){
        class Ant extends Creature {
          override val range = 2
        }
//      intercept[scala.UninitializedFieldError]{
        val ant = new Ant
        ant.range should equal(2)
        ant.env.length should equal(0)
//      }
      }
      it("early definitionによる解決"){
        class Bug extends {
          override val range = 3
        } with Creature
        val bug = new Bug
        bug.range should equal(3)
        bug.env.length should equal(3)
      }
      it("補足: lazy valによる解決"){
        class Creature {
          def range: Int = 10
          val env: Array[Int] = new Array[Int](range) // ここで range を使っているのが問題となる
        }
        class Worm extends Creature {
          override lazy val range = 5
        } 
        val worm = new Worm
        worm.range should equal(5)
        worm.env.length should equal(5)
      }
    }
    describe("sec 8.11: The Scala Inheritance Hierarchy"){
      
    }
    describe("sec 8.12: Object Equality"){
      it("等値演算"){
        val a_string = new String("string")
        val another_string = new String("string")
        (a_string.eq(another_string)) should equal(false)
        (a_string.eq(a_string)) should equal(true)
        
      }
      it("equalsを再定義する"){
        class Item(val name: String, val price: Double, val color:String){
          final override def equals(other: Any) = {
            val that = other.asInstanceOf[Item]
            if (that == null) false
            else name == that.name && price == that.price
          }
          final override def hashCode = 13 * name.hashCode + 17 * price.hashCode
        }
        val an_item = new Item("an item",10.0, "Red")
        val another_item = new Item("an item",10.0, "Black")
        an_item.equals(another_item) should equal(true)
      }
    }
    describe("補足:case class では、equalsとhashCodeがメンバーから自動的に定義されている"){
    }
  }
}
