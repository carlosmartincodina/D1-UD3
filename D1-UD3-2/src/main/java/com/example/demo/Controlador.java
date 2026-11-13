package com.example.demo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@Controller
public class Controlador {

	public static void main(String[] args) {
		SpringApplication.run(Controlador.class, args);
	}
	
	private void guardarValor(HttpServletResponse respuesta, String nombreVariable, String valorVariable, int caducidad) {
		Cookie cookie = new Cookie(nombreVariable, URLEncoder.encode(valorVariable, StandardCharsets.UTF_8));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(caducidad);
		respuesta.addCookie(cookie);
	}
	
	@GetMapping("/")
	public String paso1(Model model) {
		//Añadimos el objeto quiz al modelo
		model.addAttribute("quiz", new Quiz());
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/")
	public String paso1Post(Model model) {
		//Añadimos el objeto quiz al modelo en el caso de que se haya ido para atrás
		model.addAttribute("quiz", new Quiz());
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/paso2")
	public String paso2(@Validated(ValidarTipoRadio.class) @ModelAttribute Quiz quiz, BindingResult bindingResult,
			Model model, HttpServletResponse respuesta) {
		//Asignamos a la sesion el String tipoRadio del objeto quiz
		guardarValor(respuesta, "tipoRadio", quiz.getTipoRadio(), 900);
		//Comprueba las validaciones de ValidarTipoRadio y si no es correcto vuelve a /paso1
		if (bindingResult.hasErrors()) return "paso1";
		//Devolvemos la plantilla
		return "paso2";
	}
	
	@PostMapping("/paso3")
	public String paso3(@Validated(ValidarEmisoras.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpServletResponse respuesta) {
		//Asignamos a la sesion el ArrayList<String> EmisoraFavorita del objeto quiz
		guardarValor(respuesta, "emisoras", String.join(", ", quiz.getEmisoraFavorita()), 900);
		//Comprueba las validaciones de ValidarEmisoras y si no es correcto vuelve a /paso2
		if (bindingResult.hasErrors()) return "paso2";
		//Devolvemos la plantilla
		return "paso3";
	}
	
	@PostMapping("/paso4")
	public String paso4(@Validated(ValidarTema.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpServletResponse respuesta) {

		//Asignamos a la sesion el String temaFavorito del objeto quiz
		guardarValor(respuesta, "tema", quiz.getTemaFavorito(), 900);
		//Comprueba las validaciones de ValidarTema y si no es correcto vuelve a /paso3
		if (bindingResult.hasErrors()) return "paso3";
		//Devolvemos la plantilla
		return "paso4";
	}
	
	@PostMapping("/resultado")
	public String resultado(@Validated(ValidarAMoFM.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model,
			@CookieValue("tipoRadio") String tipoRadio,
			@CookieValue("emisoras") String emisoras,
			@CookieValue("tema") String tema) {

		//Aqui no hace falta añadir el String AMoFM ya que lo obtendrá directamente de paso4.
		//Comprueba las validaciones de ValidarAMoFM y si no es correcto vuelve a /paso4
		if (bindingResult.hasErrors()) return "paso4";
		//Creamos la lista que se va a recorrer en la plantilla
		ArrayList<String> datos = new ArrayList<>();
		//Añadimos los datos
		datos.add("Donde te gusta más escuchar la radio: "+tipoRadio);
		datos.add("Emisoras favoritas: "+emisoras);
		datos.add("Temas favoritos: "+tema);
		datos.add("¿AM o FM?: "+quiz.getAMoFM());
		model.addAttribute("datos",datos);
		//Devolvemos la plantilla
		return "resultado";
	}
	
	@PostMapping("/reiniciar")
	public String reiniciar(HttpSession session, HttpServletResponse respuesta) {
		//Reseteamos el valor de los atributos de la cookie
		String[] valores = {"tipoRadio", "emisoras", "tema"};
		for (String valor : valores) {
			guardarValor(respuesta,valor,"",0);
		}
		//Redirigimos a la pantalla principal
		return "redirect:/";
	}

}
