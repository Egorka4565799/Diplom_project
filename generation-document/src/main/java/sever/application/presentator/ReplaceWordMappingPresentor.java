package sever.application.presentator;

import sever.application.model.ReplaceWordMapping;

import java.util.List;

public record ReplaceWordMappingPresentor(String key, List<String> value) {

    // Создаем конструктор, принимающий объект ReplaceWordMapping
    public ReplaceWordMappingPresentor(ReplaceWordMapping replaceWordMapping) {
        this(replaceWordMapping.getKey(), replaceWordMapping.getValue());
    }
}
