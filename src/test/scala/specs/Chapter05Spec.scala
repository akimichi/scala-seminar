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
            // privateメンバーは、そのゲッターセッターも private となる
            private var age = 0
            def increment() { age += 1 }
            def current = age
          }
          val fred = new Person
          fred.increment()
        }
        
        {
          class Person {
            // val の場合は、ゲッターのみが付与される
            val age = 0
          }
          val fred = new Person
          fred.age should equal(0)
        }
        info("private[this]を前置すると、ゲッターセッターが付与されない")
        
        {
          class Person {
            private[this] val age = 0
          }
        }
      }
      
    }
    describe("sec 5.3"){
      info("インスタンス生成後に不変な変数は、val で宣言する")
      class Message {
        val timeStamp = new java.util.Date
      }

      info("インスタンス生成後に可変な変数は、var で宣言する")
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
      it("@BeanPropertyで設定されたゲッターセッターを利用する"){
        val fred = new Person
        fred.setName("fred")
        fred.getName should equal("fred")
      }
    }
    describe("sec 5.6"){
      it("補助コンストラクタは、必ず先頭で主コンストラクタあるいは他の補助コンストクタを呼出す必要がある"){
        class Person() {
          private var name = ""
          private var age = 0
          def this(name: String) { // これは補助コンストラクタ
            this() // ここで主コンストラクタを呼んでいる
            this.name = name
          }
          def this(name: String, age: Int) { // 別の補助コンストラクタ
            this(name) // 前述の補助コンストラクタを呼んでいる
            this.age = age
          }
        }
        val p1 = new Person() // Primary constructor
        val p2 = new Person("Fred") // First auxiliary constructor
        val p3 = new Person("Fred", 42) // Second auxiliary constructor
      }
      it("補足: 多くの補助コンストラクが必要となる場合には、コンパニオンオブジェクトにファクトリーメソッドを定義するほうが簡便である"){
        class Person {
          private var name = ""
          private var age = 0
        }
        object Person {
          def apply(_name: String = "", _age: Int = 0) : Person = new Person {
            var name = _name
            var age = _age
          }
        }
        val p1 = Person()
        val p2 = Person("Fred")
        val p3 = Person("Fred", 42)
        
      }
    }
    describe("sec 5.7"){
      it("主コンストラクタによるクラス定義"){
        class Person(val name: String, val age: Int)
        val fred = new Person("fred", 10)
        fred.name should equal("fred")
        fred.age should equal(10)
        
      }
      it("コンストラクタは、インスタンス生成時にクラス定義内の文を実行する"){
        class Person(val name: String, val age: Int) {
          println("Just constructed another person")
          def description = name + " is " + age + " years old"
        }
      }
      it("インスタンス生成時に、設定ファイルを読みこませる例"){
        class MyProg {
          import java.util.Properties
          import java.io.FileReader
          
          val props = new Properties
          props.load(new FileReader("src/test/resources/myprog.properties"))
        }
        val myprog = new MyProg
        myprog.props.get("foo") should equal("bar")
      }
      describe("さまざまな主コンストラクタ"){
        {
          class Person(val name: String, private var age: Int)
        }
        
        {
          class Person(name: String, age: Int) {
            def description = name + " is " + age + " years old"
          }
        }

        it("主コンストラクの引数を private にする") {
          object test {
            class Person private (val id: Int)
            
            object Person  {
              def apply(id:Int) : Person = new Person(id)
            }
          }
          import test._
          val fred = Person(1)
          
        }
      }
    }
    
    describe("sec 5.8"){
      import scala.collection.mutable.ArrayBuffer
      it("インナークラスを使う"){
        class Network {
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Member]
          }
          private val members = new ArrayBuffer[Member]
          def join(name: String):Member = {
            val m = new Member(name)
            members += m
            m
          }
        }
        val chatter = new Network
        val myFace = new Network
        val fred:chatter.Member = chatter.join("Fred")
        val wilma = chatter.join("Wilma")
        fred.contacts += wilma // OK
        val barney:myFace.Member = myFace.join("Barney") // barney は myFace.Member型となる
        // fred.contacts += barney // fred は chatter.Member型となるので、この行はコンパイルエラーとなる
      }
      it("コンパニオンオブジェクトを利用する"){
        info("コンパニオンオブジェクトとは、同一のソースファイルに同じ名前のクラスとシングルトンを定義したときのシングルトンを指す")
        info("クラスとコンパニオンオブジェクトは、互いのプライベートメンバーにアクセスできる")
        object Network {
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Member]
          }
        }
        class Network {
          private val members = new ArrayBuffer[Network.Member]
          def join(name: String):Network.Member = {
            val m = new Network.Member(name)
            members += m
            m
          }
        }
        val chatter = new Network
        val myFace = new Network
        val fred:Network.Member = chatter.join("Fred")
        val wilma:Network.Member = chatter.join("Wilma")
        fred.contacts += wilma // OK
        val barney:Network.Member = myFace.join("Barney") // barney は Network.Member型となる
        fred.contacts += barney // fred は Network.Member型となる。
      }
      it("パス依存型を利用する"){
        class Network {
          private val members = new ArrayBuffer[Member]
          class Member(val name: String) {
            val contacts = new ArrayBuffer[Network#Member]
          }
          def join(name: String) = {
            val m = new Member(name)
            members += m
            m
          }
        }
        val chatter = new Network
        val myFace = new Network
        val fred:chatter.Member = chatter.join("Fred")
        val wilma:chatter.Member = chatter.join("Wilma")
        val barney:myFace.Member = myFace.join("Barney") // barney は myFace.Member型となる
        fred.contacts += barney // fred は chatter.Member型であるが、contactsが ArrayBuffer[Network#Member]なので、エラーにならない。
      }
      it("self型を利用する"){
        class Network(val name: String) { outer =>
          private val members = new ArrayBuffer[Member]
          class Member(val name: String) {
            def description = name + " inside " + outer.name
            val contacts = new ArrayBuffer[Network#Member]
            // val contacts = new ArrayBuffer[outer.Member]
          }
          def join(name: String) : outer.Member = {
            val m = new Member(name)
            members += m
            m
          }
        }
        val chatter = new Network("chatter")
        val myFace = new Network("myFace")
        val fred:chatter.Member = chatter.join("Fred")
        fred.description should equal("Fred inside chatter")
        val wilma:chatter.Member = chatter.join("Wilma")
        val barney:myFace.Member = myFace.join("Barney") // barney は myFace.Member型となる
        fred.contacts += barney
      }
      it("補足:インナーtraitの例"){
        trait Component {
          trait Answer {
            val result:String
          }
          def computation:Answer
        }
        trait moduleA {
          trait ComponentA extends Component {
            def computation:Answer = new Answer {
              val result = "A"
            }
          }
        }
        trait moduleB {
          trait ComponentB extends Component {
            def computation:Answer = new Answer {
              val result = "B"
            }
          }
        }
        object App extends moduleA with moduleB with Component {
          val componentA = new ComponentA {}
          val componentB = new ComponentB {}
          def computation:Answer = {
            new Answer {
              val result = componentA.computation.result + componentB.computation.result
            }
          }
        }
        App.computation.result should equal("AB")
      }
    }
  }
}
