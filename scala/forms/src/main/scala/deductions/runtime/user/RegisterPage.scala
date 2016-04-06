package deductions.runtime.user

import scala.xml.Text
import scala.util.Success
import scala.util.Failure
import org.w3.banana.RDF
import deductions.runtime.abstract_syntax.PreferredLanguageLiteral
import deductions.runtime.abstract_syntax.InstanceLabelsInferenceMemory
import deductions.runtime.html.TableViewModule
import deductions.runtime.html.CreationFormAlgo
import deductions.runtime.services.Configuration
import deductions.runtime.services.StringSearchSPARQL
import deductions.runtime.utils.I18NMessages
import scala.xml.NodeSeq

/** Register HTML Page */
trait RegisterPage[Rdf <: RDF, DATASET]
    extends StringSearchSPARQL[Rdf, DATASET]
    with InstanceLabelsInferenceMemory[Rdf, DATASET]
    with PreferredLanguageLiteral[Rdf]
    with TableViewModule[Rdf, DATASET]
    with CreationFormAlgo[Rdf, DATASET]
    with Configuration {

  import ops._

  /** display User information in pages */
  def displayUser(userid: String, pageURI: String, pageLabel: String,
      lang: String = "en"): NodeSeq = {
    <div class="userInfo"> {
      if (needLogin) {
        if (userid != "") {
          val userLabel = rdfStore.rw( dataset, {
            instanceLabel(URI(userid), allNamedGraph, lang)
            // TODO link to User profile
          })
          val displayUserLabel = userLabel match {
            case Success(lab) => s"${I18NMessages.get("User", lang)}: $lab"
            case Failure(e) => s"No label for user (!?): $e"
          }
          <div>{displayUserLabel} - <a href="/logout">logout</a></div>
        } else {
          <div>
            Anonyme
        	-{
              if (pageURI != "") {
                <a href="/claimid?uri={URLEncode.encode(pageURI)}" title="il ne reste plus qu'à saisir un mot de passe">
                  Revendiquer l'identité de cette page:{ pageLabel }
                </a>
                Text("- ou -")
              }
            }
            <a href="/register" title="Nouveau compte à partir de zéro">
              Nouveau compte
            </a>
            - ou 
        	-<a href="/searchid" title="Peut-être votre identité est déjà enregistrée ici?">
            Chercher mon identité sur ce site
          </a>
          </div>
        }
      } else Text(I18NMessages.get("All_rights", lang))
    } </div>
  }

  /**
   * action="claimid"
   *  claim identity is made up of foaf:Person edition + entering password
   */
  def claimIdentityAction(uri: String) = {
    val rawForm = htmlFormElem(uri,
      actionURI = "/saveuser",
      actionURI2 = "/saveuser")
    // TODO put the password after name field inside the form
    <p>
      { passWordField }
      { rawForm }
    </p>
  }

  /**
   * action="register"
   *  register from scratch;
   *  new account is made up of foaf:Person creation + entering password
   */
  def registerAction(uri: String)
//  (implicit graph: Rdf#Graph)
  = {
	  implicit val graph: Rdf#Graph = allNamedGraph
    val rawForm = create(uri //      , actionURI = "/saveuser", // TODO
    //      actionURI2 = "/saveuser"
    )
    // TODO put the password after name field inside the form
    <p>
      { passWordField }
      { rawForm }
    </p>
  }

  private val passWordField =
    <div>
      <label>Entrer un mot de passe</label>
      <input type="password" name="password"></input>
    </div>

  /**
   * action="searchid"
   *  search entered Name in TDB
   */
  def searchEnteredNameAction(enteredName: String) = {
    // then click on one link to claim the identity
    searchString(enteredName)
  }

  /* val registerPage =
    <p>
      <h1>Créer un compte</h1>
      <form action="searchid">
        Entrer votre nom ou votre courriel
        <input type="text">
        </input>
      </form>
    </p> */
}