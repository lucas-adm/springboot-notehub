package com.adm.lucas.microblog.application.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class BaseController {

    @Value("${api.host}")
    private String host;

    @GetMapping
    public RedirectView redirectToDocs() {
        return new RedirectView(String.format("%s/docs", host));
    }

    @GetMapping("/docs")
    public RedirectView redirecToSwaggerUI() {
        return new RedirectView(String.format("%s/swagger-ui.html", host));
    }

}