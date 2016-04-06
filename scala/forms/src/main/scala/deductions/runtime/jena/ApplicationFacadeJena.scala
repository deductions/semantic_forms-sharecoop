package deductions.runtime.jena

import scala.xml.NodeSeq

import org.w3.banana.jena.JenaModule

import deductions.runtime.dataset.RDFStoreLocalUserManagement
import deductions.runtime.services.ApplicationFacade
import deductions.runtime.services.ApplicationFacadeImpl
import deductions.runtime.services.ApplicationFacadeInterface
import deductions.runtime.services.Configuration
import deductions.runtime.services.ConfigurationCopy

/**
 * ApplicationFacade for Jena,
 * does not expose Jena, just ApplicationFacadeInterface
 */
trait ApplicationFacadeJena
    extends ApplicationFacadeInterface
    with ApplicationFacade[ImplementationSettings.Rdf, ImplementationSettings.DATASET]
    with RDFStoreLocalJenaProvider
    with Configuration {
  val conf: Configuration = this
  override val impl = try {
    /**
     * NOTES:
     * - mandatory that JenaModule is first; otherwise ops may be null
     * - mandatory that RDFStoreLocalJena1Provider is before ApplicationFacadeImpl;
     *   otherwise allNamedGraph may be null
     */
    abstract class ApplicationFacadeImplJena extends JenaModule
      with RDFStoreLocalJenaProvider
      with ApplicationFacadeImpl[ImplementationSettings.Rdf, ImplementationSettings.DATASET]
      with RDFStoreLocalUserManagement[ImplementationSettings.Rdf, ImplementationSettings.DATASET]

    new ApplicationFacadeImplJena with ConfigurationCopy {
      lazy val original = conf
      override def htmlForm(uri0: String, blankNode: String = "",
        editable: Boolean = false,
        lang: String = "en", formuri: String = "",
        graphURI: String = ""): NodeSeq = {
        println(s""">> ApplicationFacadeImplJena 
                max  Memory  ${Runtime.getRuntime.maxMemory()}
                totalMemory  ${Runtime.getRuntime.totalMemory()}""")
        val name = "TDB/journal.jrnl"
        println(s"$name  : ${new java.io.File(name).length()} bytes")
        super.htmlForm(uri0: String, blankNode,
          editable, lang: String, graphURI = graphURI)
      }
    }
  } catch {
    case t: Throwable =>
      t.printStackTrace()
      throw t
  }
}
