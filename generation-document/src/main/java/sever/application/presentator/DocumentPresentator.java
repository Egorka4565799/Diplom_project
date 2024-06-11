package sever.application.presentator;

import sever.application.model.Document;
import sever.application.model.Template;

import java.util.Date;

public record DocumentPresentator(Long Id, String Name, Date dateCreate) {
    public DocumentPresentator(Document document) {
        this(document.getId(), document.getDocumentName(),document.getDateCreate());
    }

}
