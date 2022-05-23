package com.developers.dmaker.controller;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.dto.EditDeveloper;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DMakerController {

    private final DMakerService dMakerService;

    @GetMapping("/developers")
    public List<DeveloperDto> getAllDEvelopers() {
        // GET /developers HTTP/1.1
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getAllEmployedDevelopers();
    }

    @GetMapping("/developer/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(@PathVariable String memberId) {
        // GET /developer/{memberId} HTTP/1.1
        log.info("GET /developer/{memberId} HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(@RequestBody @Valid CreateDeveloper.Request request) {

        log.info("GET /developers HTTP/1.1");
        log.info("request  {}", request);

        CreateDeveloper.Response response;
        try {
            response = dMakerService.createDeveloper(request);
        } catch (DMakerException e) {
            throw new DMakerException(e.getDMakerErrorCode(), e.getDetailMessage());
        }

        return response;
    }

    @PutMapping("/developer/{memberId}")
    public DeveloperDetailDto editDeveloper(@PathVariable String memberId, @RequestBody @Valid EditDeveloper.Request request) {
        log.info("GET /developer/{memberId} HTTP/1.1");

        return dMakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping("/developer/{memberId}")
    public DeveloperDetailDto deleteDeveloper(@PathVariable String memberId) {
        return dMakerService.deleteDeveloper(memberId);
    }
}
