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
      Accounts.newUniqueNumber().
      
    }
    describe("sec 6.2"){
      class Account {
        val id = Account.newUniqueNumber()
        private var balance = 0.0
        def deposit(amount: Double) { balance += amount }
      }
      object Account { // The companion object
        private var lastNumber = 0
        private def newUniqueNumber() = { lastNumber += 1; lastNumber }
      }

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
      val actions = Map("open" -> DoNothingAction, "save" -> DoNothingAction)
      
    }
    describe("sec 6.4"){
      class Account private (val id: Int, initialBalance: Double) {
        private var balance = initialBalance
      }
      object Account { // The companion object
        def apply(initialBalance: Double) =
          new Account(newUniqueNumber(), initialBalance)
      }
      val acct = Account(1000.0)
    }
    describe("sec 6.5"){
      object Hello extends App {
        if (args.length > 0)
          println("Hello, " + args(0))
        else
          println("Hello, World!")
      }
    }
    describe("sec 6.6"){
      it("enumeration"){
        object TrafficLightColor extends Enumeration {
          val Red, Yellow, Green = Value
        }
      }
      it("type alias"){
        object TrafficLightColor extends Enumeration {
          type TrafficLightColor = Value
          val Red, Yellow, Green = Value
        }
      }
      
    }
    describe("補足"){
      it("singletonのスコープ"){
        trait Trait {
          object ObjectInTrait {
          }
        }
      }
    }
  }
}
