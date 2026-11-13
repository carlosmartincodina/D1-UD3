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
		model.addAttribute("quiz", new Quiz());
		return "paso1";
	}
	
	@PostMapping("/paso2")
	public String paso2(@Validated(ValidarTipoRadio.class) @ModelAttribute Quiz quiz, BindingResult bindingResult,
			Model model) {
		//Comprueba las validaciones y si no es correcto vuelve a /paso1
		// o si se accede directamente a /paso2 sin sesión, vuelve a /paso1 también
		if (bindingResult.hasErrors()) return "paso1";
		return "paso2";
	}
	
	@PostMapping("/paso3")
	public String paso3(@Validated({ValidarEmisoras.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		//Añadimos al modelo el objeto quiz
		if (bindingResult.hasErrors()) return "paso2";
		
		return "paso3";
	}
	
	@PostMapping("/paso4")
	public String paso4(@Validated({ValidarTema.class,ValidarEmisoras.class,ValidarTipoRadio.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) return "paso3";
		return "paso4";
	}
	
	@PostMapping("/resultado")
	public String resultado(@Validated({ValidarTema.class,ValidarEmisoras.class,ValidarTipoRadio.class, ValidarAMoFM.class}) @ModelAttribute Quiz quiz,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) return "paso4";
		ArrayList<String> datos = new ArrayList<>();
		datos.add("Donde te gusta más escuchar la radio: "+quiz.getTipoRadio());
		datos.add("Emisoras favoritas: "+quiz.getEmisoraFavorita());
		datos.add("Temas favoritos: "+quiz.getTemaFavorito());
		datos.add("¿AM o FM?: "+quiz.getAMoFM());
		model.addAttribute("datos",datos);
		return "resultado";
	}
	
	@PostMapping("/reiniciar")
	public String reiniciar(@ModelAttribute Quiz quiz, SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}

}
