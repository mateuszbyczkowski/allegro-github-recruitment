package pl.allegro.recruitment.allegro_github.web.rest.errors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GithubErrorsExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {GithubErrorOccurredException.class,
            GithubApiConnectionTimeoutException.class,
            GithubRepositoryOrUserNotFoundException.class,
            GithubEmptyArrayException.class,
            GithubInternalServerErrorException.class,
            GithubForbiddenUriException.class,
            IOException.class})
    protected String handleApiError(RuntimeException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        return "errorpage";
    }
}