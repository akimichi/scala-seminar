package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter10Spec extends FunSpec with ShouldMatchers with helpers {
  info("テストしやすいよう下準備しておく")
  trait Action[+T] {
    def content:T
    def invoke:Unit
  }
  object EmptyAction extends Action[Nothing] {
    def content:Nothing = throw new Exception("should not be called")
    def invoke:Unit = { /* do nothing */}
  }
  trait ConsoleAction[T] extends Action[T] {
    def invoke:Unit = println(content)
  }
  trait FileAction[T] extends Action[T] {
    def filename:String
    
    def invoke:Unit = {
      val out = new java.io.PrintStream(filename)
      out.println(content)
      out.flush
    }
  }
  describe("補足: "){
    info("http://lampwww.epfl.ch/~odersky/papers/ScalableComponent.pdf")
    /*
     class SymbolTable {
       class Name { /* name specific operations */ }
       class Type { /* subclasses of Type and type specific operations */ }  
       class Symbol { /* subclasses of Symbol and symbol specific operations */ }  
       object definitions { /* global definitions */ }
       // other elements
     }
     この方法では、シンボルテーブルだけでなく抽象構文木 Tree も NameやTypeを扱かう必要がある場面で困る。
     */
    class ScalaCompiler extends SymbolTable with Trees
    trait Names {
      class Name { /* name specific operations */ }
    }
    trait  Symbols { self : Names with Types => 
      class Symbol { /* subclasses of Symbol and symbol specific operations */ }
    }
    trait Definitions { self : Names with Symbols => 
      object definitions {  }
    }
    trait Types { self : Names with Symbols with Definitions =>
      class Type { /* subclasses of Type and type specific operations */ }
    }
    class SymbolTable extends Names with Types with Symbols with Definitions
    trait Trees { self : Names with Symbols with Definitions =>
      class Tree { /* Asbtract Syntax Tree */ }
    }

    info("コンパイラのシンボル操作と型操作にログ機能を付加する")
    trait LogSymbols extends Symbols { self : Names with Types => 
      def log: java.io.PrintStream
    }
    trait LogTypes extends Types {self : Names with Symbols with Definitions =>
      def log: java.io.PrintStream
    }
    class LoggedCompiler extends ScalaCompiler with LogSymbols with LogTypes {
      val log: java.io.PrintStream = System.out
    }
  }
  
  describe("sec 10.1: Why No Multple Inheritance?"){
  }
  describe("sec 10.2: Traits as Interfaces"){
    trait Logger {
      def log(msg: String):Action[String] // An abstract method
    }
    
    class ConsoleLogger extends Logger { // Use extends, not implements
      def log(msg: String):ConsoleAction[String] = { // No override needed
       new ConsoleAction[String] { val content = msg}
      }
    }
    class SerializableLogger extends Logger with Serializable {
      def log(msg: String):ConsoleAction[String] = { // No override needed
       new ConsoleAction[String] { val content = msg }
      }
    }
    it("ConsoleLoggerを使う"){
      val logger = new ConsoleLogger
      logger.log("this is a test") should be (anInstanceOf[ConsoleAction[String]])
    }
  }
  describe("sec 10.3: Traits with Concrete Implementations"){
    trait ConsoleLogger {
      def log(msg: String):ConsoleAction[String] = new ConsoleAction[String] {
        val content = msg
      }
    }
    trait Account {
      var balance:Double
    }
    class SavingsAccount(val _balance:Double) extends Account with ConsoleLogger {
      var balance:Double = _balance
      
      def withdraw(amount: Double):Action[String] = {
        if (amount > balance)
          log("Insufficient funds")
        else {
          balance -= amount
          EmptyAction
        }
      }
    }
    it("SavingsAccountを使う"){
      val account = new SavingsAccount(100)
      val first_action = account.withdraw(10)
      first_action should be (anInstanceOf[EmptyAction.type])
      account.balance should equal(90)
      val second_action = account.withdraw(100)
      second_action.content should equal("Insufficient funds")
    }
  }
  describe("sec 10.4: Objects with Traits"){
    info("インスタンスに対しても trait を mixin することができる")
    trait Logged {
      def log(msg: String):Action[String] =  EmptyAction
    }
    trait Account {
      var balance:Double = 0.0
    }
    class SavingsAccount extends Account with Logged {
      def withdraw(amount: Double) :Action[String] = {
        if (amount > balance)
          log("Insufficient funds")
        else {
          balance -= amount
          EmptyAction
        }
      }
    }
    trait ConsoleLogger extends Logged {
      override def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }
    trait FileLogger extends Logged {
      override def log(msg: String) = new FileAction[String] {
        val content = msg
        def filename = "/tmp/filelogger.log"
      }
    }
    val acct = new SavingsAccount with ConsoleLogger
    val acct2 = new SavingsAccount with FileLogger

  }
  describe("sec 10.5: Layered Traits"){

    trait Logged {
      def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }

    trait ConsoleLogger extends Logged {
      override def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }
    trait TimestampLogger extends Logged {
      import java.text._
      override def log(msg: String):Action[String] = {
        val date = {
          val df:DateFormat = new SimpleDateFormat("yyyy/MM/dd")
          df.parse("2013/06/26")
        }
        super.log(date + " " + msg)
      }
    }
    trait ShortLogger extends Logged {
      val maxLength = 15
      override def log(msg:String):Action[String] = {
        super.log(
          if (msg.length <= maxLength) msg else msg.substring(0,maxLength -3) + "..."
        )
      }
    }
    trait Account {
      var balance:Double
    }
    class SavingsAccount(val _balance:Double) extends Account with Logged {
      override var balance:Double = _balance
      def withdraw(amount: Double):Action[String] = {
        if (amount > balance)
          log("Insufficient funds")
        else {
          balance -= amount
          log("The balance is now %f".format(balance))
        }
      }
    }
    
    val acct1 = new SavingsAccount(100) with ConsoleLogger with TimestampLogger with ShortLogger
    val acct2 = new SavingsAccount(100) with ConsoleLogger with ShortLogger with TimestampLogger
    acct1.withdraw(10).content should equal("Wed Jun 26 00:00:00 JST 2013 The balance ...")
    acct2.withdraw(10).content should equal("Wed Jun 26 0...")
  }
  describe("sec 10.6: Overriding Abstract Methods in Traits"){
    trait Logger {
      def log(msg:String):Action[String]
    }
    trait TimestampLogger extends Logger {
      import java.text._
      abstract override def log(msg: String):Action[String] = {
        val date = {
          val df:DateFormat = new SimpleDateFormat("yyyy/MM/dd")
          df.parse("2013/06/26")
        }
        super.log(date + " " + msg)
      }
    }
  }
  describe("sec 10.7: Traits for Rich Interfaces"){
    trait Logger {
      def log(msg:String):Action[String]
      def info(msg:String):Action[String] = { log("INFO: " + msg) }
      def warn(msg:String):Action[String] = { log("WARN: " + msg) }
      def severe(msg:String):Action[String] = { log("SEVERE: " + msg) }
    }
    class SavingsAccount(var balance:Double) extends Logger {
      def log(msg:String):Action[String] = new ConsoleAction[String] { val content = msg }
      def withdraw(amount: Double):Action[String] = {
        if (amount > balance)
          severe("Insufficient funds")
        else {
          balance -= amount
          info("The balance is now %f".format(balance))
        }
      }
    }
    val acct1 = new SavingsAccount(100)
    acct1.withdraw(10).content should equal("INFO: The balance is now 90.000000")
    acct1.withdraw(1000).content should equal("SEVERE: Insufficient funds")
  }
  describe("sec 10.8: Concrete Fields in Traits"){
    it("trait が具象メンバーを持つ場合、その具象メンバーはサブクラスに継承されるのではなく、追加される"){
      trait Logged {
        def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
      }
      trait ShortLogger extends Logged {
        val maxLength = 15
        override def log(msg:String):Action[String] = {
          super.log(
            if (msg.length <= maxLength) msg else msg.substring(0,maxLength -3) + "..."
          )
        }
      }
      class Account {
        var balance:Double = 0.0
      }
      class SavingsAccount extends Account with ShortLogger {
        def withdraw(amount: Double):Action[String] = {
          if (amount > balance)
            log("Insufficient funds")
          else {
            balance -= amount
            log("The balance is now %f".format(balance))
          }
        }
      }
      
      
    }
  }
  describe("sec 10.9: Abstract Fields in Traits"){
    it("抽象メンバーを持つtraitをmixinする"){
      trait Logged {
        def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
      }
      trait ShortLogger extends Logged {
        val maxLength:Int
        override def log(msg:String):Action[String] = {
          super.log(
            if (msg.length <= maxLength) msg else msg.substring(0,maxLength -3) + "..."
          )
        }
      }
      class SavingsAccount(var balance:Double) extends ShortLogger {
        val maxLength = 20 // 抽象メンバーを具体化する必要がある
        def withdraw(amount: Double):Action[String] = {
          if (amount > balance)
            log("Insufficient funds")
          else {
            balance -= amount
            log("The balance is now %f".format(balance))
          }
        }
      }
    }
  }
  describe("sec 10.10: Trait Construction Order"){
    trait Logged {
      println("Logged construction")
      def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }
    trait FileLogger extends Logged {
      println("FileLogger construction")
      val filename = "/tmp/app.log"
      import java.text._
      import java.io.PrintWriter
      val out = new PrintWriter(filename)
      val date = {
        val df:DateFormat = new SimpleDateFormat("yyyy/MM/dd")
        df.parse("2013/06/26")
      }
      out.println("# " + date.toString)
      
      override def log(msg: String) = new FileAction[String] {
        def filename = filename
        val content = msg
      }
    }
    trait ShortLogger extends Logged {
      println("ShortLogger construction")
      val maxLength = 15
      override def log(msg:String):Action[String] = {
        super.log(
          if (msg.length <= maxLength) msg else msg.substring(0,maxLength -3) + "..."
        )
      }
    }
    trait Account {
      println("Account construction")
      var balance:Double
    }
    class SavingsAccount(val _balance:Double) extends Account with ShortLogger {
      override var balance = _balance
      println("SavingsAccount construction")
      def withdraw(amount: Double):Action[String] = {
        if (amount > balance)
          log("Insufficient funds")
        else {
          balance -= amount
          log("The balance is now %f".format(balance))
        }
      }
    }
    val acct1 = new SavingsAccount(100)
    acct1.withdraw(10).content should equal("The balance ...")
    info("Account,Logged, ShortLogger, SavingsAccount の順序で初期化されている")
  }
  describe("sec 10.11: Initializing Trait Fields"){
    info("traitはコンストラクタでパラメータを持てない")
    /* 以下はコンパイルエラーとなる
     * val acct = new SavingsAccount with FileLogger("myapp.log")
     */
    trait Logged {
      def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }
    trait FileLogger extends Logged {
      def filename:String // val filename:String
      
      import java.io.PrintStream
      val out = new PrintStream(filename)
      override def log(msg: String) = new FileAction[String] {
        def filename:String = filename
        val content = msg
      }
    }
    it("抽象メンバーを用いて実現する"){
      class SavingsAccount(var balance:Double) extends FileLogger {
        def filename = "/tmp/app.log" // val filename = "/tmp/app.log"
        
        def withdraw(amount: Double):Action[String] = {
          if (amount > balance)
            log("Insufficient funds")
          else {
            balance -= amount
            log("The balance is now %f".format(balance))
          }
        }
      }
      val acct = new SavingsAccount(100.0) with FileLogger
      acct.withdraw(10.0)
    }
    it("クラスコンストラクタを用いてtraitの抽象メンバーを具体化する"){
      class SavingsAccount(val filename:String, var balance:Double) extends FileLogger {
        def withdraw(amount: Double):Action[String] = {
          if (amount > balance)
            log("Insufficient funds")
          else {
            balance -= amount
            log("The balance is now %f".format(balance))
          }
        }
      }
      val acct = new SavingsAccount("/tmp/app2.log",100.0) with FileLogger
      acct.withdraw(1000.0)
    }
  }
  describe("sec 10.12: Traits Extending Classes"){
    trait Logged {
      def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
    }
    trait LoggedException extends Exception with Logged {
      def log():Action[String] = {
        log(getMessage())
      }
    }
    trait UnhappyException extends LoggedException {
      override def getMessage() = "contentgh!"
    }
    info("mixinするtraitのスーパクラス(この場合は Exception)のサブクラスであれば、さらに継承可能である")
    import java.io.IOException
    trait UnhappyIOException extends IOException with LoggedException {
      override def getMessage() = "contentgh!"
    }
    
  }
  describe("sec 10.13: Self Types"){
    describe("self type を使う"){
      trait Logged {
        def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
      }
      trait LoggedException extends Logged { self : Exception =>
        def log() { log(getMessage) }
      }
      info("self type指定では、利用する側では指定された trait を mixin することが必要となる")
      class Program extends Exception with LoggedException
      
      val program = new Program
    }
    
    describe("self type を使えば、循環参照が可能である"){
      /* 下記コードはコンパイルエラーとなる
       * trait Model extends Evaluator
       * trait Evaluator extends Model
       * 
       */
      trait Model extends Evaluator
      trait Evaluator { self :Model =>
      }
      
      trait A {self: B=>
        def b:B = new B with A
      }
      trait B {self: A=>
        def a:A = new A with B
      }
      class C extends A with B
    }
    describe("self type は構造型に対応している"){
      trait Logged {
        def log(msg: String):Action[String] = new ConsoleAction[String] { val content = msg }
      }
      trait LoggedException extends Logged { self : { def getMessage() : String } => // 構造型による self typeの指定
        def log():Action[String] =  { log(getMessage) }
      }
      trait FakeException {
        def getMessage() : String = "I am not an exception"
      }
      class Program extends FakeException with LoggedException
      
      val program = new Program
      program.log().content  should equal ("I am not an exception")
    }
  }
  describe("sec 10.14: What Happens under the Hood"){
  }
}
