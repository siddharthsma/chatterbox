package com.chatterbox

import scala.util.{Success, Try}

/**
 * Created by siddharthambegaonkar on 7/14/14.
 */
case class FailedAuthenticationException(message: String) extends Exception

class AuthenticationService(val token: String) {

  def authenticate: Try[String] = Try(
  if (token != "plop")
    token
  else
    throw FailedAuthenticationException("Invalid token")
  )

}
