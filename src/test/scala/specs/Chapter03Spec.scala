import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter03Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 3."){
    describe("Arrayの問題点"){
      it("Arrayはmutableである"){
        val array = Array(1,2,3,4,5)
        array(1) should equal(2)
        array(1) = 0
        array(1) should equal(0)
      }
    }
    info("マルチスレッドで動作する場合は、immutable なListやVectorを使うべき")
    info("ランダムアクセスが必要な場合は、Listではなく、Vectorを使うべき")
    info("src/test/resources/scala_collection_hierachy.png")
    describe("ListとVectorの性能比較"){
      /*
       * val max = 10000000
       * val vector = Vector.range(1, max)
       * val list = List.range(1, max)
       * time{list.last}
       * time{vector.last}
       * time{vector(0)}
       * time{list(0)}
       */ 
    }
    describe("使い分けのためのパターン"){
      it("traitの場合"){
        trait Abstract {
          val collection:Seq[Int]
        }
        
        val list = new Abstract {
          val collection = List.range(1,100000)
        }
        list.collection(0) should equal(1)
        val vector  = new Abstract {
          val collection = Vector.range(1,100000)
        }
        vector.collection(99998) should equal(99999)
      }
      it("methodの場合"){
        def get[T](collection:Seq[T], index:Int):T = collection(index)
        get(List.range(1,100000),0) should equal(1)
        get(Vector.range(1,100000),99998) should equal(99999)
      }        
    }
  }
}

