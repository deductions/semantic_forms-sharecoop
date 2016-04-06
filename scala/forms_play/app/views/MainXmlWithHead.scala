package views
 
import controllers._
import deductions.runtime.html.MainXml

trait MainXmlWithHead extends MainXml {

  val prefix = "urn:ShareCoop/vocab#"

  override def mainPageHeader(implicit lang: String) =
	<div class="container">
		<div class="row">
			<h3>Bienvenue à Share.Coop</h3>
			Participez à Share.Coop	et recevez un don en 
			<a href="http://chequesolidaire.org">Chèque solidaire</a> !
			<br/>
			Jeu gratuit et sans engagement
			<br/>
			<a href="http://share.coop">Share.Coop</a>
		</div>
    Créer votre compte Share.Coop
    { creationButton( prefix + "Prospect", "Créer") }
  </div>

  /** bas de page **/
  override def pageBottom = {
    Seq( <div><a href="???">Contact</a></div> , super.linkToToolsPage )   
  }

  /** HTML head */
  override def head(title: String = "")(implicit lang: String = "en") = {
    <head>
      <title>ShareCoop</title>

      <meta http-equiv="Content-type" content="text/html; charset=UTF-8"></meta>
      <link rel="shortcut icon" type="image/png" href={ routes.Assets.at("images/favicon.png").url }/>

      { javascriptCSSImports }
      
      <style type="text/css">
        .resize {{ resize: both; width: 100%; height: 100%; }}
        .overflow {{ overflow: auto; width: 100%; height: 100%; }}
      </style>
    </head>
  }
}
