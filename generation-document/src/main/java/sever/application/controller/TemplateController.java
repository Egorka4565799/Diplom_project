package sever.application.controller;

import sever.application.model.ReplaceWordMapping;
import sever.application.model.Template;
import sever.application.model.User;
import sever.application.presentator.ReplaceWordMappingPresentor;
import sever.application.service.TemplateService;
//import sever.application.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/templates")
public class TemplateController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    @Autowired
    private TemplateService templateService;

//    @Autowired
//    private UserService userService;

    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadTemplate(@RequestParam("file") MultipartFile file
    ) throws IOException {

        logger.info("Uploading starting...");
        try{
            if(file.isEmpty()){
                return ResponseEntity.badRequest().body("The file is empty, please attach the file");
            }

            // Логика для сохранения файла в базе данных или файловой системе
            templateService.uploadTemplate(file);
            logger.info("Uploading finish");

            return ResponseEntity.ok("Template uploaded successfully");

        } catch (Exception e){
            logger.error("Error uploading ex:{}",e.getMessage(),e);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Failed to upload file");
        }

    }

    @PostMapping("/upload-file-replace-word")
    public ResponseEntity<String> uploadTemplate(@RequestBody List<ReplaceWordMapping> replaceWord,
                                                 @RequestParam("fileName") String fileName,
                                                 @RequestParam("categoryId") Long categoryId
    ) throws IOException {

        logger.info("Adding replaceWord starting...");
        try{

            // Логика для сохранения файла в базе данных или файловой системе
            templateService.addReplaceWordsToTemplate(fileName,replaceWord,categoryId);
            logger.info("Adding finish");

            return ResponseEntity.ok("ReplaceWord Adding successfully");

        } catch (Exception e){
            logger.error("Error adding ex:{}",e.getMessage(),e);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Failed to adding file");
        }

    }

    @GetMapping("/all")
    public ResponseEntity<Map<Long,String>> getAllTemplate() throws IOException {

        Map<Long, String> templates = new HashMap<>();

        templates=templateService.getAllTemplates();

        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ReplaceWordMappingPresentor>> getAllTemplate(@PathVariable("id") Long id) throws IOException {

        List<ReplaceWordMapping> replaceWords = new ArrayList<>();

        replaceWords=templateService.getReplaceWordsTemplate(id);

        //Сериализация ответа
        //........
        // Преобразуем список ReplaceWordMapping в список ReplaceWordMappingPresentor
        List<ReplaceWordMappingPresentor> presentors = replaceWords.stream()
                .map(ReplaceWordMappingPresentor::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(presentors);
    }


    @PostMapping("/get-replace-words")
    public ResponseEntity<List<String>> getReplaceWord(@RequestParam("file") MultipartFile file
    ) throws IOException {

        List<String> replaceWords = templateService.parsingTemplate(file);
        return ResponseEntity.ok(replaceWords);
    }



//    @PutMapping("/{templateId}/update")
//    public ResponseEntity<String> updateTemplate(
//            @PathVariable Long templateId,
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal User user) throws IOException {
//
//        if(file.isEmpty()){
//            return ResponseEntity.badRequest().body("The file is empty, please attach the file");
//        }
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("The user is not found, please log in");
//        }
//
//        if (user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))) {
//            // Логика для обновления файла шаблона по идентификатору
//            templateService.updateTemplate(templateId, file);
//            return ResponseEntity.ok("Template updated successfully");
//        } else {
//            if(templateService.equalsTemplate(templateId,user)){
//                templateService.updateTemplate(templateId, file);
//                return ResponseEntity.ok("Template updated successfully");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't change a file that doesn't belong to you");
//        }
//
//
//    }
//
//
//    @DeleteMapping("/delete/{templateId}")
//    public ResponseEntity<String> deleteTemplate(
//            @PathVariable Long templateId,
//            @AuthenticationPrincipal User user) {
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("The user is not found, please log in");
//        }
//
//        if (user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))) {
//            templateService.deleteTemplate(templateId);
//            return ResponseEntity.ok("Template deleted successfully");
//        } else {
//            if(templateService.equalsTemplate(templateId,user)){
//                templateService.deleteTemplate(templateId);
//                return ResponseEntity.ok("Template deleted successfully");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user does not have this template");
//        }
//
//
//    }

}
