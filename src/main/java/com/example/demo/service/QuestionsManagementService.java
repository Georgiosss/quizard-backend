package com.example.demo.service;

import com.example.demo.Utils;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.response.AddQuestionsResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsByCodeResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsResponseDTO;
import com.example.demo.model.dto.response.ImportQuestionsResponseDTO;
import com.example.demo.model.entity.*;
import com.example.demo.repository.QuestionPackRepository;
import com.example.demo.repository.QuestionRepository;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionsManagementService {

    @Autowired
    private QuestionPackRepository questionPackRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private UserService userService;


    public AddQuestionsResponseDTO addQuestions(String questionsName, MultipartFile multipartFile) {
        User user = userService.getAuthenticatedUser();
        String code = generateQuestionsPackCode();

        List<SingleChoiceQuestion> singleChoiceQuestions = readSingleChoiceQuestions(multipartFile);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = readMultipleChoiceQuestions(multipartFile);

        if (singleChoiceQuestions.size() < 17) {
            throw new ApiException("Insufficient single choice questions");
        }
        if (multipleChoiceQuestions.size() < 12) {
            throw new ApiException("Insufficient multiple choice questions");
        }

        QuestionPack questionPack = new QuestionPack(
                user, questionsName, code, singleChoiceQuestions, multipleChoiceQuestions
        );

        for (MultipleChoiceQuestion multipleChoiceQuestion : multipleChoiceQuestions) {
            choiceService.saveAll(multipleChoiceQuestion.getChoices());
        }
        questionRepository.saveAll(singleChoiceQuestions);
        questionRepository.saveAll(multipleChoiceQuestions);
        questionPackRepository.save(questionPack);

        return new AddQuestionsResponseDTO(code);
    }

    private List<SingleChoiceQuestion> readSingleChoiceQuestions(MultipartFile file) {
        List<SingleChoiceQuestion> questions = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) continue;

                String questionStr = getStringValue(row.getCell(0));
                Double answer = getDoubleValue(row.getCell(1));
                Long time = getDoubleValue(row.getCell(2)).longValue();

                SingleChoiceQuestion question = new SingleChoiceQuestion(questionStr, answer, time);
                questions.add(question);
            }

        } catch (Exception e) {
            throw new ApiException("Error while processing file");
        }

        return questions;
    }

    private List<MultipleChoiceQuestion> readMultipleChoiceQuestions(MultipartFile file) {
        List<MultipleChoiceQuestion> questions = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(1);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) continue;

                String questionStr = getStringValue(row.getCell(0));
                String choiceA = getStringValue(row.getCell(1));
                String choiceB = getStringValue(row.getCell(2));
                String choiceC = getStringValue(row.getCell(3));
                String choiceD = getStringValue(row.getCell(4));
                Integer answer = getDoubleValue(row.getCell(5)).intValue();
                Long time = getDoubleValue(row.getCell(6)).longValue();

                List<Choice> choices = List.of(
                        new Choice(choiceA), new Choice(choiceB),
                        new Choice(choiceC), new Choice(choiceD)
                );
                MultipleChoiceQuestion question = new MultipleChoiceQuestion(questionStr, choices, answer, time);
                questions.add(question);
            }

        } catch (Exception e) {
            throw new ApiException("Error while processing file");
        }

        return questions;
    }

    private String getStringValue(Cell c) {
        switch (c.getCellType()) {
            case STRING: {
                return c.getRichStringCellValue().toString();
            }
            case NUMERIC: {
                return Double.valueOf(c.getNumericCellValue()).toString();
            }
            default:
                return "";
        }
    }

    private Double getDoubleValue(Cell c) {
        switch (c.getCellType()) {
            case STRING: {
                return Double.valueOf(c.getRichStringCellValue().toString());
            }
            case NUMERIC: {
                return c.getNumericCellValue();
            }
            default:
                return 0D;
        }
    }

    public void addQuestionsToPack(String code, MultipartFile file) {
        QuestionPack pack = questionPackRepository.findByCode(code).orElseThrow(
                () -> new ApiException("Question pack not found")
        );

        User user = userService.getAuthenticatedUser();
        // TODO: subscribers?
        if (!user.equals(pack.getOwner())) {
            throw new ApiException("User doesn't own the pack");
        }

        List<SingleChoiceQuestion> singleChoiceQuestions = readSingleChoiceQuestions(file);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = readMultipleChoiceQuestions(file);


        for (MultipleChoiceQuestion multipleChoiceQuestion : multipleChoiceQuestions) {
            choiceService.saveAll(multipleChoiceQuestion.getChoices());
        }
        questionRepository.saveAll(singleChoiceQuestions);
        questionRepository.saveAll(multipleChoiceQuestions);

        pack.addSingleChoiceQuestions(singleChoiceQuestions);
        pack.addMultipleChoiceQuestions(multipleChoiceQuestions);
        questionPackRepository.save(pack);
    }

    public ImportQuestionsResponseDTO importQuestions(String packCode) {
        User user = userService.getAuthenticatedUser();

        QuestionPack pack = questionPackRepository.findByCode(packCode)
                .orElseThrow(() -> new ApiException("Question pack not found!"));

        if (hasAccessToQuestionPack(pack, user)) {
            throw new ApiException("User already has access to pack");
        }
        pack.addSubscriber(user);

        questionPackRepository.save(pack);

        return new ImportQuestionsResponseDTO(pack.getName());
    }

    public List<GetQuestionsResponseDTO> getQuestionPacks() {
        User user = userService.getAuthenticatedUser();

        List<GetQuestionsResponseDTO> result = new ArrayList<>();

        for (QuestionPack owned : user.getOwnedQuestionPacks()) {
            result.add(new GetQuestionsResponseDTO(owned.getName(), owned.getCode()));
        }

        for (QuestionPack imported : user.getImportedQuestionPacks()) {
            result.add(new GetQuestionsResponseDTO(imported.getName(), imported.getCode()));
        }

        return result;
    }

    public GetQuestionsByCodeResponseDTO getQuestionsByCode(String packCode) {
        QuestionPack pack = questionPackRepository.findByCode(packCode)
                .orElseThrow(() -> new ApiException("Question pack not found!"));

        User user = userService.getAuthenticatedUser();
        if (!hasAccessToQuestionPack(pack, user)) {
            throw new ApiException("User doesn't have access to question pack");
        }

        List<Question> singleChoiceQuestions = new ArrayList<>(pack.getSingleChoiceQuestions());
        List<Question> multipleChoiceQuestions = new ArrayList<>(pack.getMultipleChoiceQuestions());

        return new GetQuestionsByCodeResponseDTO(singleChoiceQuestions, multipleChoiceQuestions);
    }

    private boolean hasAccessToQuestionPack(QuestionPack pack, User user) {
        if (user.equals(pack.getOwner())) return true;

        for (User subscriber : pack.getSubscribers()) {
            if (user.equals(subscriber)) return true;
        }

        return false;
    }

    private String generateQuestionsPackCode() {
        String code;

        do {
            code = Utils.generateRandomCode(6);
        } while (questionPackRepository.existsByCode(code));

        return code;
    }

}
