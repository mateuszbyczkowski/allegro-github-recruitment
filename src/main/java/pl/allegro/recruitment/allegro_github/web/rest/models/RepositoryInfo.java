package pl.allegro.recruitment.allegro_github.web.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryInfo {

    @JsonProperty(value = "full_name")
    private String repositoryName;
    @JsonProperty(value = "updated_at")
    private Date updatedAt;

}
