package sever.application.presentator;

import sever.application.model.ReplaceWordMapping;
import sever.application.model.Template;

public record TemplatePresentator(Long templateId, String templateName, Long categoryId) {
    public TemplatePresentator(Template template) {
        this(template.getId(), template.getTemplateName(),template.getCategory().getId());
    }

}
