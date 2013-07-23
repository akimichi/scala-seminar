package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter12Spec extends FunSpec with ShouldMatchers with helpers {
  describe("sec 12.1: Functions as Values"){
    import scala.math._
    val num = 3.14
    val fun: Double => Double = ceil _
    fun(num) should equal(4.0)
    Seq(3.14,1.42,2.0).map(fun) should equal{
      List(4.0, 2.0, 2.0)
    }
  }
  describe("sec 12.2: Anonymous Functions"){
    val triple = (x:Double) => 3 * x
    Seq(3.14,1.42,2.0).map(triple) should equal{
      List(9.42, 4.26, 6.0)
    }
    Seq(3.14,1.42,2.0).map{(x:Double) => 3 * x} should equal{
      List(9.42, 4.26, 6.0)
    }
    
  }
  describe("sec 12.3: Functions with Function Parameters"){
    import scala.math._

    it("関数を引数にとるメソッド"){
      def valueAtOneQuarter(f:Double => Double) = f(0.25)
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
    def valueAtOneQuarter(f:Double => Double) = f(0.25)
    valueAtOneQuarter{x:Double => 3 * x} should equal(0.75)
    valueAtOneQuarter{x => 3 * x} should equal(0.75)
    valueAtOneQuarter{3 * _} should equal(0.75)

    it("引数の型はコンパイラが推論できないため、必須である"){
      val fun: Double => Double = 3 * _
      fun(3) should equal{9}
      val fun2 = 3 * (_:Double)
      fun2(3) should equal{9}
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
      (1 to 9).foldRight(0)(_ + _)should equal{
        45
      }
      (1 to 9).foldRight(1)(_ * _)should equal{
        362880
      }
      "123456789".foldRight(0){(_,n) => 1 + n} should equal{
        9
      }
      List(1,2,3,4,5,6,7,8,9).foldRight(List.empty[Int]){(i,accum) =>
        val p:Int => Boolean = {x =>
          x % 2  == 0
        }
        if(p(i))
          i::accum
        else
          accum
      } should equal{
        List(2, 4, 6, 8) 
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
      filter(List(1,2,3,4,5)){_ % 2 != 0} should equal(List(1,3,5))
    }
    it("for表現を利用してfilterを定義する"){
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
    it("callbackの例"){
      val x = 10
      val caller: (Int => Int) => Int = {callback =>
        callback(x)
      }
    }
    it("平方根の例"){
      trait squareRoot {
        val precision:Double
        val guess:Double
        
        def squareRootSimple(x : Double) : Double = squareRootIter(guess,x)
        def squareRootIter(guess:Double, x : Double) : Double = {
          if (goodEnough(guess, x))
            guess
          else
            squareRootIter(improveGuess(guess, x), x)
        }
        
        private def improveGuess(guess:Double, x : Double) : Double = {
          val newguess = average(guess, x / guess)
          // println("guess for x " + x + " improved to = " + newguess)
          newguess
        }

        private def goodEnough(guess:Double, x : Double) = {
          (square(guess) - x).abs  < precision
        }

        def squareRoot(x : Double) : Double = {
          def squareRootIter(guess:Double, x : Double) : Double = {
            if (goodEnough(guess)) {
              guess
            }
            else {
              squareRootIter(improveGuess(guess), x)
            }
          }
          def improveGuess(guess:Double) : Double = {
            val newguess = average(List(guess, x / guess))
            println("guess for x " + x + " improved to = " + newguess)
            newguess
          }
          
          def goodEnough(guess : Double):Boolean = {
            (square(guess) - x).abs  < precision
          }
          squareRootIter(guess, x)
          // squareRootIter(1.0, x)
        }

        private def average(x : Double, y : Double) : Double = {
          (x + y) / 2
        }

        private def average(list : List[Double]) : Double = list.foldLeft(0.0)(_ + _) / list.size

        private def square(value : Double) : Double = value * value
      }
    }
  }
  describe("sec 12.7: SAM Conversions"){
    
  }
  describe("sec 12.8: Currying"){
    describe("curry化を用いた mapReduce抽象(Couseraのコース 2.3の例)") {
      object test {
        def mapReduce(map: Int => Int, reduce:(Int,Int) => Int, zero:Int)(a:Int,b:Int): Int = {
          if(a > b) zero
          else reduce(map(a), mapReduce(map, reduce, zero)(a+1,b))
        }
        def product(map:Int => Int)(a:Int, b:Int):Int = mapReduce(map, (x,y) => x*y, 1)(a,b)
        def sum(map:Int => Int)(a:Int, b:Int):Int = mapReduce(map, (x,y) => x+y, 0)(a,b)
      }
      it("mapReduceを実行する") {
        import test._
        product(x => x*x)(3,4) should equal(144)
      }
    }
  }
  describe("sec 12.9: Control Abstractions"){
    
  }
  describe("sec 12.10: The return Expression"){
    
  }
}
    
