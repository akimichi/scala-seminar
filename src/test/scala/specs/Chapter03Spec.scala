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
    info("src/test/resources/scala_collection_hierachy.png")
    describe("sec 3.4"){
      val a = List(2,3,5,7,11)
      it("for yield で変換する"){
        val result = for (elem <- a) yield 2 * elem
        result should equal(List(4,6,10,14,22))
      }
      it("for内部でガードを使う"){
        val result = for (elem <- a if elem % 2 == 0) yield 2 * elem
        result should equal(List(4))
      }
      describe("リスト内包で素数を計算する"){
        val factors : Int => Seq[Int] = (n:Int) => {
          val list = List.range(1,n + 1)
          for {
            x <- list
            if n % x == 0
          } yield x
        }
        factors(10) should equal(List(1,2,5,10))
        factors(7) should equal(List(1,7))

        val isPrime : Int => Boolean = (n:Int) => {
          factors(n) == List(1,n)
        }
        isPrime(15) should equal(false)
        isPrime(7) should equal(true)

        val primes : Int => Seq[Int] = (n:Int) => {
          val list = List.range(2,n + 1)
          for {
            x <- list
            if isPrime(x)
          } yield x
        }
        primes(40) should equal(List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37))
      }
    }
    describe("sec 3.5"){
      it("sum") {
        List(1,7,2,9).sum should equal(19)
      }
      it("List#sortedメソッドでListをソートする") {
        val list = List(9,5,7,4,2,6,3,8,1,10)
        list.sorted should equal(List(1,2,3,4,5,6,7,8,9,10))
      }
      it("sortWith") {
        List(1,7,2,9).sortWith{(x,y) =>
          x < y
        } should equal(List(1,2,7,9))
      }
      it("mkString") {
        List(1,7,2,9).mkString(" and ") should equal("1 and 7 and 2 and 9")
      }
    }
    describe("sec 3.6"){
      info("http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List")
      it("List#sizeで長さを取得する") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list.size should equal(10)
      }
      it("List#takeで一部を取得する") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list.take(2) should equal(List(1,2))
      }
      it("List#zipメソッドで2つのListをマージしてtupleを得る"){
        val list1 = List(1,2,3)
        val list2 = List("1","2","3")
        val zipped = list1.zip(list2)
        zipped should equal(List((1,"1"), (2,"2"), (3,"3")))
      }
      it("zip と drop で隣りあわせの要素のペアを持つリストを作る") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list drop(1) should equal (List(2,3,4,5,6,7,8,9,10))
        list.zip(list drop(1)) should equal(List((1,2), (2,3), (3,4), (4,5), (5,6), (6,7), (7,8), (8,9), (9,10)))
      }
      it("List#findメソッドはリストを検索して条件に合致する最初の要素を返す") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list.find(_ % 2 == 0) should equal(Some(2))
        list.find(_ > 10) should equal(None)
      }
      it("List#containsメソッドはリストに該当する要素があるかどうかを返す") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list.contains(1) should equal(true)
      }
      it("List#filterメソッドはリストを検索して条件に合致する最初の要素を返す") {
        val list = List(1,2,3,4,5,6,7,8,9,10)
        list.filter(_ % 2 == 0) should equal(List(2, 4, 6, 8, 10))
      }
      it("List#existsメソッドはリストを検索して条件に合致する最初の要素があるかどうかを調べる") {
        val list = List("foo","bar","bazz")
        list.exists(_.startsWith("f")) should equal(true)
      }
      it("List#forallメソッドはリストを検索して全ての要素が条件に合致するかどうかを調べる") {
        val list = List("foo","bar","gus")
        list.forall(_.length == 3) should equal(true)
      }
      it("List#distictで一意の要素のみに変換する") {
        List(1,2,3,2,1).distinct should equal(List(1, 2, 3))
        (List(3,2,1,2,3) ::: List(2,3,1,3,1)).distinct should equal(List(3, 2, 1))
      }
      it("List#collectメソッドでListの一部の要素のみを置換する"){
        val list = List(1,2,3)
        val new_list = list.collect {
          case i if i == 2 => 0
          case j => j
        }
        new_list should equal(List(1,0,3))
      }
      describe("Listをmatchする") {
        it("Listについて、個数を指定して match させる"){
          val list = List(1,2,3,4,5,6,7,8,9,10)
          list match {
            case list @ List(one,two,three,_,_,_,_,_,_,_) => {
              one should equal(1)
              list.head should equal(one)
            }
            case Nil => fail()
            case _ => fail()
          }
        }
        it("Listについて、パターンでさせる"){
          List(1,2,3,4,5,6,7,8,9,10) match {
            case head::tail => {
              head should equal(1)
              tail should equal(List(2,3,4,5,6,7,8,9,10))
            }
            case _ => fail()
          }
          List(1) match {
            case head::Nil => {
              head should equal(1)
            }
            case head::tail => fail()
            case _ => fail()
          }
        }
      }
    }
    describe("ListとVectorの性能比較"){
      info("List has O(1) prepend and head/tail access. Most other operations are O(n) on the number of elements in the list. This includes the index-based lookup of elements, length, append and reverse.")
      info("http://docs.scala-lang.org/overviews/collections/concrete-immutable-collection-classes.html#vectors")
      info("http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html")
      import System.{currentTimeMillis => now}
      def time[T](f: => T): T = {
        val start = now
        try { f } finally { println("Elapsed: " + (now - start)/1000.0 + " s") }
      }
      val max = 10000000
      val vector = Vector.range(1, max)
      val list = List.range(1, max)
      time{vector(0)} should equal(1)
      time{list(0)} should equal(1)
      time{vector.last} should equal(9999999)
      time{list.last} should equal(9999999)
      info("ランダムアクセスが必要な場合は、Listではなく、Vectorを使うべき")
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

