package sever.application.presentator;

import sever.application.model.Category;
import sever.application.model.ReplaceWordMapping;

public record CategoryPresentator(long id, String categoryName) {
    public CategoryPresentator(Category category) {
        this(category.getId(), category.getCategoryName());
    }
}
