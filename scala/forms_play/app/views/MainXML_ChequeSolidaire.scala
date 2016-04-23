package views

trait MainXML_ChequeSolidaire extends MainXmlWithHead {
  
 override def mainPageHeader(implicit lang: String) =
	<div class="container">
		<div class="row">
			<h3>Bienvenue à Chèque solidaire</h3>
			Participez à Chèque solidaire	et recevez un don en 
			<a href="http://chequesolidaire.org">Chèque solidaire</a> !
			<br/>
		</div>
    Créer votre compte 
    { creationButton( prefix + "Prospect", "Créer") }
  </div>
}
