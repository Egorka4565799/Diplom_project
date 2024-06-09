package sever.application.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sever.application.model.Category;
import sever.application.presentator.CategoryPresentator;
import sever.application.presentator.ReplaceWordMappingPresentor;
import sever.application.service.CategoryService;
import sever.application.service.TemplateService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryPresentator>> getAllCategorys() throws IOException {

        List<Category> categories =  categoryService.getAll();

        // Преобразуем список ReplaceWordMapping в список ReplaceWordMappingPresentor
        List<CategoryPresentator> presentors = categories.stream()
                .map(CategoryPresentator::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(presentors);
    }

    @PostMapping()
    public ResponseEntity<String> addCategory(@RequestParam("categoryName") String categoryName
    ) throws IOException {

        categoryService.addCategory(categoryName);

        System.out.println("Successfully add category");
        System.out.println(categoryName);

        return ResponseEntity.ok("Successfully add category");
    }

    @PutMapping("/{id}")//Реализовать
    public ResponseEntity<String> updateGategoryId(){

        ///Реализация

        return ResponseEntity.ok("Successfully update category");
    }

    @GetMapping("/{id}")//Реализовать
    public ResponseEntity<String> getGategoryId(){

        ///Реализация

        return ResponseEntity.ok("Successfully update category");
    }

    @DeleteMapping("/{id}")//Реализовать
    public ResponseEntity<String> deleteGategoryId(){

        ///Реализация

        return ResponseEntity.ok("Successfully update category");
    }
}
