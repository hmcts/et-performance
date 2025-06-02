package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURLETUIApp = "https://et-sya.#{env}.platform.hmcts.net"
  val baseURLETUIResp = "https://et-syr.#{env}.platform.hmcts.net"
  val baseURL = "https://manage-case.#{env}.platform.hmcts.net"
  val respLoginURL = "https://manage-case.#{env}.platform.hmcts.net"
  val idamURL = "https://idam-web-public.#{env}.platform.hmcts.net"
  val pcqURL = "https://pcq.#{env}.platform.hmcts.net"


  val minThinkTime = 5
  val maxThinkTime = 10

  val HttpProtocol = http

}
