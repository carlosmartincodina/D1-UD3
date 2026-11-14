package com.example.demo;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@Controller
public class Controlador {

	public static void main(String[] args) {
		SpringApplication.run(Controlador.class, args);
	}
	
	@GetMapping("/")
	public String paso1(Model model, HttpSession session) {
		Quiz quiz = new Quiz();
		
		String tipoRadio = (String) session.getAttribute("tipoRadio");
		if (tipoRadio != null) {
			quiz.setTipoRadio(tipoRadio);
		}
		//Añadimos el objeto quiz al modelo
		model.addAttribute("quiz", quiz);
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/paso2")
	public String paso2(@Validated(ValidarTipoRadio.class) @ModelAttribute Quiz quiz, BindingResult bindingResult,
			Model model, HttpSession session) {
		//Comprueba las validaciones de ValidarTipoRadio y si no es correcto vuelve a /paso1
		if (bindingResult.hasErrors()) return "paso1";
		
		@SuppressWarnings("unchecked")
		ArrayList<String> emisoraFavorita = (ArrayList<String>) session.getAttribute("EmisoraFavorita");
		if (emisoraFavorita != null) {
			quiz.setEmisoraFavorita(emisoraFavorita);
		}
		//Asignamos a la sesion el String tipoRadio del objeto quiz
		session.setAttribute("tipoRadio", quiz.getTipoRadio());
		//Devolvemos la plantilla
		return "paso2";
	}
	
	@PostMapping("/paso3")
	public String paso3(@Validated(ValidarEmisoras.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpSession session) {
		//Comprueba las validaciones de ValidarEmisoras y si no es correcto vuelve a /paso2
		if (bindingResult.hasErrors()) return "paso2";
		
		String temaFavorito = (String) session.getAttribute("temaFavorito");
		if (temaFavorito != null) {
			quiz.setTemaFavorito(temaFavorito);
		}
		//Asignamos a la sesion el ArrayList<String> EmisoraFavorita del objeto quiz
		session.setAttribute("EmisoraFavorita", quiz.getEmisoraFavorita());
		//Devolvemos la plantilla
		return "paso3";
	}
	
	@PostMapping("/paso4")
	public String paso4(@Validated(ValidarTema.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpSession session) {
		if (bindingResult.hasErrors()) return "paso3";
		
		String AMoFM = (String) session.getAttribute("AMoFM");
		if (AMoFM != null) {
			quiz.setAMoFM(AMoFM);
		}
		//Asignamos a la sesion el String temaFavorito del objeto quiz
		session.setAttribute("temaFavorito", quiz.getTemaFavorito());
		//Comprueba las validaciones de ValidarTema y si no es correcto vuelve a /paso3

		//Devolvemos la plantilla
		return "paso4";
	}
	
	@PostMapping("/resultado")
	public String resultado(@Validated(ValidarAMoFM.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpSession session) {

		//Comprueba las validaciones de ValidarAMoFM y si no es correcto vuelve a /paso4
		if (bindingResult.hasErrors()) return "paso4";
		//Guardamos el atributo AMoFM
		session.setAttribute("AMoFM", quiz.getAMoFM());
		// Hacemos emisorasFavoritas un String para que tenga un formato mas limpio, en vez de tener [].
		@SuppressWarnings("unchecked") // Ignoramos la advertencia que nos da por el casteo
		String emisorasFavoritasString = String.join(", ", (ArrayList<String>) session.getAttribute("EmisoraFavorita"));
		//Creamos la lista que se va a recorrer en la plantilla
		ArrayList<String> datos = new ArrayList<>();
		//Añadimos los datos
		datos.add("Donde te gusta más escuchar la radio: "+(String) session.getAttribute("tipoRadio"));
		datos.add("Emisoras favoritas: "+emisorasFavoritasString);
		datos.add("Temas favoritos: "+(String) session.getAttribute("temaFavorito"));
		datos.add("¿AM o FM?: "+quiz.getAMoFM());
		model.addAttribute("datos",datos);
		//Devolvemos la plantilla
		return "resultado";
	}
	
	@PostMapping("/volver1")
	public String volver1(Model model, HttpSession session) {
		Quiz quiz = new Quiz();
		String tipoRadio = (String) session.getAttribute("tipoRadio");
		if (tipoRadio != null) {
			quiz.setTipoRadio(tipoRadio);
		}
		model.addAttribute("quiz", quiz);
	    return "paso1";
	}
	
	
	@PostMapping("/volver2")
	public String volver2(Model model, HttpSession session) {
		Quiz quiz = new Quiz();
		@SuppressWarnings("unchecked")
		ArrayList<String> emisoraFavorita = (ArrayList<String>) session.getAttribute("EmisoraFavorita");
		if (emisoraFavorita != null) {
			quiz.setEmisoraFavorita(emisoraFavorita);
		}
		model.addAttribute("quiz", quiz);
	    return "paso2";
	}
	
	
	@PostMapping("/volver3")
	public String volver3(Model model, HttpSession session) {
		Quiz quiz = new Quiz();
		String temaFavorito = (String) session.getAttribute("temaFavorito");
		if (temaFavorito != null) {
			quiz.setTemaFavorito(temaFavorito);
		}
		model.addAttribute("quiz", quiz);
	    return "paso3";
	}
	
	@PostMapping("/volver4")
	public String volver4(Model model, HttpSession session) {
		Quiz quiz = new Quiz();
		String AMoFM = (String) session.getAttribute("AMoFM");
		if (AMoFM != null) {
			quiz.setAMoFM(AMoFM);
		}
		model.addAttribute("quiz", quiz);
	    return "paso4";
	}

	
	@PostMapping("/reiniciar")
	public String reiniciar(HttpSession session) {
		//Invalidamos la sesion
		session.invalidate();
		//Redirigimos a la pantalla principal
		return "redirect:/";
	}

}
