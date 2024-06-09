package sever.application.presentator;

import sever.application.model.ReplaceWordMapping;

import java.util.List;

public class TemplateResponse {
    private byte[] documentContent;
    private List<ReplaceWordMappingPresentor> replaceWordMappings;

    private String documentName;

    // Constructors, getters, and setters

    public TemplateResponse() {}



    public TemplateResponse(byte[] documentContent, List<ReplaceWordMappingPresentor> replaceWordMappings, String documentName) {
        this.documentContent = documentContent;
        this.replaceWordMappings = replaceWordMappings;
        this.documentName = documentName;
    }

    public byte[] getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(byte[] documentContent) {
        this.documentContent = documentContent;
    }

    public List<ReplaceWordMappingPresentor> getReplaceWordMappings() {
        return replaceWordMappings;
    }

    public void setReplaceWordMappings(List<ReplaceWordMappingPresentor> replaceWordMappings) {
        this.replaceWordMappings = replaceWordMappings;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}

