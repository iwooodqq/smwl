package org.example.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.admin.service.GroupService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
}
