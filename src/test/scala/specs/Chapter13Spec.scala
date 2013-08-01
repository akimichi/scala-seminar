package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import scala_seminar._

class Chapter13Spec extends FunSpec with ShouldMatchers with helpers {

  /*
   *
   *
   *
   *
   *
   */

  describe("sec 13.1: The Main Collections Traits"){
    it("companion object の apply でインスタンスを生成できる"){
    }
  }
  describe("sec 13.2: Mutable and Immmutable Collections"){

    it("明示しなれば、デフォルトでは immutable なコレクションとなる"){
      scala.collection.Map("Hello" -> 42) should be {
        anInstanceOf[scala.collection.immutable.Map[String,Int]]
      }
    }
    it("immutable なコレクションへの操作"){
      val numbers = Set(1,2,3)
      val newNumbers = numbers + 9

      def digits(n:Int) : Set[Int] = {
        if(n < 0) digits(-n)
        else if(n < 10) Set(n)
        else digits(n / 10) + (n % 10)
      }
    }

  }
  describe("sec 13.3: Sequences"){
    it("Vector"){
    }
    it("Range"){
    }
  }
  describe("sec 13.4: Lists"){
    val digits = List(4,2)

    def sum(lst:List[Int]) : Int = lst match {
      case Nil => 0
      case h :: t => h + sum(t)
    }
    List(9,4,2).sum should equal(15)
  }
  describe("sec 13.5: Mutable Lists"){

  }
  describe("sec 13.6: Sets"){
    (Set(2,0,1) + 1) should equal{
      Set(2,0,1)
    }
    it("linked hash set"){
      val weekdays = scala.collection.mutable.LinkedHashSet("Mo","Tu","We","Th","Fr")

    }
    it("bit set"){
    }
  }
  describe("sec 13.7: Operators for Adding or Removing Elements"){
    Vector(1,2,3) :+ 5 should equal(Vector(1,2,3,5))
    1 +: Vector(1,2,3) should equal(Vector(1,1,2,3))
    describe("コレクションの更新"){
      it("mutableなコレクションを更新する"){
        val numbers = scala.collection.mutable.ArrayBuffer(1,2,3)
        numbers += 5
      }
      it("immutableなコレクションを更新する"){
        var numbers = scala.collection.immutable.Set(1,2,3)
        numbers += 5
      }
      it("要素を削除する"){
        (Set(1,2,3) - 2) should equal {
          Set(1,3)
        }
      }
      it("複数の要素を追加する"){
        (Set(1,2,3) ++ Set(3,4,5)) should equal {
          Set(1,2,3,4,5)
        }
      }
    }

  }
  describe("sec 13.8: Common Methods"){

  }
  describe("sec 13.9: Mapping a Function"){
    it("mapを使う"){
      val names = List("Peter","Paul","Mary")
      names.map(_.toUpperCase) should equal {
        List("PETER","PAUL","MARY")
      }
      (for {
        n <- names
      } yield n.toUpperCase)  should equal {
        List("PETER","PAUL","MARY")
      }
    }
    it("flatMapを使う"){
      val names = List("Peter","Paul","Mary")
      def ulcase(s:String) = Vector(s.toUpperCase(), s.toLowerCase())
      names.map(ulcase) should equal {
        List(Vector("PETER", "peter"), Vector("PAUL", "paul"), Vector("MARY", "mary"))
      }
      names.flatMap(ulcase) should equal {
        List("PETER", "peter","PAUL", "paul","MARY", "mary")
      }
    }
    it("collectを使う"){
      "-3+4".collect {
        case '+' => 1
        case '-' => -1
      } should equal {
        Vector(-1,1)
      }

    }
  }
  describe("sec 13.10: Reducing,Folding and Scanning"){

  }

}

