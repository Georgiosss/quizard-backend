package com.example.demo.service;

import com.example.demo.Utils;
import com.example.demo.model.dto.exception.ApiException;
import com.example.demo.model.dto.response.AddQuestionsResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsByCodeResponseDTO;
import com.example.demo.model.dto.response.GetQuestionsResponseDTO;
import com.example.demo.model.dto.response.ImportQuestionsResponseDTO;
import com.example.demo.model.entity.Question;
import com.example.demo.model.entity.QuestionPack;
import com.example.demo.model.entity.User;
import com.example.demo.repository.QuestionPackRepository;
import com.example.demo.repository.QuestionRepository;
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
    private UserService userService;


    public AddQuestionsResponseDTO addQuestions(String questionsName, MultipartFile multipartFile) {
        User user = getAuthenticatedUser();
        String code = generateQuestionsPackCode();

        QuestionPack questionPack = new QuestionPack(user, questionsName, code);
        List<Question> questions = readExcelFile(multipartFile);
        questionPack.setQuestions(questions);

        questionRepository.saveAll(questions);
        questionPackRepository.save(questionPack);

        return new AddQuestionsResponseDTO(code);
    }

    private List<Question> readExcelFile(MultipartFile file) {
        List<Question> questions = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) continue;

                String questionStr = row.getCell(0).getRichStringCellValue().toString();
                String answerStr = row.getCell(1).getRichStringCellValue().toString();
                Long time = Double.valueOf(row.getCell(2).getNumericCellValue()).longValue();

                Question question = new Question(questionStr, answerStr, time);
                questions.add(question);
            }

        } catch (Exception e) {
            throw new ApiException("Error while processing file");
        }

        return questions;
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

    public List<GetQuestionsByCodeResponseDTO> getQuestionsByCode(String packCode) {
        QuestionPack pack = questionPackRepository.findByCode(packCode)
                .orElseThrow(() -> new ApiException("Question pack not found!"));

        User user = getAuthenticatedUser();
        if (!hasAccessToQuestionPack(pack, user)) {
            throw new ApiException("User doesn't have access to question pack");
        }

        List<GetQuestionsByCodeResponseDTO> result = new ArrayList<>();

        for (Question question : pack.getQuestions()) {
            result.add(new GetQuestionsByCodeResponseDTO(question.getId(), question.getQuestion()));
        }

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
