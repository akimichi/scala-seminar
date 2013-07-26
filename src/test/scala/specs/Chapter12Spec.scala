package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter12Spec extends FunSpec with ShouldMatchers with helpers {
  describe("sec 12.1: Functions as Values"){
    info("関数型言語では、関数も値として扱かわれる")
    val num = 3.14
    info("メソッドに _ を付けると関数となる")
    val fun: Double => Double = scala.math.ceil _
    fun(num) should equal(4.0)
    List(3.14,1.42,2.0).map(fun) should equal{
      List(4.0, 2.0, 2.0)
    }
  }
  describe("sec 12.2: Anonymous Functions"){
    info("関数に名前を付ける場合")
    val triple = (x:Double) => 3 * x
    Seq(3.14,1.42,2.0).map(triple) should equal{
      List(9.42, 4.26, 6.0)
    }
    info("一度しか使われない関数は無名関数として定義することが多い")
    Seq(3.14,1.42,2.0).map{(x:Double) => 3 * x} should equal{
      List(9.42, 4.26, 6.0)
    }
    
  }
  describe("sec 12.3: Functions with Function Parameters"){
    import scala.math._

    it("関数を引数にとるメソッド"){
      def valueAtOneQuarter(func:Double => Double) = func(0.25)
      valueAtOneQuarter(ceil _) should equal(1.0)
      valueAtOneQuarter(sqrt _) should equal(0.5)
    }
    it("関数を返り値にとるメソッド"){
      def multBy(factor:Double) = (x:Double) => factor * x
      val quintuple = multBy(5)
      quintuple(20) should equal(100)
    }
  }
  describe("sec 12.4: Parameter Inference"){
    def valueAtOneQuarter(func:Double => Double) = func(0.25)
    valueAtOneQuarter{x:Double => 3 * x} should equal(0.75)
    valueAtOneQuarter{x => 3 * x} should equal(0.75)
    valueAtOneQuarter{3 * _} should equal(0.75)

    it("引数の型はコンパイラが推論できないため、必須である"){
      val fun: Double => Double = 3 * _
      fun(3) should equal{9}
      val fun2 = 3 * (_:Double)
      fun2(3) should equal{9}
      val fun3 = {i:Double =>
        3 * i
      }
      fun3(3) should equal{9}
    }
  }
  describe("sec 12.5: Useful Higher-Order Functions"){
    it("高階関数を受けとる各種メソッドの例"){
      (1 to 9) map {2 * _} should equal{
        Vector(2, 4, 6, 8, 10, 12, 14, 16, 18)
      }
      (1 to 9) filter { _ % 2  == 0} should equal{
        Vector(2, 4, 6, 8)
      }
      (1 to 9).reduceLeft{_ * _} should equal{
        362880
      }
      (1 to 9).foldRight(0){_ + _} should equal{
        45
      }
      (1 to 9).foldRight(1){_ * _} should equal{
        362880
      }
      "abcdefghi".foldRight(0){(_,n) => 1 + n} should equal{
        9
      }
      List(1,2,3,4,5,6,7,8,9).foldRight(List.empty[Int]){(i,accum) =>
        val filterBy:Int => Boolean = {x =>
          x % 2  == 0
        }
        if(filterBy(i))
          i::accum
        else
          accum
      } should equal{
        List(2, 4, 6, 8) 
      }
      
      val func:(Int => Boolean) => (Int,List[Int]) => List[Int] = {filterBy:(Int => Boolean) => {(i,accum) => 
        if(filterBy(i))
          i::accum
        else
          accum
      }}
      val evenFilter:(Int,List[Int]) => List[Int] = func{x:Int =>
        x % 2 == 0
      }
      val oddFilter:(Int,List[Int]) => List[Int] = func{x:Int =>
        x % 2 != 0
      }

      List(1,2,3,4,5,6,7,8,9).foldRight(List.empty[Int])(evenFilter) should equal{
        List(2, 4, 6, 8) 
      }
      List(1,2,3,4,5,6,7,8,9).foldRight(List.empty[Int])(oddFilter) should equal{
        List(1,3,5,7,9)
      }
    }
    
    it("foldRightでfilterを定義する"){
      def filter[T](collection:Seq[T])(filterBy:(T) => Boolean):Seq[T] = {
        collection.foldRight(List.empty[T]){(x,xs) =>
          if(filterBy(x))
            x::xs
          else
            xs
        }
      }
      val odd:Int => Boolean = {x =>
        x % 2  != 0
      }
      filter(List(1,2,3,4,5))(odd) should equal(List(1,3,5))
    }
    it("for構文を利用してfilterを定義する"){
      def filter[T](collection:Seq[T])(filterBy:(T) => Boolean):Seq[T] = {
        for {
          elem:T <- collection
          if filterBy(elem)
        } yield {
          elem
        }
      }
      filter(List(1,2,3,4,5)){elem =>
        elem % 2 == 0
      } should equal(List(2,4))
      val odd:Int => Boolean = {(elem) =>
        elem % 2 != 0
      }
      filter(List(1,2,3,4,5))(odd) should equal(List(1,3,5))
    }
  }
  describe("sec 12.6: Closures"){
    info("closureとは、自由変数を含んだ関数をいう")
    it("closureが参照透明性 referential transparency を破壊するケース"){
      var outer = 1
      val fun = (x:Int) => {
        x + outer
      }
      fun(2) should equal(3)
      outer = 10
      fun(2) should equal(12)
    }
    it("closureが参照透明性 referential transparency を保持するケース"){
      case class Closure(val y:Int) {
        val fun:Int => Int = (x) => {
          x + y
        }
      }
      Closure(1).fun(2) should equal(3)
      Closure(10).fun(2) should equal(12)
      Closure(1).fun(2) should equal(3)
    }
    // it("callbackの例"){
    //   val x = 10
    //   val caller: (Int => Int) => Int = {callback =>
    //     callback(x)
    //   }
    // }
    it("平方根の例"){
      trait squareRoot {
        val precision:Double
        val guess:Double

        /* guess が自由変数である */ 
        val calc: Double => Double = {x =>
          calcIterate(guess)(x)
        }
        /* precision が自由変数である */ 
        val calcIterate: Double => Double => Double = {guess => {x =>
          def average(x : Double, y : Double) : Double = (x + y) / 2
          def square(value : Double) : Double = value * value
          def improveGuess(guess:Double, x : Double) : Double = average(guess, x / guess)
          def goodEnough(guess:Double, x : Double) = (square(guess) - x).abs  < precision
          
          if (goodEnough(guess, x))
            guess
          else
            calcIterate(improveGuess(guess, x))(x)
        }}
      }
      val sqrt = new squareRoot {
        val guess = 1.0
        val precision = 0.01
      }
      sqrt.calc(2.0) should equal(1.4166666666666665)
      
      val sqrt_precise = new squareRoot {
        val guess = 1.0
        val precision = 0.000000001
      }
      sqrt_precise.calc(2.0) should equal(1.4142135623746899)
    }
  }
  describe("sec 12.7: SAM Conversions"){
      
    it("SAMを使った例"){
      object widgets {
        case class Event(name:String)
        
        trait Listener {
          def action(event:Event):Unit
        }
        
        case class Button(name:String) {
          var listeners:Seq[Listener] = List.empty[Listener]
          
          def addListener(listener:Listener):Seq[Listener] = {
            listeners = listener +: listeners
            listeners
          }
          def receive(event:Event) {
            listeners.foreach{ listener:Listener =>
              listener.action(event)
            }
          }
        }
      }
      class Client(var counter:Int = 0) {
        import widgets._
        var buttons:Seq[Button] = Seq.empty[Button]
        def addButton(button:Button):Unit = {
          button.addListener(new Listener {
            def action(event:Event):Unit = {
              counter += 1
            }
          })
          buttons = button +: buttons
        }
        def receive(event:Event):Unit = {
          buttons.foreach{ button:Button =>
            button.receive(event)
          }
        }
      }
      import widgets._
      val client = new Client()
      client.counter should equal(0)
      val button = Button("sample")
      client.addButton(button)
      val event = Event("an event")
      client.receive(event)
      client.counter should equal(1)
    }
    it("implicit method で改善する"){
      object widgets {
        case class Event(name:String)
        
        trait Listener {
          def action(event:Event):Unit
        }
        
        case class Button(name:String) {
          var listeners:Seq[Listener] = List.empty[Listener]
          
          def addListener(listener:Listener):Seq[Listener] = {
            listeners = listener +: listeners
            listeners
          }
          def receive(event:Event) {
            listeners.foreach{ listener:Listener =>
              listener.action(event)
            }
          }
        }
      }
      
      class Client(var counter:Int = 0)  {
        import widgets._
        var buttons:Seq[Button] = Seq.empty[Button]
        implicit def makeAction(func:(Event) => Unit) : Listener = {
          new Listener {
            def action(event:Event):Unit = func(event)
          }
        }
        def addButton(button:Button):Unit = {
          button.addListener{event:Event =>
            counter = counter + 10
          }
          buttons = button +: buttons
        }
        def receive(event:Event):Unit = {
          buttons.foreach{ button:Button =>
            button.receive(event)
          }
        }
      }
      import widgets._
      val client = new Client()
      client.counter should equal(0)
      val button = Button("sample")
      client.addButton(button)
      val event = Event("an event")
      client.receive(event)
      client.counter should equal(10)
    }
  }
  describe("sec 12.8: Currying"){
    describe("curry化を用いた mapReduce抽象(Couseraのコース 2.3の例)") {
      it("mapReduceで整数の集約を実行する") {
        object test {
          def mapReduce(map: Int => Int, reduce:(Int,Int) => Int, zero:Int)(from:Int,to:Int): Int = {
            if(from > to)
              zero
            else
              reduce(map(from), mapReduce(map, reduce, zero)(from+1,to))
          }
          def product(a:Int, b:Int):Int = mapReduce(x => x, (x,y) => x*y, 1)(a,b)
          def sum(a:Int, b:Int):Int = mapReduce(x => x, (x,y) => x+y, 0)(a,b)
        }
        import test._
        mapReduce(x => x,(x,y) => x+y,0)(0,9) should equal{
          0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9
        }
        mapReduce(x => x,(x,y) => x+y,0)(0,9) should equal{
          sum(0,9)
        }
        mapReduce(x => x,(x,y) => x*y,1)(1,10) should equal{
          1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10
        }
        mapReduce(x => x,(x,y) => x*y,1)(1,10) should equal{
          product(1,10)
        }
      }
      it("mapReduceで文字列処理を実行する") {
        object test {
          def mapReduce[T,U](map: Char => U)(reduce:(U,T) => T)(zero:T)(str:String): T = {
            if(str.isEmpty)
              zero
            else {
              val char = str.charAt(0)
              reduce(map(char), mapReduce(map)(reduce)(zero)(str drop 1))
            }
          }
        }
        import test._
        mapReduce[Int,Char]{x => x}{(char,t) => (char.toInt - 48) + t}(0)("0123456789") should equal{
          0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9
        }
        mapReduce[Int,Int]{x => Integer.parseInt(x.toString)}{(integer,t) => integer + t}(0)("0123456789") should equal{
          0 + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9
        }
        mapReduce[Int,Char]{x => x}{(char,t) => if(char == ' ') t+1 else t}(0)("abc defg hij klm") should equal{
          3
        }
        mapReduce[String,Char]{x => (x+1).toChar}{(char,str) => char + str}("")("abc defg hij klm") should equal{
          "bcd!efgh!ijk!lmn"
        }
      }
    }
  }
  describe("sec 12.9: Control Abstractions"){
    def until[T](condition: => Boolean)(block: => Unit):Unit = {
      if(!condition) {
        block
        until(condition)(block)
      }
    }
    it("untilを使う"){
      var index = 10
      var times = 0
      until(index == 0) {
        index -= 1
        times += 1
      }
      index should equal(0)
      times should equal(10)
    }
  }
  describe("sec 12.10: The return Expression"){
    def until[T](condition: => Boolean)(block: => Unit) : Unit = {
      if(!condition) {
        block
        until(condition)(block)
      }
    }
    def indexOf(str:String,ch:Char) : Int = {
      var i = 0
      until(i == str.length) {
        if(str(i) == ch)
          return i
        i += 1
      }
      return -1
    }
    indexOf("012345789",'2') should equal(2)
  }
}
    
