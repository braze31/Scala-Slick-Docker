import org.scalatest._
import org.scalatest.FunSpec
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.StatusCodes

class RestTestScala extends FunSpec {

  describe("Test that server works") {

    it("Check that the server is running and responding") {
      assert(Get("localhost:8080") == StatusCodes.OK)
    }

  }

}