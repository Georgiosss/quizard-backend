package com.example.demo.model.dto.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(
        value = {"error", "cause", "stackTrace", "total", "data", "localizedMessage", "message", "suppressed"},
        ignoreUnknown = true
)
public class ApiException extends RuntimeException {
    protected String keyword;
    protected Map<String, String> description = new HashMap();
    protected Exception error;

    public ApiException(String keyword) {
        super(keyword);
        this.keyword = keyword;
    }

    public ApiException withDescription(String description) {
        this.description.put("ge", description);
        return this;
    }

    public ApiException withDescription(Map<String, String> description) {
        this.description = description;
        return this;
    }

    public ApiException withDescription(String language, String description) {
        this.description.put(language.toLowerCase(), description);
        return this;
    }

    public ApiException withError(Exception error) {
        this.error = error;
        return this;
    }

    public void printStackTrace() {
        if (this.error != null) {
            this.error.printStackTrace();
        } else {
            super.printStackTrace();
        }

    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getDescription() {
        return (String)this.description.get("ge");
    }

    public String getDescription(String language) {
        return (String)this.description.get(language.toLowerCase());
    }

    public Map<String, String> getDescriptionInAllLanguages() {
        return this.description;
    }

    public Exception getError() {
        return this.error;
    }
}
