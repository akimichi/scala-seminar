package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter10Spec extends FunSpec with ShouldMatchers with helpers {
  trait Action[T] {
    val arg:T
    def invoke:Unit
  }
  trait EmptyAction[T] extends Action[T] {
    def invoke:Unit = { }
  }
  trait ConsoleAction[T] extends Action[T] {
    def invoke:Unit = println(arg)
  }
  trait FileAction[T] extends Action[T] {
    def invoke:Unit = println(arg)
  }
  describe("sec 10.1: Why No Multple Inheritance?"){
  }
  describe("sec 10.2: Traits as Interfaces"){
    trait Logger {
      def log(msg: String) // An abstract method
    }
    
    class ConsoleLogger extends Logger { // Use extends, not implements
      def log(msg: String) { println(msg) } // No override needed
    }
    class SerializableLogger extends Logger with Serializable {
      def log(msg: String) { println(msg) } // No override needed
    }
    
  }
  describe("sec 10.3: Traits with Concrete Implementations"){
    trait ConsoleLogger {
      def log(msg: String) = msg
    }
    trait Account {
      var balance:Double
    }
    class SavingsAccount(val _balance:Double) extends Account with ConsoleLogger {
      override var balance:Double = _balance
      
      def withdraw(amount: Double) {
        if (amount > balance)
          log("Insufficient funds")
        else {
          balance -= amount
        }
      }
    }

  }
  describe("sec 10.4: Objects with Traits"){
    
    trait Logged {
      def log(msg: String):Action[String] =  new EmptyAction[String] {
        val arg = msg
      }
    }
    trait Account {
      var balance:Double = 0.0
    }
    class SavingsAccount extends Account with Logged {
      def withdraw(amount: Double) {
        if (amount > balance)
          log("Insufficient funds")
        else
          balance -= amount
      }
    }
    trait ConsoleLogger extends Logged {
      override def log(msg: String):Action[String] = {
        new ConsoleAction[String] {
          val arg = msg
        }
      }
    }
    trait FileLogger extends Logged {
      override def log(msg: String) = new FileAction[String] {
        val arg = msg
      }
    }
    val acct = new SavingsAccount with ConsoleLogger
    val acct2 = new SavingsAccount with FileLogger

  }
  describe("sec 10.5: Layered Traits"){

    trait Logged {
      def log(msg: String):Action[String] =  new EmptyAction[String] {
        val arg = msg
      }
    }

    trait TimestampLogger extends Logged {
      override def log(msg: String):Action[String] = {
        super.log(new java.util.Date() + " " + msg)
      }
    }
  }
  describe("sec 10.6: Overriding Abstract Methods in Traits"){
  }
  describe("sec 10.7: Traits for Rich Interfaces"){
  }
  describe("sec 10.8: Concrete Fields in Traits"){
  }
  describe("sec 10.9: Abstract Fields in Traits"){
  }
  describe("sec 10.10: Trait Construction Order"){
  }
  describe("sec 10.11: Initializing Trait Fields"){
  }
  describe("sec 10.12: Traits Extending Classes"){
  }
  describe("sec 10.13: Self Types"){
    describe("補足: self type を使えば、循環参照が可能である"){
      trait A {self: B=>}
      trait B {self: A=>}
      /*
       * trait Model extends Evaluator
       * trait Evaluator extends Model
       * 
       */
      trait Model extends Evaluator
      trait Evaluator { self :Model =>
      }
    }
  }
  describe("sec 10.14: What Happens under the Hood"){
  }
}
