package test

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter06Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 6."){
    describe("sec 6.1"){
      object Accounts {
        private var lastNumber = 0
        def newUniqueNumber() = { lastNumber += 1; lastNumber }
      }
      Accounts.newUniqueNumber() should equal(1)
      Accounts.newUniqueNumber() should equal(2)
      
    }
    describe("sec 6.2"){
      class Account {
        val id = Account.newUniqueNumber() // class Account内からアクセス可能だが、スコープ内ではない。
        private var balance = 0.0
        def deposit(amount: Double) { balance += amount }
      }
      info("同一ソースファイル中に、同じ名前の class と object があるとき、その object を companion object という")
      object Account { // The companion object
        def apply() = new Account
        private var lastNumber = 0
        def newUniqueNumber() = { lastNumber += 1; lastNumber }
      }
      val account01 = Account()
      val account02 = Account()
      account01.id should equal(1)
      account02.id should equal(2)
    }
    describe("sec 6.3"){
      abstract class UndoableAction(val description: String) {
        def undo(): Unit
        def redo(): Unit
      }
      
      object DoNothingAction extends UndoableAction("Do nothing") {
        override def undo() {}
        override def redo() {}
      }
      class PrintAction(output:String) extends UndoableAction("open a file") {
        once()
        def once(){ println(output) }
        override def undo() {}
        override def redo() {once()}
      }
      val actions = Map("open" -> DoNothingAction, "save" -> DoNothingAction, "print" -> new PrintAction("test"))
      describe("MyOptionを定義する"){
        sealed abstract class MyOption[+A] {
          def isEmpty:Boolean
          def get: A
          def getOrElse[B >: A](default: => B): B = {
            if (isEmpty) default else this.get
          }
        }
        case class MySome[+A](a: A) extends MyOption[A] {
          def isEmpty = false
          def get = a
        }
        case object MyNone extends MyOption[Nothing] {
          def isEmpty = true
          def get = throw new NoSuchElementException("MyNone.get")
        }
        it("MyOptionを使う"){
          val option = MySome(1)
          option.getOrElse(0) should equal(1)
        }
      }
      info("Chapter01Specの MyList における、MyNilもシングルトンオブジェクトである")
    }
    describe("sec 6.4"){
      object test {
        class Account private (val id: Int, val initialBalance: Double) {
          private var balance = initialBalance
        }
        object Account { // The companion object
          private var lastNumber = 0
          private def newUniqueNumber() = { lastNumber += 1; lastNumber }
          def apply(initialBalance: Double) = new Account(newUniqueNumber(), initialBalance)
        }
      }
      it("Account.applyで Accountインスタンスを生成する"){
        import test._
        val acct = Account(1000.0)
        acct.id  should equal(1)
        acct.initialBalance  should equal(1000.0)
      }

      it("Array(100) と new Array(100) は意味が異なる"){
        Array(100) should not equal(new Array(100))
        Array(100)(0) should equal(100)
      }
    }
    describe("sec 6.5"){
      object Hello extends App {
        if (args.length > 0)
          println("Hello, " + args(0))
        else
          println("Hello, World!")
      }
      /* 上記のプログラムは以下のように実行する*
       * 
       * > scalac Hello.scala
       * > scala Hello
       *
       */ 
    }
    describe("sec 6.6"){
      it("列挙型は Enumerationクラスを利用する"){
        object TrafficLightColor extends Enumeration {
          val Red, Yellow, Green = Value
        }
        import TrafficLightColor._
        def doWhat(color: TrafficLightColor.Value):String = {
          color match {
            case Red => "stop"
            case Yellow => "hurry up"
            case _ => "go"
          }
        }
        doWhat(Red) should equal("stop")
        info("idメソッドは、列挙型に割当てられた整数の識別子を返す")
        Red.id should equal(0)
      }
      it("type aliasを使う"){
        object TrafficLightColor extends Enumeration {
          type TrafficLightColor = Value
          val Red, Yellow, Green = Value
        }
        
        import TrafficLightColor._
        def doWhat(color: TrafficLightColor):String = {
          color match {
            case Red => "stop"
            case Yellow => "hurry up"
            case _ => "go"
          }
        }
        doWhat(Red) should equal("stop")
      }
      describe("補足: case object で Enumeration類似の機能を実現する"){
        sealed abstract class TrafficLightColor(val code:Int) {
          def doWhat:String = {
            code match {
              case 0 => "stop"
              case 1 => "hurry up"
              case _ => "go"
            }
          }
          
        }
        object TrafficLightColor {
          case object Red extends TrafficLightColor(0)
          case object Yellow extends TrafficLightColor(1)
          case object Green extends TrafficLightColor(2)
        }
        
        import TrafficLightColor._
        Red.doWhat should equal("stop")
      }
    }
    describe("補足"){
      it("objectの唯一性にはスコープがある"){
        trait Trait {
          object ObjectInTrait {
            var lastNumber = 0
            def increment() = { lastNumber += 1}
            
          }
        }
        val a = new Trait {}
        a.ObjectInTrait.lastNumber should equal(0)
        a.ObjectInTrait.increment()
        a.ObjectInTrait.lastNumber should equal(1)
        val b = new Trait {}
        b.ObjectInTrait.lastNumber should equal(0)
      }
    }
  }
}
