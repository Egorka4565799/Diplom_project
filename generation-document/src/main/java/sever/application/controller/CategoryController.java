package sever.application.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    @Autowired
    private CategoryService categoryService;

    @GetMapping()//Список всех категорий
    public ResponseEntity<List<CategoryPresentator>> getAllCategories() throws IOException {

        List<Category> categories =  categoryService.getAll();

        // Преобразуем список ReplaceWordMapping в список ReplaceWordMappingPresentor
        List<CategoryPresentator> presentors = categories.stream()
                .map(CategoryPresentator::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(presentors);
    }

    @PostMapping()//Создание новой категории
    public ResponseEntity<?> addCategory(@RequestParam("categoryName") String categoryName
    ) throws IOException {

        // Декодируем строку
        String decodedCategoryName = URLDecoder.decode(categoryName, "UTF-8");

        System.out.println("Новая категория: " + decodedCategoryName);

        Category category = categoryService.addCategory(decodedCategoryName);

        CategoryPresentator categoryPresentator= new CategoryPresentator(category);
        System.out.println("Successfully add category");
        //System.out.println(categoryName);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String categoriesJson = objectMapper.writeValueAsString(categoryPresentator);
        return ResponseEntity.ok(categoryPresentator);
    }

    @PutMapping("/{id}")//Обновление категории
    public ResponseEntity<?> updateCategoryId(@PathVariable("id") Long categoryId,
                                              @RequestParam("categoryName") String categoryName
    ) throws IOException {

        // Декодируем строку
        String decodedCategoryName = URLDecoder.decode(categoryName, "UTF-8");

        System.out.println("Обновленная категория: " + decodedCategoryName);

        Category category = categoryService.updateCategory(decodedCategoryName, categoryId);

        CategoryPresentator categoryPresentator= new CategoryPresentator(category);
        System.out.println("Successfully update category");

        return ResponseEntity.ok(categoryPresentator);

    }


    @DeleteMapping("/{id}")//Удаление категории
    public ResponseEntity<String> deleteCategoryId(@PathVariable("id") Long categoryId){

        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }

}
