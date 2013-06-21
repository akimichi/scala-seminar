package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter08Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 8"){
    describe("sec 8.1: Extending a Class"){
      it("extends を用いてクラスを継承する"){
        class Person
        class Employee extends Person {
          var salary:Double =  0.0
        }
        val worker = new Employee
        worker.salary should equal(0.0)
      }
      it("final を指定すれば、メンバーについてもオーバライドを禁止できる"){
        class Person
        class Employee extends Person {
          final var salary:Double =  0.0
        }
      }
    }
    describe("sec 8.2: Overriding Methods"){
      info("親クラスのメソッドをオーバーライドする場合は、 override修飾子を付けることが必要である")
      class Person(val name:String) {
        override def toString = "Person[name=" + name + "]"
      }
      val adam = new Person("adam")
      adam.toString should equal("Person[name=%s]".format(adam.name))

      info("superで親クラスのインスタンスを参照する")
      class Employee(override val name:String) extends Person(name) {
        var salary:Double =  0.0
        override def toString = super.toString + "[salary=" + salary + "]"
      }
      val eve = new Employee("eve")
      eve.toString should equal("Person[name=%s][salary=%s]".format(eve.name, eve.salary))
      
    }
    describe("sec 8.3: Type Checks and Casts"){
      class Person
      class Employer extends Person {
        var fee:Double =  1000.0
      }
      class Employee extends Person {
        var salary:Double =  10.0
      }
      it("isInstanceOfメソッドでオブジェクトがあるクラスに属するかどうかを判定でき、asInstanceOfメソッドで実行時の型にキャストできる"){
        val person:Person = new Employee
        if (person.isInstanceOf[Employee]) {
          person.isInstanceOf[Person] should equal(true)
          val employee = person.asInstanceOf[Employee] // s has type Employee
          employee.salary should equal(10.0)
        } else
          fail()
      }
      it("nullの場合での、isInstanceOfとasInstanceOfの挙動"){
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
      it("person が Employee にキャストできない場合には、asInstanceOfは例外 java.lang.ClassCastException を投げる"){
        intercept[java.lang.ClassCastException]{
          val person:Int = 1
          val employee = person.asInstanceOf[Employee]
        }
      }
      it("classOfとisInstanceOfの違い"){
        val person:Person = new Employee
        person.isInstanceOf[Person] should equal(true)
        person.isInstanceOf[Object] should equal(true)
        person.isInstanceOf[Employee] should equal(true)
        person.getClass should equal(classOf[Employee])
        person.getClass should not equal(classOf[Person])
      }
      it("実行時の型で処理を分岐させるには、パターンマッチのほうが望ましい"){
        val person:Person = new Employee
        person match {
          case employee: Employee => {
            employee.salary should equal(10.0)
          }
          case _ => fail("this should not happen")
        }
        /* 以下はコンパイルエラーである
         val another_person:Person = new Employee
         another_person.salary  should equal(10.0)
         */ 
      }
    }
    describe("sec 8.4: Protected Fields and Methods"){
     info("protectedで指定されたメンバーは、そのクラスを継承したクラスからもアクセス可能である")
     info("javaとは異なり、scalaでは protected指定はpackageスコープではない")
    }
    describe("sec 8.5: Superclass Construction"){
      it("親クラスのコンストラクタの呼出しは主コンストラクタのみから可能であり、補助コンストラクタからは不可能である") {
        class Person(val name:String, val age:Int)      
        class Employee(name: String, age: Int, val salary : Double) extends Person(name, age)
        /* 上記と同じ内容のコードは、javaでは以下のようになる。
         *
         * public class Employee extends Person { // Java
         *    private double salary;
         *    public Employee(String name, int age, double salary) {
         *       super(name, age);
         *       this.salary = salary;
         *    }
         * }
         */ 
      }
      it("javaのクラスを継承する場合は、主コンストラクタを用いてjavaのコンストラクタを呼出す必要がある"){
        import java.awt.Rectangle

        class Square(x: Int, y: Int, width: Int) extends java.awt.Rectangle(x, y, width, width)

      }
    }
    describe("sec 8.6: Overriding Fields"){
      it("メンバーの定義を上書き場合は、 override修飾子を前置する"){
        class Person(val name: String) {
          override def toString = getClass.getName + "[name=" + name + "]"
        }
        class SecretAgent(codename: String) extends Person(codename) {
          override val name = "secret" // SecretAgentのnameメンバーをオーバーライドする
          override val toString = "secret" // SecretAgentのtoStringメソッドをオーバーライドする
        }
      }

      it("抽象クラスのメソッドを、その派生クラスにおいて val でオーバーライドできる"){
        abstract class Person { // See Section 8.8 for abstract classes
          def id: Int // Each person has an ID that is computed in some way
        }
        class Student(override val id: Int) extends Person
      }
      it("補足: 親クラスの抽象メンバーは、原則として def で定義することが望ましい"){
        abstract class AbstractClass {
          def baseMember:Long
        }
        class OverrideByDef extends AbstractClass {
          def baseMember = {
            import org.joda.time._
            val date_time:org.joda.time.DateTime = new DateTime()
            date_time.getMillis
          }
        }
        
        class OverrideByVal extends AbstractClass {
          val baseMember:Long = 100000L
        }
        class OverrideByLazyVal extends AbstractClass {
          lazy val baseMember:Long = {
            def factorial(n:Long):Long = if (n==0) 1 else n * factorial(n-1)
            factorial(10000L)
          }
        }
      }
    }
    describe("sec 8.7: Anonymous Subclasses"){
      it("無名クラスとしてインスタンスを生成する"){
        class Person(val name:String)
        val alien = new Person("Fred") {
          def greeting = "Greetings, Earthling! My name is %s.".format(name)
        }
        alien.greeting should equal("Greetings, Earthling! My name is Fred.")
        info("このとき、インスタンスの型は構造型となっている")
        alien.isInstanceOf[Person{def greeting:String}] should equal(true)
      }
    }
    describe("sec 8.8: Abstract Classes"){
      abstract class Person(val name: String) {
        def id: Int // 定義を省略すると抽象メソッドとなる 
      }
      info("抽象クラスの抽象メソッドを具体化する場合は、override修飾子は不要である")
      class Employee(name: String) extends Person(name) {
        def id = name.hashCode
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
      it("無名クラスでインスタンス化することも可能である"){
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
        intercept[scala.UninitializedFieldError]{
          val ant = new Ant
          ant.range should equal(2)
          ant.env.length should equal(0)
        }
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
          val env: Array[Int] = new Array[Int](range)
        }
        class Worm extends Creature {
          override lazy val range = 5  // ここで lazy を付けてオーバーライドする
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
        }
        val an_item = new Item("an item",10.0, "Red")
        val another_item = new Item("an item",10.0, "Black")
        an_item.equals(another_item) should equal(true)

        val item01 = new Item("an item",10.0, "Red")
        val item02 = new Item("an item",10.0, "Red")
        import scala.collection.mutable.HashSet
        HashSet(item01).contains(item02) should equal(false)
      }
      it("equalsを再定義した場合は、hashCodeも再定義する必要がある"){
        class Item(val name: String, val price: Double, val color:String){
          final override def equals(other: Any) = {
            val that = other.asInstanceOf[Item]
            if (that == null) false
            else name == that.name && price == that.price
          }
          final override def hashCode = 13 * name.hashCode + 17 * price.hashCode
        }
        val item01 = new Item("an item",10.0, "Red")
        val item02 = new Item("an item",10.0, "Red")
        import scala.collection.mutable.HashSet
        HashSet(item01).contains(item02) should equal(true)
        
      }
    }
    describe("補足:case class では、equalsとhashCodeがメンバーから自動的に定義されている"){
      it("colorを等値判定に含める"){
        case class Item(val name: String, val price: Double, val color:String)
        val an_item = Item("an item",10.0, "Red")
        val another_item = Item("an item",10.0, "Black")
        an_item.equals(another_item) should equal(false)
        val an_item_with_same_values = Item("an item",10.0, "Red")
        an_item.equals(an_item_with_same_values) should equal(true)
        
        import scala.collection.mutable.HashSet
        HashSet(an_item).contains(an_item_with_same_values) should equal(true)
      }
      it("colorを等値判定から除きたい場合"){
        case class Item(val name: String, val price: Double) {
          var color:Option[String] = None
        }
        val an_item = Item("an item",10.0)
        an_item.color = Some("Red")
        val another_item = Item("an item",10.0)
        another_item.color = Some("Black")
        an_item.equals(another_item) should equal(true)
        import scala.collection.mutable.HashSet
        HashSet(an_item).contains(another_item) should equal(true)
      }
    }
  }
}
