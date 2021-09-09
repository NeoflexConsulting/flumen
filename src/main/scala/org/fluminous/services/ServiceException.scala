package org.fluminous.services

import io.circe.{ DecodingFailure, Json, ParsingFailure }

sealed abstract class ServiceException private (val serviceName: String, message: String, cause: Throwable)
    extends Exception(message, cause) {
  def this(serviceName: String, message: String) = {
    this(serviceName, s"Error during service $serviceName invocation: $message", null)
  }
  def this(serviceName: String, throwable: Throwable) = {
    this(serviceName, s"Error during service $serviceName invocation", throwable)
  }
}

case class DeserializationException(override val serviceName: String, deserializationError: DecodingFailure)
    extends ServiceException(serviceName, deserializationError.message)

case class NotFoundInputParameter(override val serviceName: String, expectedParameter: String)
    extends ServiceException(serviceName, s"Expected input parameter $expectedParameter not found")

case class IncompatibleTypeException(override val serviceName: String, expectedType: String, actualType: String)
    extends ServiceException(serviceName, s"Incompatible input type. Expected: $expectedType, actual: $actualType")

abstract class ValidationFailure(val message: String)

case class RequiredInputParameterIsMissing(parameterName: String)
    extends ValidationFailure(s"Required input parameter $parameterName is missing")

case class JSONInputParameterType(parameterName: String, isObject: Boolean, isArray: Boolean, isNull: Boolean)
    extends ValidationFailure(
      s"Parameter $parameterName is ${if (isObject) "JSON Object" else if (isArray) "JSON Array" else "Null"} while JSON value expected"
    )

case class ValidationError(override val serviceName: String, exceptions: Seq[ValidationFailure])
    extends ServiceException(
      serviceName,
      s"Parameter validation errors found:\n ${exceptions.map(_.message).mkString("\n")}"
    )

case class UnsuccessfulHttpStatusCode(override val serviceName: String, url: String, method: String, statusCode: String)
    extends ServiceException(
      serviceName,
      s"""Unsuccessful status code '$statusCode' received by method $method from url: $url"""
    )

case class HttpInvocationError(override val serviceName: String, error: Throwable)
    extends ServiceException(serviceName, error)

case class ParsingResponseError(
  override val serviceName: String,
  url: String,
  method: String,
  parsingError: ParsingFailure)
    extends ServiceException(
      serviceName,
      s"""Error occurred while parsing response from method $method from url $url ${parsingError.message}"""
    )
