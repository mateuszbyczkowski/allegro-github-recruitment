package pl.allegro.recruitment.allegro_github.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GithubErrorsExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "errorpage";

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestErrorException(Model model) {

        model.addAttribute("errorMessage", "Request incorrect, check request uri and try again.");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenErrorException(Model model) {

        model.addAttribute("errorMessage", "Requesting content is forbidden!");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundErrorException(Model model) {

        model.addAttribute("errorMessage", "Repository or User not found, uri may be incorrect.");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public String handleRequestTimeoutErrorException(Model model) {

        model.addAttribute("errorMessage", "Connection timeout.");
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerErrorException(Model model) {

        model.addAttribute("errorMessage", "Internal server error! Please try again later.");
        return DEFAULT_ERROR_VIEW;
    }
}