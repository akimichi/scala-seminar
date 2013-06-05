import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter05Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 5."){
    describe("sec 5.1"){
      it("クラスからインスタンスを作成する"){
        class Counter {
          private var value = 0 // You must initialize the field
          def increment() { value += 1 } // Methods are public by default
          def current() = value
        }
        val myCounter = new Counter
        myCounter.increment()
        myCounter.current should equal(1)
      }
    }
    describe("sec 5.2"){
      it("メンバー変数には、自動的にゲッターとセッターが定義される"){
        class Person {
          var age = 0
        }
        val fred = new Person
        fred.age should equal(0)
        fred.age = 21
        fred.age should equal(21)
        fred.age_=(31)
        fred.age should equal(31)
      }
      /* テキストには誤植がある
       * 
       * $ scalac Person.scala
       * $ javap -private Person
       * Compiled from "Person.scala"
       * public class Person implements scala.ScalaObject {
       *   private int age;
       *   public int age();
       *   public void age_$eq(int);
       *   public Person();
       * }
       *
       */
      it("ゲッター、セッターを自前で定義する"){
        class Person {
          private var privateAge = 0 // Make private and rename
          def age = privateAge
          def age_=(newValue: Int) {
            if (newValue > privateAge) privateAge = newValue; // Can’t get younger
          }
        }
        val fred = new Person
        fred.age = 30
        fred.age = 21
        fred.age should equal(30)
      }
      it("補足: 統一アクセス原則 uniform access principle"){
        info("統一アクセス原則とは、「属性をフィールドとメソッドのどちらで実装するかによってクライアントコードが影響を受けてはならない」というもの")
        info("最初 val で定義しておき、なんらかの処理(値のチェックなど)が必要になった場合に、途中で def に変更してもクライアントには影響を及ぼさない。")
        info("あるいは最初 def で定義しておき、実行効率を重視する場合に、途中で val に変更してもクライアントには影響を及ぼさない。")
      }
      it("ゲッターセッターの可視性を制御する"){
        {
          class Person {
            private var age = 0
          }
        }
        
        {
          class Person {
            val age = 0
          }
        }
        
        {
          class Person {
            private[this] val age = 0
          }
        }
      }
      
    }
    describe("sec 5.3"){
      class Message {
        val timeStamp = new java.util.Date
      }

      class Counter {
        private var value = 0
        def increment() { value += 1 }
        def current = value // No () in declaration
      }

    }
    describe("sec 5.4"){
      info("あるクラスは、同じクラスに属するインスタンスの privateメンバーにアクセスできる")
      class Counter {
        private var value = 0
        def increment() { value += 1 }
        def isLess(other : Counter) = value < other.value
        // Can access private field of other object
      }
      
    }
    describe("sec 5.5"){
      import scala.reflect.BeanProperty
      class Person {
        @BeanProperty var name: String = _
      }

    }
    describe("sec 5.6"){
      it("補助コンストラクタは、必ず主コンストラクあるいは他の補助コンストクタを呼出す必要がある"){
        class Person {
          private var name = ""
          private var age = 0
          def this(name: String) { // An auxiliary constructor
            this() // Calls primary constructor
            this.name = name
          }
          def this(name: String, age: Int) { // Another auxiliary constructor
            this(name) // Calls previous auxiliary constructor
            this.age = age
          }
        }
        val p1 = new Person // Primary constructor
        val p2 = new Person("Fred") // First auxiliary constructor
        val p3 = new Person("Fred", 42) // Second auxiliary constructor
      }
    }
    describe("sec 5.7"){
      it("主コンストラクタによるクラス定義"){
        class Person(val name: String, val age: Int)
        
      }
      it("主コンストラクタは、インスタンス生成時にクラス定義内の文を実行する"){
        class Person(val name: String, val age: Int) {
          println("Just constructed another person")
          def description = name + " is " + age + " years old"
        }
        class MyProg {
          private val props = new Properties
          props.load(new FileReader("myprog.properties"))
        }
      }
      it("さまざまな主コンストラクタ"){
        {
          class Person(val name: String, private var age: Int)
        }
        
        {
          class Person(name: String, age: Int) {
            def description = name + " is " + age + " years old"
          }
        }

        {
          class Person private(val id: Int)
        }


      }

     
    }
    describe("sec 5.8")
      it("クラスのネスト"){
        import scala.collection.mutable.ArrayBuffer
        class Network {
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Member]
          }
          private val members = new ArrayBuffer[Member]
          def join(name: String) = {
            val m = new Member(name)
            members += m
            m
          }
        }
        
        val chatter = new Network
        val myFace = new Network
        val fred = chatter.join("Fred")
        val wilma = chatter.join("Wilma")
        fred.contacts += wilma // OK
        val barney = myFace.join("Barney") // Has type myFace.Member
        fred.contacts += barney
      }
      it("コンパニオンオブジェクトを利用する"){
        object Network {
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Member]
          }
        }
        class Network {
          private val members = new ArrayBuffer[Network.Member]
        }
      }
      it("型プロジェクションを利用する"){
        class Network {
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Network#Member]
          }
        }
      }
      it("self型を利用する"){
        class Network(val name: String) { outer =>
          class Member(val name: String) {
            def description = name + " inside " + outer.name
          }
        }
      }
  }
}
