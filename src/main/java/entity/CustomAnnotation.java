package entity;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonIgnoreProperties(ignoreUnknown = true)
    //@JsonPropertyOrder({"PLASTIC_ID"})
    public @interface CustomAnnotation {}