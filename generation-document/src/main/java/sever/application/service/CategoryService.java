package sever.application.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sever.application.model.Category;
import sever.application.model.Template;
import sever.application.repository.CategoryRepostory;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepostory categoryRepository;

    public Category addCategory(String categoryName) {

        Category category = new Category();

        category.setCategoryName(categoryName);

        categoryRepository.save(category);

        return category;
    }

    public Category updateCategory(String newCategoryName, Long id) {

        Optional<Category> category = categoryRepository.findById(id);

        if(category.isPresent()){
            Category categoryPresent = category.get();
            categoryPresent.setCategoryName(newCategoryName);
            categoryRepository.save(categoryPresent);
            return categoryPresent;
        }

        return null;
    }

    public List<Category> getAll(){
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList;
    }

    public void deleteCategory(Long id){

        categoryRepository.deleteById(id);

    }

}
