package scala_seminar

trait helpers {
  import org.scalatest.matchers._
  
  def anInstanceOf[T](implicit manifest: Manifest[T]) = { 
    val clazz = manifest.erasure.asInstanceOf[Class[T]] 
    new BePropertyMatcher[AnyRef] { 
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName) 
    }
  }

   // var mutable:String = "mutable"
   // def get_mutable:String = mutable

   object ExpensiveCalc {
     val NumInterations = 1000

     def expensiveCalc = calculatePiFor(0, 1000000)

     def calculatePiFor(start: Int, nrOfElements: Int): Double = {
       var acc = 0.0
       for (i <- start until (start + nrOfElements))
         acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
       acc
     }
   }

   object mongodb {
     import com.mongodb.casbah.Imports._
    import com.mongodb.{CommandResult}
    import com.mongodb.util._
    import scala.collection.JavaConversions._

    com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers()
    def connection(dbname:String, colname:String):(MongoDB, MongoCollection) = {
      val db = MongoConnection()(dbname)
      val collection = db(colname)
      (db,collection)
    }

    // resourcesディレクトリ下にある fileをMongoDBに読みこむ
    val loadJsonToMongoDB = (collection:MongoCollection) => (source:String) =>  {
      var record = JSON.parse(source).asInstanceOf[BasicDBObject]
      collection.insert(record)
    }
    def aggregationResult(collectionName:String, pipeline: MongoDBList)(implicit database:MongoDB):Either[Throwable, MongoDBList] = {
      val command_result:CommandResult = database.command(MongoDBObject("aggregate" -> collectionName, "pipeline" -> pipeline))
      command_result.ok() match {
        case true => {
          Right(command_result.as[MongoDBList]("result"))
        }
        case false => Left(new Exception(command_result.getErrorMessage()))
      }
    }
  }
  
}
