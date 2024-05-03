package sever.application.service;


import sever.application.model.Category;
import sever.application.model.ReplaceWordMapping;
import sever.application.model.Template;
import sever.application.model.User;
import sever.application.repository.CategoryRepostory;
import sever.application.repository.TemplateRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CategoryRepostory categoryRepostory;

    //----------- Загрузка, обновление и удаление ----------------------------------

    @Transactional
    public void uploadTemplate(MultipartFile file) throws IOException {
        logger.info("Start uploading: {}",file.getOriginalFilename());

        try {
            Template template = new Template();

            // Сохраняем имя файла в сущности template
            template.setTemplateName(file.getOriginalFilename());
            // Сохраняем файл в виде Large Object (LOB) в базе данных
            template.setTemplateData(file.getBytes());

            //user.addTemplate(template);

            templateRepository.save(template);
            logger.info("Uploading successfully: {}",template.getTemplateName());
        }
        catch(Exception e){
            logger.error("Error upload! :{}", e.getMessage());
        }
    }

    public void addReplaceWordsToTemplate(String fileName, List<ReplaceWordMapping> replaceWords, Long categoryId) {
        // Находим шаблон по имени файла
        Template template = templateRepository.findByTemplateName(fileName);
        if (template == null) {
            // Если шаблон не найден, можно выбросить исключение или выполнить другие действия
            throw new IllegalArgumentException("Template with filename " + fileName + " not found");
        }
        Optional<Category> category = categoryRepostory.findById(categoryId);


        template.setCategories(category.get());
        // Добавляем слова для замены к найденному шаблону
        List<ReplaceWordMapping> existingReplaceWords = template.getReplaceWordMappings();
        existingReplaceWords.addAll(replaceWords);
        template.setReplaceWordMappings(existingReplaceWords);
        // Сохраняем обновленный шаблон в базу данных
        templateRepository.save(template);
    }

    public void deleteTemplate(Long templateId) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isPresent()) {
//            Template template = optionalTemplate.get();
//            // Получите пользователя, связанного с этим шаблоном
//            User user = template.getUser();
//            user.removeTemplate(template);

            // Затем удалите сам шаблон
            templateRepository.deleteById(templateId);
        }

    }

    public Map<Long,String> getAllTemplates(){

        List<Template> templates = templateRepository.findAll();

        return templates.stream()
                .collect(Collectors.toMap(Template::getId, Template::getTemplateName));
    }

    public List<ReplaceWordMapping> getReplaceWordsTemplate(Long templateId){
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();

            //return new ArrayList<>();
            return template.getReplaceWordMappings();
        }
        return null;
    }

    public List<String> parsingTemplate(MultipartFile file) throws IOException {
        List<String> foundWords = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            XWPFDocument doc = new XWPFDocument(is);

            // Регулярное выражение для поиска слов вида {@text}
            Pattern pattern = Pattern.compile("\\{@(.*?)\\}");

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText();

                // Применяем регулярное выражение к тексту абзаца
                Matcher matcher = pattern.matcher(paragraphText);

                while (matcher.find()) {
                    // Добавляем найденное слово в список
                    foundWords.add("{@" + matcher.group(1) + "}");
                }
            }
        }

        return foundWords;
    }

}

