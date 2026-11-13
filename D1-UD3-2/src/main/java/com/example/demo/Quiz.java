package com.example.demo;

import java.util.ArrayList;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Quiz {

	@NotNull(message="No puedes dejar el campo vacío.", groups=ValidarTipoRadio.class)
	@Pattern(regexp="Analogico|Digital", message = "Opción inválida", groups=ValidarTipoRadio.class)
	private String tipoRadio;
	@NotEmpty(message="Tienes que seleccionar un campo.", groups=ValidarTema.class)
	@Pattern(regexp="Musica|Noticias|Deportes|Humor|Entrevistas", message = "Opción inválida", groups=ValidarTema.class)
	private String temaFavorito;
	@NotEmpty(message="No puedes dejarlo vacío.", groups=ValidarEmisoras.class)
	private ArrayList<@Pattern(regexp="Megastar|Los 40|Cadena 100|Radio Maria|Kiss FM|RNE|Cadena SER", message = "Opción inválida", groups=ValidarEmisoras.class) String> emisoraFavorita;
	@NotEmpty(message="No se puede dejar vacío", groups=ValidarAMoFM.class)
	@Pattern(regexp="AM|FM", groups=ValidarAMoFM.class)
	private String AMoFM;
	
	public String getTipoRadio() {
		return tipoRadio;
	}
	public void setTipoRadio(String tipoRadio) {
		this.tipoRadio = tipoRadio;
	}
	public String getTemaFavorito() {
		return temaFavorito;
	}
	public void setTemaFavorito(String temaFavorito) {
		this.temaFavorito = temaFavorito;
	}
	public ArrayList<String> getEmisoraFavorita() {
		return this.emisoraFavorita;
	}
	public void setEmisoraFavorita(ArrayList<String> emisoraFavorita) {
		this.emisoraFavorita = emisoraFavorita;
	}
	public String getAMoFM() {
		return AMoFM;
	}
	public void setAMoFM(String aMoFM) {
		AMoFM = aMoFM;
	}
	
	
	
}
