import scala.concurrent.{ExecutionContext, Future}


class Database {
  def insert(data: String) = s"$data-written"
}

class AsyncTask {

  val database = new Database

  def process(data: String)(implicit executionContext: ExecutionContext): Future[String] = {
    Future.apply({
      Thread.sleep(2000)
      database.insert(data)
    })(executionContext)
  }
}
