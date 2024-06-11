package sever.application.controller;


import sever.application.model.DocumentFormat;
import sever.application.model.ReplaceWordMapping;
import sever.application.model.Template;
import sever.application.model.User;
import sever.application.presentator.DocumentPresentator;
import sever.application.presentator.ReplaceWordMappingPresentor;
import sever.application.presentator.TemplatePresentator;
import sever.application.presentator.TemplateResponse;
import sever.application.repository.TemplateRepository;
import sever.application.service.DocumentService;
import sever.application.service.TemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private TemplateService templateService;

    public static class GenerateDocumentRequest {
        private DocumentFormat format;
        private Map<String, String> variables;

        public GenerateDocumentRequest() {
        }

        public GenerateDocumentRequest(DocumentFormat format, Map<String, String> variables) {
            this.format = format;
            this.variables = variables;
        }

        public DocumentFormat getFormat() {
            return format;
        }

        public void setFormat(DocumentFormat format) {
            this.format = format;
        }

        public Map<String, String> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, String> variables) {
            this.variables = variables;
        }
    }

    @GetMapping("/templates/{id}")
    public ResponseEntity<?> getTempalte(@PathVariable("id") Long id) throws IOException{

        try {
            Template template = documentService.findById(id);

            if (template == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template not found");
            }

            byte[] documentContent = template.getTemplateData();
            String documentName = template.getTemplateName();

            List<ReplaceWordMapping> replaceWords = new ArrayList<>();

            replaceWords=templateService.getReplaceWordsTemplate(id);

            System.out.println("document = "+documentContent);
            //Сериализация ответа
            // Преобразуем список ReplaceWordMapping в список ReplaceWordMappingPresentor
            List<ReplaceWordMappingPresentor> replaceWordMappings = replaceWords.stream()
                    .map(ReplaceWordMappingPresentor::new)
                    .collect(Collectors.toList());
            TemplateResponse response = new TemplateResponse(documentContent, replaceWordMappings,documentName);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve document");
        }
    }

    @PostMapping("/templates/{id}/generate")
    public ResponseEntity<?> generateDocument(@PathVariable("id") Long id,
                                              @RequestBody GenerateDocumentRequest request
                                              ) throws IOException {

        try {

            //Map <String,String> variables = new HashMap<>();
            //variables.put("{@Vvedenie}","Valera");
            System.out.println(request.format);
            System.out.println(request.variables);

            Template template = documentService.findById(id);

            if (template == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template not found");
            }

            String originalFileName = template.getTemplateName();
            int lastDotIndex = originalFileName.lastIndexOf(".");
            String fileNameWithoutExtension = originalFileName.substring(0, lastDotIndex);
            System.out.println(fileNameWithoutExtension);
            HttpHeaders headers = new HttpHeaders();

            if (DocumentFormat.DOCX.equals(request.format)) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileNameWithoutExtension+".docx");
            } else if (DocumentFormat.PDF.equals(request.format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", fileNameWithoutExtension+".pdf");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported format");
            }


            byte[] documentBytes = documentService.generateDocumentFromTemplate(template, request.variables, request.format);


            return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download document");
        }

    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentPresentator>> getAllDocument() throws IOException {

        List<DocumentPresentator> templates = documentService.getAllDocument();

        return ResponseEntity.ok(templates);
    }

}
