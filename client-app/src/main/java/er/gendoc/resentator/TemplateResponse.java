package er.gendoc.resentator;

import java.util.List;

public class TemplateResponse {
    private byte[] documentContent;
    private List<ReplaceWordMapping> replaceWordMappings;

    private String documentName;

    // Constructors, getters, and setters

    public TemplateResponse() {}



    public TemplateResponse(byte[] documentContent, List<ReplaceWordMapping> replaceWordMappings, String documentName) {
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

    public List<ReplaceWordMapping> getReplaceWordMappings() {
        return replaceWordMappings;
    }

    public void setReplaceWordMappings(List<ReplaceWordMapping> replaceWordMappings) {
        this.replaceWordMappings = replaceWordMappings;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
