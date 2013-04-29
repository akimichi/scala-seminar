import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter02Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 2."){
    info("本章では、副作用をもたない式 expression と副作用を持つ文 statement について")
    describe("Sec 2.1"){
      it("条件構造の if も式 expression である"){
        info("式 expression とは評価されると値を返すものであり、一方で文 statement とは値を返さずに副作用を生じるものである")
        var x = 0
        val s = if (x > 0) 1 else -1
        s should equal(-1)
        info("返り値を捨てれば文のように作用するが、好ましいスタイルではない")
        if (x > 0) {
          x = x
          x should be >= 0
        } else {
          x = - x
          x should be >= 0
        }
      }
      it("したがってif式も型 type を持つ"){
        info("値は常に何らかの型に帰属するものであり、式が常に何らかの値を返すものであれば、式は常に型を持つ")
        val x = 10
        val s = if (x > 0) 1 else -1
        s.isInstanceOf[Int] should equal(true)
      }
      it("演習: 次のif式はどのような型を持つか"){
        val x = "a string"
        val s = if (x.length > 0)
                  x.length
                else
                  "empty string"
        s.isInstanceOf[Any] should equal(true)
        info("src/test/resources/scala_class_hierachy.jpg を参照")
      }
    }
    describe("Sec 2.2"){
      it("多くの場合にセミコロンは省略できる。文の区切り"){
        pending
      }
    }    
    describe("Sec 2.3"){
      it("ブロック内で複数の式を逐次実行する"){
        pending
      }
    }    
    describe("Sec 2.4"){
      it("標準出力に出力する"){ pending }
      it("標準入力から入力する"){ pending }
      info("副作用を持つメソッドはテストが難しい")
    }
    describe("Sec 2.5"){
      it("forでループする"){}
      it("breakは標準では提供されていない"){}
      it("breakableでloopを抜ける"){
        import scala.util.control.Breaks
        
        val breaks = new Breaks()
        import breaks.{break,breakable}
        var sum = 0
        breakable {
          for(i <- 1 to 100) {
            sum = sum + i
            if(i > 5)
              break
          }
        }
        sum should equal(21)
      }
      it("再帰でbreak同等を実現する"){
        val upto = 6
        def loop(range:Range,accumulator:Int):Int = {
          val head = range.head
          if(head > upto)
            accumulator
          else
            loop(range.tail, accumulator + range.head)
        }
        val range:Range = 1 to 100
        loop(range, 0) should equal(21)
      }
    }
  }
}

