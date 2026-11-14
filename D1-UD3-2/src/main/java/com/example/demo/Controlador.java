package com.example.demo;

import java.net.URLDecoder;
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
import jakarta.servlet.http.HttpServletRequest;
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
	
	//Aqui decodificamos la cookie para que no aparezca ningun valor raro (solo para el arraylist)
	private String leerCookie(String valor) {
		//Si el valor es null, devuelve null, si no, devuelve el string decodificado.
	    if (valor == null) {
	    	return null;
	    } else {
	    	return URLDecoder.decode(valor,StandardCharsets.UTF_8);
	    }
	}
	
	@GetMapping("/")
	public String paso1(Model model) {
		//Añadimos el objeto quiz al modelo
		model.addAttribute("quiz", new Quiz());
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/paso2")
	public String paso2(@Validated(ValidarTipoRadio.class) @ModelAttribute Quiz quiz, BindingResult bindingResult,
			Model model, HttpServletResponse respuesta, @CookieValue(name="emisoras",required=false) String emisoras) {
		
		//Comprueba las validaciones de ValidarTipoRadio y si no es correcto vuelve a /paso1
		if (bindingResult.hasErrors()) return "paso1";
		
		if (emisoras != null) {
			emisoras = leerCookie(emisoras);
			ArrayList<String> emisoraFavorita = new ArrayList<>();
			String[] arrayEmisoras = emisoras.split(", ");
			for (String elemento : arrayEmisoras) {
				emisoraFavorita.add(elemento);
			}
			quiz.setEmisoraFavorita(emisoraFavorita);
		}
		guardarValor(respuesta,"tipoRadio",quiz.getTipoRadio(),1000000);

		//Devolvemos la plantilla
		return "paso2";
	}
	
	@PostMapping("/paso3")
	public String paso3(@Validated(ValidarEmisoras.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpServletResponse respuesta,
			@CookieValue(name="tema",required=false) String tema) {
		//Comprueba las validaciones de ValidarEmisoras y si no es correcto vuelve a /paso2
		if (bindingResult.hasErrors()) return "paso2";
		
		if (tema != null) {
			quiz.setTemaFavorito(tema);
		}
		
		//Asignamos a la sesion el ArrayList<String> EmisoraFavorita del objeto quiz
		guardarValor(respuesta, "emisoras", String.join(", ", quiz.getEmisoraFavorita()), 1000000);
		//Devolvemos la plantilla
		return "paso3";
	}
	
	@PostMapping("/paso4")
	public String paso4(@Validated(ValidarTema.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpServletResponse respuesta,
			@CookieValue(name="AMoFM", required=false) String AMoFM) {

		if (bindingResult.hasErrors()) return "paso3";
		
		
		if (AMoFM != null) {
			quiz.setAMoFM(AMoFM);
		}
		
		//Asignamos a la sesion el String temaFavorito del objeto quiz
		guardarValor(respuesta, "tema", quiz.getTemaFavorito(), 1000000);
		//Comprueba las validaciones de ValidarTema y si no es correcto vuelve a /paso3
		//Devolvemos la plantilla
		return "paso4";
	}
	
	@PostMapping("/resultado")
	public String resultado(@Validated(ValidarAMoFM.class) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model, HttpServletResponse respuesta,
			@CookieValue("tipoRadio") String tipoRadio,
			@CookieValue("emisoras") String emisoras,
			@CookieValue("tema") String tema) {
		
		//Comprueba las validaciones de ValidarAMoFM y si no es correcto vuelve a /paso4
		if (bindingResult.hasErrors()) return "paso4";

		guardarValor(respuesta,"AMoFM",quiz.getAMoFM(), 1000000);
		//Creamos la lista que se va a recorrer en la plantilla
		ArrayList<String> datos = new ArrayList<>();
		//Añadimos los datos
		datos.add("Donde te gusta más escuchar la radio: "+tipoRadio);
		datos.add("Emisoras favoritas: "+leerCookie(emisoras));
		datos.add("Temas favoritos: "+tema);
		datos.add("¿AM o FM?: "+quiz.getAMoFM());
		model.addAttribute("datos",datos);
		//Devolvemos la plantilla
		return "resultado";
	}
	
	@PostMapping("/volver1")
	public String volver1(Model model, @CookieValue(name="tipoRadio", required=false) String tipoRadio) {
		Quiz quiz = new Quiz();
		if (tipoRadio != null) {
			quiz.setTipoRadio(tipoRadio);
		}
		model.addAttribute("quiz", quiz);
	    return "paso1";
	}

	@PostMapping("/volver2")
	public String volver2(Model model, @CookieValue(name="emisoras", required=false) String emisoras) {
		Quiz quiz = new Quiz();
		emisoras = leerCookie(emisoras);
		if (emisoras != null) {
			ArrayList<String> emisoraFavorita = new ArrayList<>();
			String[] arrayEmisoras = emisoras.split(", ");
			for (String elemento : arrayEmisoras) {
				emisoraFavorita.add(elemento);
			}
			quiz.setEmisoraFavorita(emisoraFavorita);
		}
		model.addAttribute("quiz", quiz);
	    return "paso2";
	}

	@PostMapping("/volver3")
	public String volver3(Model model, @CookieValue(name="tema", required=false) String tema) {
		Quiz quiz = new Quiz();
		if (tema != null) {
			quiz.setTemaFavorito(tema);
		}
		model.addAttribute("quiz", quiz);
	    return "paso3";
	}

	@PostMapping("/volver4")
	public String volver4(Model model, @CookieValue(name="AMoFM", required=false) String AMoFM) {
		Quiz quiz = new Quiz();
		if (AMoFM != null) {
			quiz.setAMoFM(AMoFM);
		}
	    model.addAttribute("quiz", quiz);
	    return "paso4";
	}
	
	@PostMapping("/reiniciar")
	public String reiniciar(HttpServletResponse respuesta) {
		//Reseteamos el valor de los atributos de la cookie
		String[] valores = {"tipoRadio", "emisoras", "tema", "AMoFM"};
		for (String valor : valores) {
			guardarValor(respuesta,valor,"",0);
		}
		//Redirigimos a la pantalla principal
		return "redirect:/";
	}

}
