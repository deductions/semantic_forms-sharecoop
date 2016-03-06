package controllers

/** object Application in another file to facilitate redefinition */
object Application extends ApplicationShareCoop

trait ApplicationShareCoop extends ApplicationTrait {
  override val showRDFtype = false
  override val showEditButtons = false
}
