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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        User user = getAuthenticatedUser();
        String code = generateQuestionsPackCode();

        List<SingleChoiceQuestion> singleChoiceQuestions = readSingleChoiceQuestions(multipartFile);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = readMultipleChoiceQuestions(multipartFile);

        if (singleChoiceQuestions.size() < 17) {
            throw new ApiException("Insufficient single choice questions");
        }
        if (multipleChoiceQuestions.size() < 12) {
            throw new ApiException("Insufficient multiple choice questions");
        }

        QuestionPack questionPack = new QuestionPack(user, questionsName, code);
        questionPack.setSingleChoiceQuestions(singleChoiceQuestions);
        questionPack.setMultipleChoiceQuestions(multipleChoiceQuestions);

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

                String questionStr = row.getCell(0).getRichStringCellValue().toString();
                Double answer = row.getCell(1).getNumericCellValue();
                Long time = Double.valueOf(row.getCell(2).getNumericCellValue()).longValue();

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

                String questionStr = row.getCell(0).getRichStringCellValue().toString();
                String choiceA = row.getCell(1).getRichStringCellValue().toString();
                String choiceB = row.getCell(2).getRichStringCellValue().toString();
                String choiceC = row.getCell(3).getRichStringCellValue().toString();
                String choiceD = row.getCell(4).getRichStringCellValue().toString();
                String answer = row.getCell(5).getRichStringCellValue().toString();
                Long time = Double.valueOf(row.getCell(6).getNumericCellValue()).longValue();

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

    public void addQuestionsToPack(String code, MultipartFile file) {
        QuestionPack pack = questionPackRepository.findByCode(code).orElseThrow(
                () -> new ApiException("Question pack not found")
        );

        User user = getAuthenticatedUser();
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
        User user = getAuthenticatedUser();

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
        User user = getAuthenticatedUser();

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

        User user = getAuthenticatedUser();
        if (!hasAccessToQuestionPack(pack, user)) {
            throw new ApiException("User doesn't have access to question pack");
        }

        GetQuestionsByCodeResponseDTO result = new GetQuestionsByCodeResponseDTO();

        List<Question> singleChoiceQuestions = new ArrayList<>(pack.getSingleChoiceQuestions());
        List<Question> multipleChoiceQuestions = new ArrayList<>(pack.getMultipleChoiceQuestions());
        result.setSingleChoiceQuestions(singleChoiceQuestions);
        result.setMultipleChoiceQuestions(multipleChoiceQuestions);

        return result;
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

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        return userService.getById(userId);
    }
}
