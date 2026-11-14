*D1-UD3 enlazando formularios sin campos hidden*


En este desafío vamos a recuperar el Quiz del desafío 3 de la unidad 2. En ese desafío resolvíamos el problema de no perder los datos entre un formulario y otro con campos hidden. Ahora vamos a resolverlo guardando los datos en el servidor y con cookies.

Vamos a hacerlo de tres formas distintas, cada una en su propio proyecto:

- D1-UD3: con la anotación @SessionAttributes.

- D1-UD3-1: con HttpSession.

- D1-UD3-2: con cookies, guardando nosotros los valores en el navegador.

En los tres proyectos el quiz es el mismo: cuatro pasos (tipo de radio, emisoras favoritas, tema favorito y AM o FM), una pantalla de resultado, un botón para volver atrás y otro para reiniciar el proceso.

## Parte 1: @SessionAttributes (D1-UD3)

Primero vamos a usar la anotación @SessionAttributes, que nos va a facilitar saber quién es el usuario cuando accede a una página, mediante una cookie (JSESSIONID). Esto funciona porque el navegador guarda la cookie y la envía al servidor en las peticiones posteriores, y el servidor recupera los datos de esa sesión.

En la clase controlador, debajo de @Controller, añadimos @SessionAttributes("quiz"). Con esto, el objeto quiz que está en el modelo se guarda en la sesión y se reutiliza entre peticiones, sin tener que volver a asignárselo nosotros en cada método del controlador. Así reducimos código y no necesitamos campos hidden en el HTML.

En el método GET de "/" pasamos por parámetro el objeto Model y con model.addAttribute("quiz", new Quiz()) añadimos un quiz nuevo y vacío al modelo. Es el único sitio donde hace falta, porque ningún parámetro crea el objeto por nosotros, y al estar en @SessionAttributes también queda guardado en la sesión.


En los métodos POST (paso2, paso3, paso4 y resultado) el código es el mismo que hicimos en la práctica D3-UD2, pero ya no hace falta añadir model.addAttribute. El parámetro @ModelAttribute Quiz quiz se recupera de la sesión, y Spring le aplica encima los campos que llegan del formulario.

Por el mismo motivo, podemos quitar los campos hidden de las plantillas. La sesión recuerda los valores por nosotros.

Cuando el usuario quiere ir hacia atrás, el botón Volver envía un POST al paso anterior. Como el quiz sigue en la sesión, se recuerdan los valores. Desde el paso2 el botón Volver envía un POST a "/", para lo que hemos creado un @PostMapping("/") que muestra paso1 con el quiz de la sesión. El GET de "/" crearía un quiz nuevo y borraría lo marcado.


Para reiniciar el proceso y destruir la sesión mapeamos /reiniciar, y en el controlador la función contiene status.setComplete(), que elimina los datos guardados con @SessionAttributes. Finalmente devuelve "redirect:/" para hacer una petición nueva, con la que se genera una cookie.

En el navegador vamos a localhost:8080 y vemos que se ha generado una JSESSIONID en la cabecera de la petición, que nos acompaña durante todo el proceso.


## Parte 2: HttpSession (D1-UD3-1)

Ahora vamos a hacer lo mismo, excepto que vamos a usar HttpSession, lo que nos va a obligar a extender un poco más el código. Ya no usamos @SessionAttributes, así que Spring no guarda el quiz por nosotros: en cada petición el @ModelAttribute crea un Quiz nuevo que solo trae los campos del formulario actual. Los valores de los pasos anteriores los tenemos que guardar y recuperar nosotros en la sesión.

Para ello pasamos por parámetro HttpSession session en cada método del controlador. Con session.setAttribute("nombre", valor) guardamos un dato, y con session.getAttribute("nombre") lo recuperamos con un casteo.

Primero, en el GET de "/" creamos un Quiz nuevo. Si en la sesión ya hay un tipoRadio guardado, se lo asignamos al quiz antes de añadirlo al modelo, para que aparezca marcado.

En cada paso seguimos el mismo patrón. Primero comprobamos las validaciones, y si hay errores devolvemos el formulario con su mensaje. Si es correcto, recuperamos de la sesión el valor del paso siguiente (si ya lo habíamos rellenado antes) y se lo asignamos al quiz, para que aparezca marcado. También guardamos en la sesión el valor que acabamos de recibir. Los valores se guardan con estos nombres: tipoRadio, EmisoraFavorita, temaFavorito y AMoFM.


En el método resultado ya no usamos el objeto quiz para mostrar todos los datos, porque solo contiene el último paso. Leemos de la sesión los tres primeros valores, usamos el AMoFM que acaba de llegar (y que guardamos también en la sesión) y creamos la lista datos que recorre la plantilla con Thymeleaf. Las emisoras se unen con String.join para que salgan separadas por comas y sin corchetes.

Para volver atrás ya no nos sirve enviar un POST al paso anterior, porque ese método valida y guarda datos. Por eso creamos cuatro mapeos nuevos: /volver1, /volver2, /volver3 y /volver4. Cada uno crea un Quiz nuevo, le asigna el valor guardado en la sesión (si existe) y devuelve la plantilla del paso anterior. En las plantillas cambiamos el action del botón Volver para que apunte a su /volver correspondiente.


Para reiniciar el proceso mapeamos /reiniciar, y en el controlador la función contiene session.invalidate(), que destruye la sesión y todos sus datos. Después devuelve "redirect:/", y se crea una sesión nueva.

## Antes de destruir la sesión:

## Después de destruir la sesión:

## Parte 3: Cookies (D1-UD3-2)

Por último vamos a hacerlo con cookies. La estructura es la misma que con HttpSession, pero en lugar de session.setAttribute y session.getAttribute escribimos y leemos cookies. Los datos ya no se quedan en el servidor, sino en el navegador del usuario. Creamos cuatro cookies, una por cada paso: tipoRadio, emisoras, tema y AMoFM.

Para no repetir código creamos dos métodos auxiliares. El método guardarValor crea la cookie con su nombre y valor, y le pone path "/" para que se envíe en todas las páginas, HttpOnly para que no se pueda leer desde JavaScript y una caducidad en segundos. Usamos 1000000 segundos (unos 11 días y medio). El valor se codifica con URLEncoder porque las emisoras tienen espacios y comas que no son válidos en una cookie (por ejemplo en emisoras favoritas). El método leerCookie hace lo contrario: si la cookie no existe devuelve null, y si existe devuelve el valor decodificado con URLDecoder.


Para leer una cookie en el controlador usamos @CookieValue(name="emisoras", required=false) String emisoras en los parámetros del método. Con required=false, si la cookie no existe (porque el usuario todavía no pasó por ese paso) el valor llega como null y no da error. Para escribirla pasamos HttpServletResponse respuesta y llamamos a guardarValor.

El único valor especial es la lista de emisoras, que es un ArrayList. Para guardarla en una cookie la convertimos a un String con String.join(", ", ...), y al leerla la decodificamos y la separamos con split(", ") para volver a crear el ArrayList y asignárselo al quiz.


En el método resultado leemos las cookies tipoRadio, emisoras y tema con @CookieValue y usamos el AMoFM que acaba de llegar, que guardamos también en una cookie. Con esos valores creamos la lista datos para la plantilla.

```
(ValidarAMoFM.
t, Model mode
< > datos =
datos onde te gusta más escuchar la radio
datos. misoras favoritas: "+lee
date emas favoritos: "+tema);
+quiz. getAMoFM())
"resulta
```

Para volver atrás usamos los mismos mapeos /volver1, /volver2, /volver3 y /volver4 que en HttpSession. Cada uno crea un Quiz nuevo, le asigna el valor de su cookie (si existe) y devuelve la plantilla anterior.

Con cookies no hay una sesión que invalidar, así que para reiniciar el proceso mapeamos / reiniciar y recorremos las cuatro cookies sustituyéndolas por un valor vacío con caducidad 0. Con eso el navegador las elimina. Después devolvemos "redirect:/".


```
reiniciar")
einiciar (HttpServletResponse
[] valores
guardarValor (re
```

En el navegador podemos comprobar que las cookies existen y ver su valor y su caducidad. Este es el valor que tienen al llegar a /resultado

Se anula el valor una vez se da a volver a empezar de 0 (/reiniciar):

## Conclusiones

Con este ejercicio he aprendido a utilizar tres formas diferentes de guardar los datos entre formularios en Spring Boot. La que me ha resultado más sencilla ha sido @SessionAttributes, porque Spring gestiona los datos automáticamente.
