package com.example.demo;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@SpringBootApplication
@Controller
@SessionAttributes("quiz")
public class Controlador {

	public static void main(String[] args) {
		SpringApplication.run(Controlador.class, args);
	}
	
	@GetMapping("/")
	public String paso1(Model model) {
		//Creamos el objeto quiz y lo añadimos al modelo
		model.addAttribute("quiz", new Quiz());
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/")
	public String paso1Post(Model model, Quiz quiz) {
		//Creamos el objeto quiz y lo añadimos al modelo
		model.addAttribute("quiz", quiz);
		//Devolvemos la plantilla
		return "paso1";
	}
	
	@PostMapping("/paso2")
	public String paso2(@Validated(ValidarTipoRadio.class) @ModelAttribute Quiz quiz, BindingResult bindingResult,
			Model model) {
		//Comprueba las validaciones y si no es correcto vuelve a /paso1
		if (bindingResult.hasErrors()) return "paso1";
		//Devolvemos la plantilla
		return "paso2";
	}
	
	@PostMapping("/paso3")
	public String paso3(@Validated({ValidarEmisoras.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		//Comprueba las validaciones y si no es correcto vuelve a /paso2
		if (bindingResult.hasErrors()) return "paso2";
		//Devolvemos la plantilla
		return "paso3";
	}
	
	@PostMapping("/paso4")
	public String paso4(@Validated({ValidarTema.class,ValidarEmisoras.class,ValidarTipoRadio.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		//Comprueba las validaciones y si no es correcto vuelve a /paso3
		if (bindingResult.hasErrors()) return "paso3";
		//Devolvemos la plantilla
		return "paso4";
	}
	
	@PostMapping("/resultado")
	public String resultado(@Validated({ValidarTema.class,ValidarEmisoras.class,ValidarTipoRadio.class, ValidarAMoFM.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		//Comprueba las validaciones y si no es correcto vuelve a /paso3
		if (bindingResult.hasErrors()) return "paso4";
		//Ponemos bonita la lista del array de emisoras favoritas, separadas por comas sin []
		String emisorasFavoritas = String.join(", ", quiz.getEmisoraFavorita());
		//Creamos la lista que se va a recorrer en el modelo
		ArrayList<String> datos = new ArrayList<>();
		//Añadimos los datos
		datos.add("Donde te gusta más escuchar la radio: "+quiz.getTipoRadio());
		datos.add("Emisoras favoritas: "+emisorasFavoritas);
		datos.add("Temas favoritos: "+quiz.getTemaFavorito());
		datos.add("¿AM o FM?: "+quiz.getAMoFM());
		//Añadimos los datos a la plantilla
		model.addAttribute("datos",datos);
		//Devolvemos la plantilla
		return "resultado";
	}
	
	@PostMapping("/reiniciar")
	public String reiniciar(SessionStatus status) {
		//Reiniciamos el proceso
		status.setComplete();
		//Redirigimos a la pantalla principal
		return "redirect:/";
	}

}
