import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter03Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 3."){
    info("ランダムアクセスが必要な場合は、Listではなく、Vectorを使うべき")
    info("マルチスレッドで動作する場合は、ArrayListではなく、immutable なVectorを使うべき")
    info("src/test/resources/scala_collection_hierachy.png")

    describe("ListとVectorの性能比較"){
      /*
       *
       * val max = 10000000
       * val vector = Vector.range(1, max)
       * val list = List.range(1, max)
       * time{list(max - 2)}
       * time{vector(max - 2)}
       * time{vector(0)}
       * time{list(0)}
       */ 

      
    }
    describe("パターン"){
      trait Abstract {
        val collection:Seq[Int]
        def get(index:Int):Int = collection(index)
      }

      it("テスト"){
        val list = new Abstract {
          val collection = List.range(1,100000)
        }
        list.get(0)
        val vector  = new Abstract {
          val collection = Vector.range(1,100000)
        }
      }
    }
  }
}

