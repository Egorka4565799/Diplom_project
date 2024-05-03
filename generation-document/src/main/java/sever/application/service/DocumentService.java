package sever.application.service;


import sever.application.model.DocumentFormat;

import sever.application.model.Template;
import sever.application.repository.DocumentRepository;
import sever.application.repository.TemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;



import javax.imageio.ImageIO;
import com.aspose.words.Document;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;

@Service
public class DocumentService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private DocumentRepository documentRepository;

    //----------- Выгрузка и поиск ----------------------------------

    public Template findById(Long id) {
        return templateRepository.findById(id).orElse(null);
    }


    public byte[] generateDocumentFromTemplate(Template template, Map<String, String> variables, DocumentFormat format) throws Exception {
        Document doc = new Document(new ByteArrayInputStream(template.getTemplateData()));

        FindReplaceOptions options = new FindReplaceOptions();

        // Заменяем переменные в документе
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            doc.getRange().replace(variable.getKey(), variable.getValue(), options);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();



        if (DocumentFormat.DOCX.equals(format)) {
            // Если нужен DOCX, сохраняем документ в этом формате
            doc.save(outputStream, SaveFormat.DOCX);
        } else if (DocumentFormat.PDF.equals(format)) {
            // Если нужен PDF, сохраняем документ в этом формате
            doc.save(outputStream, SaveFormat.PDF);


        } else {
            throw new IllegalArgumentException("Unsupported format");
        }

        try {
            saveDocumentToArchive(template.getTemplateName(), outputStream.toByteArray());
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(template.getTemplateName() + " - файл с таким именем не удалось внести в архив");
        }
        return outputStream.toByteArray();

    }

    public void saveDocumentToArchive(String fileName, byte[] fileData){

        sever.application.model.Document document = new sever.application.model.Document();

        Date date = new Date();


        document.setDocumentName(fileName);
        document.setDocumentData(fileData);
        document.setDateCreate(date);

        System.out.println(fileName);
        System.out.println(date);

        documentRepository.save(document);

    }
}
