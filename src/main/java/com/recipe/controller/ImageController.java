package com.recipe.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/img")
public class ImageController {

	 @GetMapping("/{folder}/{filename:.+}")
	    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) throws MalformedURLException {

	        Path file = Paths.get("C:/yummy/" + folder + "/" + filename);
	        Resource resource = new UrlResource(file.toUri());


	        return ResponseEntity.ok().body(resource);
	    }
}
