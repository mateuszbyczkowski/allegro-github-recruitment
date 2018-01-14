package pl.allegro.recruitment.allegro_github.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GithubErrorsExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "errorpage";

    @ExceptionHandler(value = {GithubErrorOccurredException.class,
            GithubApiConnectionTimeoutException.class,
            GithubRepositoryOrUserNotFoundException.class,
            GithubEmptyArrayException.class,
            GithubInternalServerErrorException.class,
            GithubForbiddenUriException.class,
            IOException.class})

    public String handleError(RuntimeException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        return DEFAULT_ERROR_VIEW;
    }

}